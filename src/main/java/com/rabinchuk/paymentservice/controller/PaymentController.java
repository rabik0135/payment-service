package com.rabinchuk.paymentservice.controller;


import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.dto.TotalAmountDto;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import com.rabinchuk.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments(@RequestParam(required = false) Long orderId,
                                                                   @RequestParam(required = false) Long userId,
                                                                   @RequestParam(required = false) PaymentStatus status) {
        List<PaymentResponseDto> paymentResponseDtoList = paymentService.getPayments(orderId, userId, status);
        return ResponseEntity.ok(paymentResponseDtoList);
    }

    @GetMapping("/total-amount")
    public ResponseEntity<TotalAmountDto> getTotalSumOfPaymentsForDatePeriod(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        TotalAmountDto totalAmountDto = paymentService.getTotalSumOfPaymentsForDatePeriod(startDate, endDate);
        return ResponseEntity.ok(totalAmountDto);
    }

}
