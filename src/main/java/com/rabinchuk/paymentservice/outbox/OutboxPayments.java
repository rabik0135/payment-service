package com.rabinchuk.paymentservice.outbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "outbox_payments")
public class OutboxPayments {
    @Id
    private String id;

    @Field(name = "payment_id")
    private String paymentId;

    private String topic;

    private String payload;

    private EventStatus status;

    private LocalDateTime createdAt;
}
