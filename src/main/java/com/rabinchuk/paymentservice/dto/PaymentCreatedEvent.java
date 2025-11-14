package com.rabinchuk.paymentservice.dto;

import com.rabinchuk.paymentservice.model.PaymentStatus;
import lombok.Builder;

@Builder
public record PaymentCreatedEvent(
        Long orderId,

        PaymentStatus status
) {
}
