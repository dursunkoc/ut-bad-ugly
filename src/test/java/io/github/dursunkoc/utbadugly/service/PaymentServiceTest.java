package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.domain.PaymentValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Spy
    private RestTemplate restTemplate=new RestTemplate();

    private String paymentValidationUrl;

    @BeforeEach
    void setUp() {
        paymentValidationUrl = "http://localhost:9999/validate-payment"; // custom test URL
        ReflectionTestUtils.setField(paymentService, "paymentValidationUrl", paymentValidationUrl);
    }

    @Test
    void testValidateProvisionWhenRemoteResponseValidTrueShouldReturnTrue() {
        RestTemplate internalRestTemplate = (RestTemplate) ReflectionTestUtils.getField(paymentService, "restTemplate");
        assertNotNull(internalRestTemplate, "RestTemplate should have been instantiated inside PaymentService");
        MockRestServiceServer server = MockRestServiceServer.bindTo(internalRestTemplate).ignoreExpectOrder(true).build();

        String provisionNumber = "1234567890";
        // Expect a POST to the validation URL
        server.expect(requestTo(paymentValidationUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"valid\":true,\"error\":\"\"}", MediaType.APPLICATION_JSON));

        boolean result = paymentService.validateProvision(provisionNumber);
        assertTrue(result);
    }

    @Test
    void testValidateProvisionWhenRemoteResponseValidFalseShouldReturnFalse() {
        RestTemplate internalRestTemplate = (RestTemplate) ReflectionTestUtils.getField(paymentService, "restTemplate");
        assertNotNull(internalRestTemplate);
        MockRestServiceServer server = MockRestServiceServer.bindTo(internalRestTemplate).ignoreExpectOrder(true).build();

        String provisionNumber = "9999999999";
        server.expect(requestTo(paymentValidationUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"valid\":false,\"error\":\"invalid provision\"}", MediaType.APPLICATION_JSON));

        boolean result = paymentService.validateProvision(provisionNumber);

        assertFalse(result);
    }

    @Test
    void testValidateProvisionWhenRemoteResponseNullShouldReturnFalse() {
        String provisionNumber = "0000000000";
        RestTemplate internalRestTemplate = (RestTemplate) ReflectionTestUtils.getField(paymentService, "restTemplate");
        assertNotNull(internalRestTemplate);
        MockRestServiceServer server = MockRestServiceServer.bindTo(internalRestTemplate).ignoreExpectOrder(true).build();

        server.expect(requestTo(paymentValidationUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        boolean result = paymentService.validateProvision(provisionNumber);
        assertFalse(result);
    }
}

