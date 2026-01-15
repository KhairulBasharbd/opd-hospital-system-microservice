package com.ztrios.opd_appointment_service.client.billingClient;

import com.ztrios.opd_appointment_service.dto.BillingServiceRequest;
import com.ztrios.opd_appointment_service.dto.BillingServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "opd-billing-service", configuration = BillingFeignConfig.class)
public interface BillingClient {

    @PostMapping("/api/billing/invoice")
    BillingServiceResponse createInvoice(@RequestBody BillingServiceRequest request);

}
