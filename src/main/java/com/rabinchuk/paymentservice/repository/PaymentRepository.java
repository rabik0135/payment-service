package com.rabinchuk.paymentservice.repository;

import com.rabinchuk.paymentservice.dto.TotalAmountDto;
import com.rabinchuk.paymentservice.model.Payment;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String>, FilterPaymentRepository {

    @Aggregation(pipeline = {
            "{ '$match': { 'timestamp': { '$gte': ?0, '$lte': ?1 }, 'status': 'SUCCESS'} }",
            "{ '$group': { '_id': null, 'totalAmount': { '$sum': '$payment_amount' } } }"
    })
    TotalAmountDto findTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate);

}
