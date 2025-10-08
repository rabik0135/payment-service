package com.rabinchuk.paymentservice.outbox;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxPaymentsRepository extends MongoRepository<OutboxPayments, String> {

    List<OutboxPayments> findAllByStatus(EventStatus status);

}
