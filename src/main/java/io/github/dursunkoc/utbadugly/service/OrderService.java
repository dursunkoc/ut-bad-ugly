package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.domain.*;
import io.github.dursunkoc.utbadugly.entity.Product;
import io.github.dursunkoc.utbadugly.entity.ProductShipmentCode;
import io.github.dursunkoc.utbadugly.exception.InvalidPaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final JdbcTemplate jdbc;
    private final PaymentService paymentService;
    private final ProductService productService;
    private final ShipmentService shipmentService;

    public OrderResponse createOrder(OrderRequest req) {

        boolean validProvision = paymentService.validateProvision(req.getProvisionNumber());

        if (!validProvision) {
            return OrderResponse.builder().error("Payment validation failed").build();
        }

        Product product = productService.findById(req.getProductId());
        if (product == null) {
            return OrderResponse.builder().error("Product not found").build();
        }

        double price = product.getPrice();
        int orderId = new Random().nextInt(100000);
        jdbc.update("INSERT INTO orders VALUES (?, ?, ?, ?, ?)", orderId, req.getProductId(), req.getQuantity(), true, false);
        jdbc.update("UPDATE warehouse SET stock = stock - ? WHERE id=1", req.getQuantity());

        ProductShipmentCode productShipmentCode = productService.getShipmentCode(req.getProductId(), req.getCity());
        if (productShipmentCode==null) {
            return OrderResponse.builder().error("Shipment code not found for product/city").build();
        }

        String shipmentCode = productShipmentCode.getShipmentCode();

        boolean shipped = shipmentService.startShipment(req.getCustomerId(), req.getCustomerAddress(), shipmentCode);

        if (shipped) {
            jdbc.update("UPDATE orders SET shipped=true WHERE id=?", orderId);
        }
        return OrderResponse.builder()
                .orderId(orderId)
                .product(product.getName())
                .quantity(req.getQuantity())
                .total(price * req.getQuantity())
                .shipped(shipped)
                .paid(true)
                .build();
    }
}
