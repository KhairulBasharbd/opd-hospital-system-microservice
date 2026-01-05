package com.ztrios.opd_billing_service.dto;


import com.ztrios.opd_billing_service.enums.PaymentMethod;

public record PaymentRequest(PaymentMethod paymentMethod) {}
