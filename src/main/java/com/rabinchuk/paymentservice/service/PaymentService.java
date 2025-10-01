package com.rabinchuk.paymentservice.service;

import com.rabinchuk.paymentservice.dto.PaymentRequestDto;
import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.dto.TotalSumDto;
import com.rabinchuk.paymentservice.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto);

    List<PaymentResponseDto> getPaymentsByOrderId(Long orderId);

    List<PaymentResponseDto> getPaymentsByUserId(Long userId);

    List<PaymentResponseDto> getPaymentsByStatuses(List<PaymentStatus> statuses);

    TotalSumDto getTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate);

}
