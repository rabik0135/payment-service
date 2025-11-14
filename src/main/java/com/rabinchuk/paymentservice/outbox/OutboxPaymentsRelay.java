package com.rabinchuk.paymentservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabinchuk.paymentservice.dto.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPaymentsRelay {

    private final OutboxPaymentsRepository outboxPaymentsRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 10000)
    @SneakyThrows
    public void processEvents() {
        List<OutboxPayments> outboxPayments = outboxPaymentsRepository.findAllByStatus(EventStatus.PENDING);
        if (outboxPayments.isEmpty()) {
            return;
        }

        for (OutboxPayments event : outboxPayments) {
            PaymentCreatedEvent paymentCreatedEvent = objectMapper.readValue(event.getPayload(), PaymentCreatedEvent.class);
            kafkaTemplate.send(event.getTopic(), paymentCreatedEvent);
            event.setStatus(EventStatus.PROCESSED);
            outboxPaymentsRepository.save(event);
        }
    }

}
