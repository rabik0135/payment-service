package com.rabinchuk.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "random-number-api", url = "http://www.randomnumberapi.com")
public interface ExternalApiClient {

    @GetMapping("/api/v1.0/random?min=1&max=100")
    List<Integer> getRandomNumber();
}
