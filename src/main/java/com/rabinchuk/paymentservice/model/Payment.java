package com.rabinchuk.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;

    @Indexed
    @Field("order_id")
    private Long orderId;

    @Indexed
    @Field("user_id")
    private Long userId;

    @Indexed
    private PaymentStatus status;

    @Indexed
    private LocalDateTime timestamp;

    @Field(value = "payment_amount", targetType = FieldType.DECIMAL128)
    private BigDecimal paymentAmount;

}
