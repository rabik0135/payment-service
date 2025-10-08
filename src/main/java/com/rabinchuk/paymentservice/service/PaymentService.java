package com.rabinchuk.paymentservice.service;

import com.rabinchuk.paymentservice.dto.OrderCreatedEvent;
import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.dto.TotalAmountDto;
import com.rabinchuk.paymentservice.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    void createPayment(OrderCreatedEvent orderCreatedEvent);

    TotalAmountDto getTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate);

    List<PaymentResponseDto> getPayments(Long orderId, Long userId, PaymentStatus status);

}
