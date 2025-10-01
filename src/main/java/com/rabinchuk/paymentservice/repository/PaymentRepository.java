package com.rabinchuk.paymentservice.repository;

import com.rabinchuk.paymentservice.dto.TotalSumDto;
import com.rabinchuk.paymentservice.model.Payment;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findAllByOrderId(Long orderId);

    List<Payment> findAllByUserId(Long userId);

    List<Payment> findAllByStatusIn(List<PaymentStatus> statuses);

    @Aggregation( pipeline = {
            "{ 'match': { 'timestamp': { '$gte': ?0, '$lte': ?1 }, 'status': 'SUCCESS'} }",
            "{ 'group': { '_id': null, 'totalSum': { '$sum': '$payment_Amount' } } }"
    })
    TotalSumDto findTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate);

}
