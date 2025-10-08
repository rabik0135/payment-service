package com.rabinchuk.paymentservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabinchuk.paymentservice.dto.PaymentCreatedEvent;
import com.rabinchuk.paymentservice.model.Payment;
import com.rabinchuk.paymentservice.outbox.OutboxPayments;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OutboxPaymentsMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "paymentId", source = "createdPayment.id")
    @Mapping(target = "topic", constant = "payment-created-topic")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(
            target = "payload",
            expression = "java(objectMapper.writeValueAsString(paymentCreatedEvent))"
    )
    OutboxPayments toEntity(Payment createdPayment,
                            PaymentCreatedEvent paymentCreatedEvent,
                            @Context ObjectMapper objectMapper) throws JsonProcessingException;
}
