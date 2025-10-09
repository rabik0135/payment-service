package com.rabinchuk.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabinchuk.paymentservice.AbstractIntegrationTest;
import com.rabinchuk.paymentservice.model.Payment;
import com.rabinchuk.paymentservice.model.PaymentStatus;
import com.rabinchuk.paymentservice.repository.PaymentRepository;
import com.rabinchuk.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private PaymentService paymentService;

    @BeforeEach
    public void setup() {
        paymentRepository.deleteAll();
    }


    @Test
    public void getAllPayments_shouldReturnFilteredPayments() throws Exception {
        paymentRepository.saveAll(List.of(
                Payment.builder().status(PaymentStatus.SUCCESS).orderId(1L).build(),
                Payment.builder().status(PaymentStatus.FAILED).orderId(2L).build(),
                Payment.builder().status(PaymentStatus.SUCCESS).orderId(3L).build()
        ));

        mockMvc.perform(get("/api/payments").param("status", "SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("SUCCESS")));
    }

    @Test
    public void getTotalSumOfPaymentsForDatePeriod_shouldReturnTotalSum() throws Exception {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59);

        paymentRepository.saveAll(List.of(
                Payment.builder().status(PaymentStatus.SUCCESS).paymentAmount(new BigDecimal("150.50")).timestamp(startDate.plusDays(5)).build(),
                Payment.builder().status(PaymentStatus.SUCCESS).paymentAmount(new BigDecimal("50.00")).timestamp(startDate.plusDays(10)).build(),
                Payment.builder().status(PaymentStatus.FAILED).paymentAmount(new BigDecimal("100.00")).timestamp(startDate.plusDays(11)).build(),
                Payment.builder().status(PaymentStatus.SUCCESS).paymentAmount(new BigDecimal("200.00")).timestamp(startDate.minusDays(1)).build()
        ));

        mockMvc.perform(get("/api/payments/total-amount")
                        .param("startDate", startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .param("endDate", endDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount", is(200.50)));
    }

}
