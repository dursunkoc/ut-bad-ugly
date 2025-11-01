package io.github.dursunkoc.utbadugly.controller;

import io.github.dursunkoc.utbadugly.domain.OrderRequest;
import io.github.dursunkoc.utbadugly.domain.OrderResponse;
import io.github.dursunkoc.utbadugly.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BadUglyEcommerceController {
    private final OrderService orderService;

    @PostMapping("/order")
    public OrderResponse createOrder(@RequestBody OrderRequest req) {
        return orderService.createOrder(req);
    }
}
