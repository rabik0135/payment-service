package com.rabinchuk.paymentservice.mapper;

import com.rabinchuk.paymentservice.dto.PaymentRequestDto;
import com.rabinchuk.paymentservice.dto.PaymentResponseDto;
import com.rabinchuk.paymentservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Payment toEntity(PaymentRequestDto paymentRequestDto);

    PaymentResponseDto toDto(Payment payment);

}
