package com.rabinchuk.paymentservice.service;

import com.rabinchuk.paymentservice.dto.PaymentRequestDto;
import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.dto.TotalAmountDto;
import com.rabinchuk.paymentservice.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto);

    List<PaymentResponseDto> getPaymentsByOrderId(Long orderId);

    List<PaymentResponseDto> getPaymentsByUserId(Long userId);

    List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus paymentStatus);

    TotalAmountDto getTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate);

}
