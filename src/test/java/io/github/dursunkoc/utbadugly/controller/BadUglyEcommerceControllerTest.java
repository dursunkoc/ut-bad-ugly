package io.github.dursunkoc.utbadugly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dursunkoc.utbadugly.domain.OrderRequest;
import io.github.dursunkoc.utbadugly.domain.OrderResponse;
import io.github.dursunkoc.utbadugly.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers=BadUglyEcommerceController.class)
@AutoConfigureMockMvc
class BadUglyEcommerceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateOrderWhenSuccessfullyCreatedOrderShouldReturnASuccessResponse() throws Exception {
        OrderRequest orderRequest = OrderRequest.builder()
                .productId(1)
                .quantity(2)
                .provisionNumber("1234567890")
                .city("TestCity")
                .customerId(1001)
                .customerAddress("123 Test St, TestCity")
                .build();
        String request = objectMapper.writeValueAsString(orderRequest);

        int orderId = 5001;
        String product = "product";
        int unitPrice = 100;
        int total = orderRequest.getQuantity() * unitPrice;
        when(orderService.createOrder(argThat(or ->
                or.getQuantity() == orderRequest.getQuantity() &&
                        or.getProductId() == orderRequest.getProductId() &&
                        or.getProvisionNumber().equals(orderRequest.getProvisionNumber()) &&
                        or.getCity().equals(orderRequest.getCity()) &&
                        or.getCustomerId() == orderRequest.getCustomerId() &&
                        or.getCustomerAddress().equals(orderRequest.getCustomerAddress()))))
                .thenReturn(OrderResponse.builder()
                        .orderId(orderId)
                        .product(product)
                        .total(total)
                        .shipped(true)
                        .error("")
                        .paid(true)
                        .quantity(orderRequest.getQuantity())
                        .build()
                );

        mockMvc.perform(
                        post("/order")
                                .content(request).contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    OrderResponse orderResponse = objectMapper.readValue(responseBody, OrderResponse.class);
                    assert orderResponse.getOrderId() == orderId;
                    assert orderResponse.getProduct().equals(product);
                    assert orderResponse.getTotal() == total;
                    assert orderResponse.isShipped();
                    assert orderResponse.isPaid();
                    assert orderResponse.getQuantity() == orderRequest.getQuantity();
                    assert orderResponse.getError().isEmpty();
                });

        verify(orderService, times(1)).createOrder(argThat(or ->
                or.getQuantity() == orderRequest.getQuantity() &&
                        or.getProductId() == orderRequest.getProductId() &&
                        or.getProvisionNumber().equals(orderRequest.getProvisionNumber()) &&
                        or.getCity().equals(orderRequest.getCity()) &&
                        or.getCustomerId() == orderRequest.getCustomerId() &&
                        or.getCustomerAddress().equals(orderRequest.getCustomerAddress())));
    }


}