package com.rabinchuk.paymentservice.controller;


import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.dto.TotalAmountDto;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import com.rabinchuk.paymentservice.service.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceImpl paymentServiceImpl;

    @GetMapping("/byOrderId/{orderId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByOrderId(@PathVariable Long orderId) {
        List<PaymentResponseDto> paymentResponseDtoList = paymentServiceImpl.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(paymentResponseDtoList);
    }

    @GetMapping("/byUserId/{userId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByUserId(@PathVariable Long userId) {
        List<PaymentResponseDto> paymentResponseDtoList = paymentServiceImpl.getPaymentsByUserId(userId);
        return ResponseEntity.ok(paymentResponseDtoList);
    }

    @GetMapping(value = "/byStatus/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<PaymentResponseDto> paymentResponseDtoList = paymentServiceImpl.getPaymentsByStatus(status);
        return ResponseEntity.ok(paymentResponseDtoList);
    }

    @GetMapping("/byTime")
    public ResponseEntity<TotalAmountDto> getTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        TotalAmountDto totalAmountDto = paymentServiceImpl.getTotalSumOfPaymentsForDatePeriod(startDate, endDate);
        return ResponseEntity.ok(totalAmountDto);
    }

}
