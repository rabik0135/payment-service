package com.rabinchuk.paymentservice.mapper;

import com.rabinchuk.paymentservice.dto.OrderCreatedEvent;
import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "timestamp", expression = "java(getCurrentDateTime())")
    Payment toEntity(OrderCreatedEvent orderCreatedEvent);

    PaymentResponseDto toDto(Payment payment);

    default LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

}
