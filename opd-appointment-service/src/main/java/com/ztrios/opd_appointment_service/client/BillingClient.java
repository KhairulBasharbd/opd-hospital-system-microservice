package com.ztrios.opd_appointment_service.client;

import com.ztrios.opd_appointment_service.dto.BillingServiceRequest;
import com.ztrios.opd_appointment_service.dto.BillingServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "opd-billing-service")
public interface BillingClient {

    @PostMapping("/internal/billing/invoices")
    BillingServiceResponse createInvoice(@RequestBody BillingServiceRequest request);

}
