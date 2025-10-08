package com.rabinchuk.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabinchuk.paymentservice.client.ExternalApiClient;
import com.rabinchuk.paymentservice.dto.OrderCreatedEvent;
import com.rabinchuk.paymentservice.dto.PaymentCreatedEvent;
import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.dto.TotalAmountDto;
import com.rabinchuk.paymentservice.mapper.OutboxPaymentsMapper;
import com.rabinchuk.paymentservice.mapper.PaymentMapper;
import com.rabinchuk.paymentservice.outbox.OutboxPayments;
import com.rabinchuk.paymentservice.model.Payment;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import com.rabinchuk.paymentservice.outbox.OutboxPaymentsRepository;
import com.rabinchuk.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxPaymentsRepository outboxPaymentsRepository;
    private final PaymentMapper paymentMapper;
    private final OutboxPaymentsMapper outboxPaymentsMapper;
    private final ExternalApiClient externalApiClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    @SneakyThrows
    public void createPayment(OrderCreatedEvent orderCreatedEvent) {
        PaymentStatus status = getPaymentStatus();

        Payment payment = paymentMapper.toEntity(orderCreatedEvent);
        payment.setStatus(status);

        Payment createdPayment = paymentRepository.save(payment);

        PaymentCreatedEvent paymentCreatedEvent = PaymentCreatedEvent.builder()
                .orderId(orderCreatedEvent.orderId())
                .status(status)
                .build();

        OutboxPayments event = outboxPaymentsMapper.toEntity(createdPayment, paymentCreatedEvent, objectMapper);
        outboxPaymentsRepository.save(event);
    }

    @Override
    public TotalAmountDto getTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        TotalAmountDto totalAmountDto = paymentRepository.findTotalSumOfPaymentsForDatePeriod(startDate, endDate);
        if (totalAmountDto == null || totalAmountDto.totalAmount() == null) {
            return new TotalAmountDto(BigDecimal.ZERO);
        }
        return totalAmountDto;
    }

    @Override
    public List<PaymentResponseDto> getPayments(Long orderId, Long userId, PaymentStatus status) {
        List<Payment> payments = paymentRepository.findPayments(orderId, userId, status);
        return payments.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    private PaymentStatus getPaymentStatus(){
        int number = externalApiClient.getRandomNumber().getFirst();
        return (number % 2 == 0) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }

}
