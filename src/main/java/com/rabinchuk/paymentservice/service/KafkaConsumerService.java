package com.rabinchuk.paymentservice.service;

import com.rabinchuk.paymentservice.dto.OrderCreatedEvent;
import com.rabinchuk.paymentservice.dto.PaymentRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order-created-topic", groupId = "payment-service-group")
    public void handleOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        log.info("Received OrderCreatedEvent {}", orderCreatedEvent);
        try {
            PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                    .orderId(orderCreatedEvent.orderId())
                    .userId(orderCreatedEvent.userId())
                    .paymentAmount(orderCreatedEvent.paymentAmount())
                    .build();
            paymentService.createPayment(paymentRequestDto);
            log.info("Payment request has been created for order {}", orderCreatedEvent.orderId());
        }  catch (Exception e) {
            log.error("Exception occurred while processing OrderCreatedEvent {}", orderCreatedEvent, e);
        }
    }
}
