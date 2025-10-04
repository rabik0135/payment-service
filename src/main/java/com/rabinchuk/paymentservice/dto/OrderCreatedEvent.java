package com.rabinchuk.paymentservice.dto;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,

        Long userId,

        BigDecimal paymentAmount
) {
}
