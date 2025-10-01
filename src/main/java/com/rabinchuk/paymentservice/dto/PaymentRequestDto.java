package com.rabinchuk.paymentservice.dto;

import java.math.BigDecimal;

public record PaymentRequestDto(

        Long orderId,

        Long userId,

        BigDecimal paymentAmount
) {
}
