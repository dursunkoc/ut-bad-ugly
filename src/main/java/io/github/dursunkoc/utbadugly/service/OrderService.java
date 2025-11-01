package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.domain.OrderRequest;
import io.github.dursunkoc.utbadugly.domain.OrderResponse;
import io.github.dursunkoc.utbadugly.entity.Orders;
import io.github.dursunkoc.utbadugly.entity.Product;
import io.github.dursunkoc.utbadugly.entity.ProductShipmentCode;
import io.github.dursunkoc.utbadugly.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final PaymentService paymentService;
    private final ProductService productService;
    private final ShipmentService shipmentService;
    private final OrdersRepository ordersRepository;

    public OrderResponse createOrder(OrderRequest req) {

        boolean validProvision = paymentService.validateProvision(req.getProvisionNumber());

        if (!validProvision) {
            return OrderResponse.builder().error("Payment validation failed").build();
        }

        Product product = productService.findById(req.getProductId());
        if (product == null) {
            return OrderResponse.builder().error("Product not found").build();
        }

        ProductShipmentCode productShipmentCode = productService.getShipmentCode(req.getProductId(), req.getCity());
        if (productShipmentCode==null) {
            return OrderResponse.builder().error("Shipment code not found for product/city").build();
        }

        String shipmentCode = productShipmentCode.getShipmentCode();

        boolean shipped = shipmentService.startShipment(req.getCustomerId(), req.getCustomerAddress(), shipmentCode);

        Orders saved = ordersRepository.save(Orders.builder()
                .customerId(req.getCustomerId())
                .productId(req.getProductId())
                .quantity(req.getQuantity())
                .shipped(shipped)
                .paid(true)
                .build());
        int orderId = saved.getId();

        return OrderResponse.builder()
                .orderId(orderId)
                .product(product.getName())
                .quantity(req.getQuantity())
                .total(product.getPrice() * req.getQuantity())
                .shipped(shipped)
                .paid(true)
                .build();
    }
}
