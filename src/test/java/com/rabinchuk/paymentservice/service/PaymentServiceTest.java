package com.rabinchuk.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabinchuk.paymentservice.client.ExternalApiClient;
import com.rabinchuk.paymentservice.dto.OrderCreatedEvent;
import com.rabinchuk.paymentservice.dto.PaymentCreatedEvent;
import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.dto.TotalAmountDto;
import com.rabinchuk.paymentservice.mapper.EventMapper;
import com.rabinchuk.paymentservice.mapper.OutboxPaymentsMapper;
import com.rabinchuk.paymentservice.mapper.PaymentMapper;
import com.rabinchuk.paymentservice.model.Payment;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import com.rabinchuk.paymentservice.outbox.OutboxPayments;
import com.rabinchuk.paymentservice.outbox.OutboxPaymentsRepository;
import com.rabinchuk.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OutboxPaymentsRepository outboxPaymentsRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private OutboxPaymentsMapper outboxPaymentsMapper;

    @Mock
    private ExternalApiClient externalApiClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    OrderCreatedEvent orderCreatedEvent;
    Payment payment;

    @BeforeEach
    public void setUp() {
        orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(1L)
                .userId(1L)
                .paymentAmount(BigDecimal.TEN)
                .build();

        payment = Payment.builder()
                .id("paymentId")
                .orderId(1L)
                .userId(1L)
                .paymentAmount(BigDecimal.TEN)
                .build();
    }

    @Test
    public void createPayment_whenRandomNumberIsEven_ShouldReturnSuccessPayment() throws JsonProcessingException {
        when(externalApiClient.getRandomNumber()).thenReturn(List.of(2));
        when(paymentMapper.toEntity(any(OrderCreatedEvent.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent(1L, PaymentStatus.SUCCESS);

        when(eventMapper.toPaymentCreatedEvent(any(OrderCreatedEvent.class), any(PaymentStatus.class))).thenReturn(paymentCreatedEvent);

        OutboxPayments outboxEvent = OutboxPayments.builder()
                .id("event")
                .build();
        when(outboxPaymentsMapper.toEntity(any(Payment.class), any(PaymentCreatedEvent.class), any(ObjectMapper.class)))
                .thenReturn(outboxEvent);
        paymentService.createPayment(orderCreatedEvent);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);

        ArgumentCaptor<OutboxPayments> outboxCaptor = ArgumentCaptor.forClass(OutboxPayments.class);
        verify(outboxPaymentsRepository, times(1)).save(outboxCaptor.capture());

        verify(externalApiClient, times(1)).getRandomNumber();
    }

    @Test
    public void createPayment_whenRandomNumberIsOdd_ShouldReturnFailedPayment() throws JsonProcessingException {
        when(externalApiClient.getRandomNumber()).thenReturn(List.of(1));
        when(paymentMapper.toEntity(any(OrderCreatedEvent.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent(1L, PaymentStatus.FAILED);

        when(eventMapper.toPaymentCreatedEvent(any(OrderCreatedEvent.class), any(PaymentStatus.class))).thenReturn(paymentCreatedEvent);

        OutboxPayments outboxEvent = OutboxPayments.builder()
                .id("event")
                .build();
        when(outboxPaymentsMapper.toEntity(any(Payment.class), any(PaymentCreatedEvent.class), any(ObjectMapper.class)))
                .thenReturn(outboxEvent);
        paymentService.createPayment(orderCreatedEvent);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);

        ArgumentCaptor<OutboxPayments> outboxCaptor = ArgumentCaptor.forClass(OutboxPayments.class);
        verify(outboxPaymentsRepository, times(1)).save(outboxCaptor.capture());

        verify(externalApiClient, times(1)).getRandomNumber();
    }

    @Test
    public void getTotalSumOfPaymentsForDatePeriod_whenPaymentsExists_ShouldReturnTotalAmount() {
        TotalAmountDto expectedDto = new TotalAmountDto(BigDecimal.TEN);
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(paymentRepository.findTotalSumOfPaymentsForDatePeriod(startDate, endDate)).thenReturn(expectedDto);

        TotalAmountDto result = paymentService.getTotalSumOfPaymentsForDatePeriod(startDate, endDate);

        assertThat(result).isEqualTo(expectedDto);
        verify(paymentRepository, times(1)).findTotalSumOfPaymentsForDatePeriod(startDate, endDate);
    }

    @Test
    public void getTotalSumOfPaymentsForDatePeriod_whenRepositoryReturnsNull_ShouldReturnZero() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(paymentRepository.findTotalSumOfPaymentsForDatePeriod(startDate, endDate)).thenReturn(null);

        TotalAmountDto result = paymentService.getTotalSumOfPaymentsForDatePeriod(startDate, endDate);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(new TotalAmountDto(BigDecimal.ZERO));
        verify(paymentRepository, times(1)).findTotalSumOfPaymentsForDatePeriod(startDate, endDate);
    }

    @Test
    public void getTotalSumOfPaymentsForDatePeriod_whenDtoAmountIsNull_ShouldReturnZero() {
        TotalAmountDto zeroAmountDto = new TotalAmountDto(BigDecimal.ZERO);
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(paymentRepository.findTotalSumOfPaymentsForDatePeriod(startDate, endDate)).thenReturn(zeroAmountDto);

        TotalAmountDto result = paymentService.getTotalSumOfPaymentsForDatePeriod(startDate, endDate);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(zeroAmountDto);
        verify(paymentRepository, times(1)).findTotalSumOfPaymentsForDatePeriod(startDate, endDate);
    }

    @Test
    public void getPayments_whenPaymentsFound_shouldReturnListOfDtos() {
        Long orderId = 1L;
        PaymentResponseDto paymentResponseDto = PaymentResponseDto.builder()
                .orderId(orderId)
                .userId(1L)
                .status(PaymentStatus.SUCCESS)
                .paymentAmount(BigDecimal.TEN)
                .build();

        when(paymentRepository.findPayments(orderId, null, PaymentStatus.SUCCESS)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentResponseDto);

        List<PaymentResponseDto> result = paymentService.getPayments(orderId, null, PaymentStatus.SUCCESS);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(paymentResponseDto);
        verify(paymentRepository, times(1)).findPayments(orderId, null, PaymentStatus.SUCCESS);
        verify(paymentMapper, times(1)).toDto(payment);
    }

    @Test
    public void getPayments_whenPaymentsNotFound_shouldReturnEmptyList() {
        Long orderId = 999L;
        when(paymentRepository.findPayments(orderId, null, null)).thenReturn(List.of());

        List<PaymentResponseDto> result = paymentService.getPayments(orderId, null, null);
        assertThat(result).isEmpty();
        verify(paymentRepository, times(1)).findPayments(orderId, null, null);
    }

}
