package com.rabinchuk.paymentservice.service;

import com.rabinchuk.paymentservice.AbstractIntegrationTest;
import com.rabinchuk.paymentservice.dto.OrderCreatedEvent;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import com.rabinchuk.paymentservice.outbox.EventStatus;
import com.rabinchuk.paymentservice.outbox.OutboxPaymentsRepository;
import com.rabinchuk.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class KafkaConsumerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OutboxPaymentsRepository outboxPaymentsRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        outboxPaymentsRepository.deleteAll();
        wireMock.resetAll();
    }

    @Test
    void shouldCreateSuccessfulPaymentFromKafkaEvent() {
        wireMock.stubFor(get(urlPathEqualTo("/api/v1.0/random"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[10]") // Четное число -> SUCCESS
                        .withStatus(200)));

        OrderCreatedEvent event = new OrderCreatedEvent(1L, 101L, new BigDecimal("199.99"));

        kafkaTemplate.send("order-created-topic", event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            var payments = paymentRepository.findAll();
            assertThat(payments).hasSize(1);
            var payment = payments.getFirst();
            assertThat(payment.getOrderId()).isEqualTo(event.orderId());
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
            assertThat(payment.getPaymentAmount()).isEqualByComparingTo("199.99");

            var outboxEvents = outboxPaymentsRepository.findAll();
            assertThat(outboxEvents).hasSize(1);
            var outboxEvent = outboxEvents.getFirst();
            assertThat(outboxEvent.getPaymentId()).isEqualTo(payment.getId());
            assertThat(outboxEvent.getStatus()).isEqualTo(EventStatus.PENDING);
        });
    }

}
