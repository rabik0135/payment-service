package com.rabinchuk.paymentservice.service;

import com.rabinchuk.paymentservice.client.ExternalApiClient;
import com.rabinchuk.paymentservice.dto.PaymentRequestDto;
import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.dto.TotalSumDto;
import com.rabinchuk.paymentservice.mapper.PaymentMapper;
import com.rabinchuk.paymentservice.model.Payment;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import com.rabinchuk.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ExternalApiClient externalApiClient;

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        PaymentStatus status = getPaymentStatus();

        Payment payment = Payment.builder()
                .orderId(paymentRequestDto.orderId())
                .userId(paymentRequestDto.userId())
                .status(status)
                .timestamp(LocalDateTime.now())
                .paymentAmount(paymentRequestDto.paymentAmount())
                .build();

        Payment createdPayment = paymentRepository.save(payment);

        return paymentMapper.toDto(createdPayment);
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findAllByOrderId(orderId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByUserId(Long userId) {
        return paymentRepository.findAllByUserId(userId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByStatuses(List<PaymentStatus> statuses) {
        return paymentRepository.findAllByStatusIn(statuses).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public TotalSumDto getTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        TotalSumDto totalSumDto = paymentRepository.findTotalSumOfPaymentsForDatePeriod(startDate, endDate);
        if (totalSumDto == null || totalSumDto.totalSum() == null) {
            return new TotalSumDto(BigDecimal.ZERO);
        }
        return totalSumDto;
    }

    private PaymentStatus getPaymentStatus(){
        int number = externalApiClient.getRandomNumber().getFirst();
        return (number % 2 == 0) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }

}
