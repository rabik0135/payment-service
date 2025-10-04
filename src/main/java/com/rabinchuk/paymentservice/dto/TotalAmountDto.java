package com.rabinchuk.paymentservice.dto;

import java.math.BigDecimal;

public record TotalAmountDto(
        BigDecimal totalAmount
) {
}
