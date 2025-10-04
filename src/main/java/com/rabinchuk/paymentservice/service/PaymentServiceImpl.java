package com.rabinchuk.paymentservice.service;

import com.rabinchuk.paymentservice.client.ExternalApiClient;
import com.rabinchuk.paymentservice.dto.PaymentCreatedEvent;
import com.rabinchuk.paymentservice.dto.PaymentRequestDto;
import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.dto.TotalAmountDto;
import com.rabinchuk.paymentservice.mapper.PaymentMapper;
import com.rabinchuk.paymentservice.model.Payment;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import com.rabinchuk.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;

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

        PaymentCreatedEvent paymentCreatedEvent = PaymentCreatedEvent.builder()
                .orderId(paymentRequestDto.orderId())
                .status(status)
                .build();
        kafkaTemplate.send("payment-created-topic", paymentCreatedEvent);
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
    public List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus paymentStatus) {
        return paymentRepository.findAllByStatus(paymentStatus).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public TotalAmountDto getTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        TotalAmountDto totalAmountDto = paymentRepository.findTotalSumOfPaymentsForDatePeriod(startDate, endDate);
        if (totalAmountDto == null || totalAmountDto.totalAmount() == null) {
            return new TotalAmountDto(BigDecimal.ZERO);
        }
        return totalAmountDto;
    }

    private PaymentStatus getPaymentStatus(){
        int number = externalApiClient.getRandomNumber().getFirst();
        return (number % 2 == 0) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }

}
