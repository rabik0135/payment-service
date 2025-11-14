package com.rabinchuk.paymentservice.mapper;

import com.rabinchuk.paymentservice.dto.OrderCreatedEvent;
import com.rabinchuk.paymentservice.dto.PaymentCreatedEvent;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "orderId", source = "orderCreatedEvent.orderId")
    PaymentCreatedEvent toPaymentCreatedEvent(OrderCreatedEvent orderCreatedEvent, PaymentStatus status);
}
