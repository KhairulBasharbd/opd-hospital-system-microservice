package com.ztrios.opd_billing_service.service;

import com.ztrios.opd_billing_service.repository.InvoiceDocumentRepository;
import com.ztrios.opd_billing_service.dto.BillingServiceRequest;
import com.ztrios.opd_billing_service.dto.BillingServiceResponse;
import com.ztrios.opd_billing_service.dto.PaymentRequest;
import com.ztrios.opd_billing_service.dto.PaymentSuccessEvent;
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
import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final InvoiceDocumentRepository invoiceRepository;
    private final BillingEventProducer eventProducer;

//    private static final Double TAX_RATE = 0.05; // 5% tax
//    private static final Double DISCOUNT = 0.0; // Default no discount


    //    Auto invoice generation (SYNC)
    public BillingServiceResponse createInvoice(BillingServiceRequest request) {

        invoiceRepository.findByAppointmentId(request.appointmentId())
                .ifPresent(inv -> {
                    throw new PaymentAlreadyDoneException("Invoice already exists");
                });

        BigDecimal baseFee = BigDecimal.valueOf(500);
        BigDecimal tax = BigDecimal.valueOf(50);

//        // Fetch external details
//        DoctorClient.DoctorDetailsResponse doctorDetails = doctorClient.getDoctorDetails(request.doctorId());
//        AuthClient.PatientDetailsResponse patientDetails = authClient.getPatientDetails(request.patientId());
//
//        // Calculate amounts
//        Double baseFee = doctorDetails.consultationFee();
//        Double tax = baseFee * TAX_RATE;
//        Double totalAmount = baseFee + tax - DISCOUNT;

        InvoiceDocument invoice = invoiceRepository.save(
                InvoiceDocument.builder()
                        .id(UUID.randomUUID())
                        .appointmentId(request.appointmentId())
                        .patientUserId(request.patientUserId())
                        .doctorId(request.doctorId())
                        .baseFee(baseFee)
                        .tax(tax)
                        .discount(BigDecimal.ZERO)
                        .totalAmount(baseFee.add(tax))
                        .status(InvoiceStatus.UNPAID)
                        .createdAt(Instant.now())
                        .build()
        );

        String paymentLink = "https://pay.opd.com/pay/" + invoice.getId();

        return new BillingServiceResponse(invoice.getId(), paymentLink);
    }


    // Simulated payment callback
    public void payInvoice(String invoiceId, PaymentRequest request) {

        InvoiceDocument invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new PaymentAlreadyDoneException("Invoice already paid");
        }

        PaymentDetails savePayment = new PaymentDetails(invoice.getId(), request.paymentMethod(), UUID.randomUUID().toString(), invoice.getTotalAmount(), Instant.now() );

        //invoiceRepository.save(InvoiceDocument.builder().payment(savePayment).build());
        invoice.setPayment(savePayment);
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        eventProducer.publishPaymentSuccess(
                new PaymentSuccessEvent(
                        invoice.getAppointmentId(),
                        invoice.getId(),
                        Instant.now()
                )
        );
    }


    //get all invoices of a patient
    public List<InvoiceDocument> getPatientInvoices(UUID patientId) {
        return invoiceRepository.findByPatientUserId(patientId);
    }
}

