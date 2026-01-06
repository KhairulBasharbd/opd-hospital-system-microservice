package com.ztrios.opd_billing_service.controller;


import com.ztrios.opd_billing_service.dto.BillingServiceRequest;
import com.ztrios.opd_billing_service.dto.BillingServiceResponse;
import com.ztrios.opd_billing_service.dto.PaymentRequest;
import com.ztrios.opd_billing_service.entity.InvoiceDocument;
import com.ztrios.opd_billing_service.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/invoice")
    public BillingServiceResponse createInvoice(
            @RequestBody BillingServiceRequest request
    ) {
        return billingService.createInvoice(request);
    }

    /** Simulated payment gateway */
    @PostMapping("/pay/{invoiceId}")
    public ResponseEntity<?> pay(
            @PathVariable UUID invoiceId,
            @RequestBody PaymentRequest request
    ) {
        billingService.payInvoice(invoiceId, request);
        return ResponseEntity.ok(Map.of("message", "Payment Successful"));
    }

    /** Payment history */
    @GetMapping("/patient/{patientId}")
    public List<InvoiceDocument> history(@PathVariable UUID patientId) {
        return billingService.getPatientInvoices(patientId);
    }
}
