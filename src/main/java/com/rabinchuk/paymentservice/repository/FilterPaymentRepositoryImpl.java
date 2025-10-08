package com.rabinchuk.paymentservice.repository;

import com.rabinchuk.paymentservice.model.Payment;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilterPaymentRepositoryImpl implements FilterPaymentRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Payment> findPayments(Long orderId, Long userId, PaymentStatus status) {
        final Query query = new Query();
        final List<Criteria> criteria = new ArrayList<>();

        if (orderId != null) {
            criteria.add(Criteria.where("orderId").is(orderId));
        }
        if (userId != null) {
            criteria.add(Criteria.where("userId").is(userId));
        }
        if (status != null) {
            criteria.add(Criteria.where("status").is(status));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new  Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Payment.class);
    }

}
