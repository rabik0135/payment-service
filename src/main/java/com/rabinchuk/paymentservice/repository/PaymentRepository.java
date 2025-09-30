package com.rabinchuk.paymentservice.repository;

import com.rabinchuk.paymentservice.model.Payment;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findAllByOrderId(Long orderId);

    List<Payment> findAllByUserId(Long userId);

    List<Payment> findAllByStatusIn(List<PaymentStatus> statuses);

    @Query(value = "{ 'timestamp': { $gte: ?0, $lte: ?1 }, 'status': 'SUCCESS' }")
    List<Payment> findTotalSumOfPaymentsForDatePeriod(LocalDate startDate, LocalDate endDate);

}
