package com.ztrios.opd_billing_service.entity;


import com.ztrios.opd_billing_service.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDocument {

    @Id
    private UUID id;

    private UUID appointmentId;
    private UUID patientUserId;
    private UUID doctorId;
    private UUID scheduleId;
    private LocalDate appointmentDate;

    private String doctorName;
    private String patientName;
    private String patientPhone;

    private BigDecimal baseFee;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal totalAmount;


    private InvoiceStatus status;

    // ✅ Embed the payment (1-to-1)
    private PaymentDetails payment;

    private Instant createdAt;

    // Optional: helper method
    public boolean isPaid() {
        return status == InvoiceStatus.PAID && payment != null;
    }
}