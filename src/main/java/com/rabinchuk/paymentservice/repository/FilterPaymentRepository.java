package com.rabinchuk.paymentservice.repository;

import com.rabinchuk.paymentservice.model.Payment;
import com.rabinchuk.paymentservice.model.PaymentStatus;

import java.util.List;

public interface FilterPaymentRepository {

    List<Payment> findPayments(Long orderId, Long userId, PaymentStatus status);

}
