package com.ztrios.opd_billing_service.service;

import com.ztrios.opd_billing_service.client.appointmentClient.AppointmentClient;
import com.ztrios.opd_billing_service.client.authClient.AuthClient;
import com.ztrios.opd_billing_service.client.doctorClient.DoctorClient;
import com.ztrios.opd_billing_service.dto.*;
import com.ztrios.opd_billing_service.enums.AppointmentStatus;
import com.ztrios.opd_billing_service.exception.custom.TotalScheduleFullException;
import com.ztrios.opd_billing_service.repository.InvoiceDocumentRepository;
import com.ztrios.opd_billing_service.entity.InvoiceDocument;
import com.ztrios.opd_billing_service.entity.PaymentDetails;
import com.ztrios.opd_billing_service.enums.InvoiceStatus;
import com.ztrios.opd_billing_service.exception.custom.InvoiceNotFoundException;
import com.ztrios.opd_billing_service.exception.custom.PaymentAlreadyDoneException;
import com.ztrios.opd_billing_service.kafka.BillingEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final InvoiceDocumentRepository invoiceRepository;
    private final BillingEventProducer eventProducer;
    private final DoctorClient doctorClient;
    private final AppointmentClient appointmentClient;
    private final AuthClient authClient;

    private static final BigDecimal TAX_RATE = new BigDecimal(0.05); // 5% tax
    private static final BigDecimal DISCOUNT = new BigDecimal(0.0);


    //    Auto invoice generation (SYNC)
    public BillingServiceResponse createInvoice(BillingServiceRequest request) {

        invoiceRepository.findByAppointmentId(request.appointmentId())
                .ifPresent(inv -> {
                    throw new PaymentAlreadyDoneException("Invoice already exists");
                });



        // Fetch external details
        DoctorResponse doctorDetails = doctorClient.getDoctorDetails(request.doctorId());
        PatientProfileDetails patientDetails = authClient.getPatientSummary(request.patientUserId());

//        // Calculate amounts
        BigDecimal baseFee = doctorDetails.consultationFee();
        BigDecimal tax = baseFee.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);



        InvoiceDocument invoice = invoiceRepository.save(
                InvoiceDocument.builder()
                        .id(UUID.randomUUID())
                        .appointmentId(request.appointmentId())
                        .patientUserId(request.patientUserId())
                        .doctorId(request.doctorId())
                        .scheduleId(request.scheduleId())
                        .appointmentDate(request.appointmentDate())
                        .doctorName(doctorDetails.doctorName())
                        .patientName(patientDetails.fullName())
                        .patientPhone(patientDetails.phone())
                        .baseFee(baseFee)
                        .tax(tax)
                        .discount(BigDecimal.ZERO)
                        .totalAmount(baseFee.add(tax).subtract(DISCOUNT))
                        .status(InvoiceStatus.UNPAID)
                        .createdAt(Instant.now())
                        .build()
        );
        log.debug("Invoice save {}", invoice.toString());

        String paymentLink = "localhost:8084/api/billing/pay/" + invoice.getId();

        return new BillingServiceResponse(invoice.getId(), paymentLink);
    }


    // Simulated payment callback
    public void payInvoice(UUID invoiceId, PaymentRequest request) {

        InvoiceDocument invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new PaymentAlreadyDoneException("Invoice already paid");
        }

        Integer currentConfirmAppointments = appointmentClient.countConfirmedAppointments(invoice.getDoctorId(), invoice.getScheduleId(), invoice.getAppointmentDate(), AppointmentStatus.CONFIRMED);

        ScheduleResponse scheduleDetails = doctorClient.getScheduleDetails(invoice.getScheduleId());
        Integer totalAppointments = scheduleDetails.maxPatients();

        if(currentConfirmAppointments >= scheduleDetails.maxPatients()){
            throw new TotalScheduleFullException("This Schedule is already full, plz!! try another doctor or schedule!!");
        }

        PaymentDetails savePayment = new PaymentDetails(invoice.getId(), request.paymentMethod(), UUID.randomUUID().toString(), invoice.getTotalAmount(), Instant.now() );

        invoice.setPayment(savePayment);
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        eventProducer.publishPaymentSuccess(
                new PaymentSuccessEvent(
                        invoice.getAppointmentId(),
                        invoice.getId(),
                        invoice.getTotalAmount(),
                        Instant.now()
                )
        );
    }


    //get all invoices of a patient
    public List<InvoiceDocument> getPatientInvoices(UUID patientId) {
        return invoiceRepository.findByPatientUserId(patientId);
    }
}

