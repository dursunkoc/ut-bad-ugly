package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.domain.PaymentValidationRequest;
import io.github.dursunkoc.utbadugly.domain.PaymentValidationResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {
    private static final String CLIENT_ID = "badugly-client";
    private static final String CLIENT_SECRET = "badugly-secret";
    private final RestTemplate restTemplate;
    private final String paymentValidationUrl;

    PaymentService(@Qualifier("paymentServiceRestTemplate") RestTemplate restTemplate,
                   @Value("${payment.validation.url:http://localhost:8081/validate-payment}") String paymentValidationUrl) {
        this.restTemplate = restTemplate;
        this.paymentValidationUrl = paymentValidationUrl;
    }

    public boolean validateProvision(String provisionNumber) {
        PaymentValidationRequest paymentRequest = PaymentValidationRequest.builder()
                .provisionNumber(provisionNumber)
                .clientSecret(CLIENT_SECRET)
                .clientId(CLIENT_ID)
                .build();

        PaymentValidationResponse paymentResponse = restTemplate.postForObject(
                paymentValidationUrl,
                paymentRequest,
                PaymentValidationResponse.class
        );

        return paymentResponse != null && paymentResponse.isValid();
    }
}
