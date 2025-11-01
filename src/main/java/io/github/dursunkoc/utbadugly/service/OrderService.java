package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.domain.*;
import io.github.dursunkoc.utbadugly.entity.Product;
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
    private final RestTemplate restTemplate = new RestTemplate();

    public OrderResponse createOrder(OrderRequest req) {

        boolean validProvision = paymentService.validateProvision(req.getProvisionNumber());

        if (!validProvision) {
            return OrderResponse.builder().error("Payment validation failed").build();
        }

        Product product = productService.findById(req.getProductId());
        if (product == null) {
            return OrderResponse.builder().error("Product not found").build();
        }

//        List<Map<String, Object>> products = jdbc.queryForList("SELECT * FROM product WHERE id=" + req.getProductId());
//        if (products.isEmpty()) return OrderResponse.builder().error("Product not found").build();
//        Map<String, Object> product = products.get(0);
        double price = product.getPrice();
        int orderId = new Random().nextInt(100000);
        jdbc.update("INSERT INTO orders VALUES (?, ?, ?, ?, ?)", orderId, req.getProductId(), req.getQuantity(), true, false);
        jdbc.update("UPDATE warehouse SET stock = stock - ? WHERE id=1", req.getQuantity());
        List<Map<String, Object>> shipmentCodes = jdbc.queryForList("SELECT shipment_code FROM product_shipment_code WHERE product_id=" + req.getProductId() + " AND city='" + req.getCity() + "'");
        if (shipmentCodes.isEmpty())
            return OrderResponse.builder().error("Shipment code not found for product/city").build();
        String shipmentCode = (String) shipmentCodes.get(0).get("shipment_code");
        StartShipmentRequest shipmentRequest = StartShipmentRequest.builder()
                .customer_id(req.getCustomerId())
                .customer_address(req.getCustomerAddress())
                .product_shipment_code(shipmentCode)
                .build();
        StartShipmentResponse shipmentResponse = restTemplate.postForObject(
                "http://localhost:8082/start-shipment",
                shipmentRequest,
                StartShipmentResponse.class
        );
        boolean shipped = shipmentResponse != null && shipmentResponse.isShipped();
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
