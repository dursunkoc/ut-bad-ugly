package io.github.dursunkoc.utbadugly.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PaymentServiceConfiguration {

    @Bean("paymentServiceRestTemplate") RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .build();
    }

}
