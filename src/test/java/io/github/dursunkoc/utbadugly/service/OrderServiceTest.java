package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.domain.OrderRequest;
import io.github.dursunkoc.utbadugly.domain.OrderResponse;
import io.github.dursunkoc.utbadugly.entity.Orders;
import io.github.dursunkoc.utbadugly.entity.Product;
import io.github.dursunkoc.utbadugly.entity.ProductShipmentCode;
import io.github.dursunkoc.utbadugly.repository.OrdersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private ProductService productService;

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private OrdersRepository ordersRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrderWhenAllValidationPassShouldReturnSuccessfulOrder() {
        OrderRequest request = OrderRequest.builder()
                .productId(1)
                .quantity(2)
                .provisionNumber("PROV-123")
                .customerId(100)
                .customerAddress("123 Main St")
                .city("Istanbul")
                .build();

        when(paymentService.validateProvision("PROV-123")).thenReturn(true);
        when(productService.findById(1)).thenReturn(new Product(1, "Keyboard", 49.99));
        when(productService.getShipmentCode(1, "Istanbul")).thenReturn(new ProductShipmentCode(1, 1, "Istanbul", "IST-001"));
        when(shipmentService.startShipment(100, "123 Main St", "IST-001")).thenReturn(true);

        Orders savedOrder = Orders.builder()
                .id(500)
                .customerId(100)
                .productId(1)
                .quantity(2)
                .shipped(true)
                .paid(true)
                .build();
        when(ordersRepository.save(any(Orders.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(500, response.getOrderId());
        assertEquals("Keyboard", response.getProduct());
        assertEquals(2, response.getQuantity());
        assertEquals(99.98, response.getTotal());
        assertTrue(response.isPaid());
        assertTrue(response.isShipped());
        assertNull(response.getError());

        verify(paymentService, times(1)).validateProvision("PROV-123");
        verify(productService, times(1)).findById(1);
        verify(productService, times(1)).getShipmentCode(1, "Istanbul");
        verify(shipmentService, times(1)).startShipment(100, "123 Main St", "IST-001");
        verify(ordersRepository, times(1)).save(any(Orders.class));
    }

    @Test
    void testCreateOrderWhenPaymentValidationFailsShouldReturnErrorResponse() {
        OrderRequest request = OrderRequest.builder()
                .productId(1)
                .quantity(2)
                .provisionNumber("INVALID-PROV")
                .customerId(100)
                .customerAddress("123 Main St")
                .city("Istanbul")
                .build();

        when(paymentService.validateProvision("INVALID-PROV")).thenReturn(false);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(0, response.getOrderId());
        assertEquals("Payment validation failed", response.getError());

        verify(paymentService, times(1)).validateProvision("INVALID-PROV");
        verify(productService, never()).findById(anyInt());
        verify(productService, never()).getShipmentCode(anyInt(), anyString());
        verify(shipmentService, never()).startShipment(anyInt(), anyString(), anyString());
        verify(ordersRepository, never()).save(any(Orders.class));
    }

    @Test
    void testCreateOrderWhenProductNotFoundShouldReturnErrorResponse() {
        OrderRequest request = OrderRequest.builder()
                .productId(999)
                .quantity(2)
                .provisionNumber("PROV-123")
                .customerId(100)
                .customerAddress("123 Main St")
                .city("Istanbul")
                .build();

        when(paymentService.validateProvision("PROV-123")).thenReturn(true);
        when(productService.findById(999)).thenReturn(null);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(0, response.getOrderId());
        assertEquals("Product not found", response.getError());

        verify(paymentService, times(1)).validateProvision("PROV-123");
        verify(productService, times(1)).findById(999);
        verify(productService, never()).getShipmentCode(anyInt(), anyString());
        verify(shipmentService, never()).startShipment(anyInt(), anyString(), anyString());
        verify(ordersRepository, never()).save(any(Orders.class));
    }

    @Test
    void testCreateOrderWhenShipmentCodeNotFoundShouldReturnErrorResponse() {
        OrderRequest request = OrderRequest.builder()
                .productId(1)
                .quantity(2)
                .provisionNumber("PROV-123")
                .customerId(100)
                .customerAddress("123 Main St")
                .city("UnknownCity")
                .build();

        when(paymentService.validateProvision("PROV-123")).thenReturn(true);
        when(productService.findById(1)).thenReturn(new Product(1, "Keyboard", 49.99));
        when(productService.getShipmentCode(1, "UnknownCity")).thenReturn(null);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(0, response.getOrderId());
        assertEquals("Shipment code not found for product/city", response.getError());

        verify(paymentService, times(1)).validateProvision("PROV-123");
        verify(productService, times(1)).findById(1);
        verify(productService, times(1)).getShipmentCode(1, "UnknownCity");
        verify(shipmentService, never()).startShipment(anyInt(), anyString(), anyString());
        verify(ordersRepository, never()).save(any(Orders.class));
    }

    @Test
    void testCreateOrderWhenShipmentFailsShouldSaveOrderWithShippedFalse() {
        OrderRequest request = OrderRequest.builder()
                .productId(1)
                .quantity(2)
                .provisionNumber("PROV-123")
                .customerId(100)
                .customerAddress("123 Main St")
                .city("Istanbul")
                .build();

        when(paymentService.validateProvision("PROV-123")).thenReturn(true);
        when(productService.findById(1)).thenReturn(new Product(1, "Keyboard", 49.99));
        when(productService.getShipmentCode(1, "Istanbul")).thenReturn(new ProductShipmentCode(1, 1, "Istanbul", "IST-001"));
        when(shipmentService.startShipment(100, "123 Main St", "IST-001")).thenReturn(false);

        Orders savedOrder = Orders.builder()
                .id(501)
                .customerId(100)
                .productId(1)
                .quantity(2)
                .shipped(false)
                .paid(true)
                .build();
        when(ordersRepository.save(any(Orders.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(501, response.getOrderId());
        assertEquals("Keyboard", response.getProduct());
        assertTrue(response.isPaid());
        assertFalse(response.isShipped());
        assertNull(response.getError());

        ArgumentCaptor<Orders> captor = ArgumentCaptor.forClass(Orders.class);
        verify(ordersRepository).save(captor.capture());
        Orders savedOrderArg = captor.getValue();
        assertFalse(savedOrderArg.isShipped());
        assertTrue(savedOrderArg.isPaid());
    }

    @Test
    void testCreateOrderWhenOrderRepositoryThrowsExceptionShouldPropagateException() {
        OrderRequest request = OrderRequest.builder()
                .productId(1)
                .quantity(2)
                .provisionNumber("PROV-123")
                .customerId(100)
                .customerAddress("123 Main St")
                .city("Istanbul")
                .build();

        when(paymentService.validateProvision("PROV-123")).thenReturn(true);
        when(productService.findById(1)).thenReturn(new Product(1, "Keyboard", 49.99));
        when(productService.getShipmentCode(1, "Istanbul")).thenReturn(new ProductShipmentCode(1, 1, "Istanbul", "IST-001"));
        when(shipmentService.startShipment(100, "123 Main St", "IST-001")).thenReturn(true);
        when(ordersRepository.save(any(Orders.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> orderService.createOrder(request));
        assertEquals("Database error", thrown.getMessage());

        verify(ordersRepository, times(1)).save(any(Orders.class));
    }

    @Test
    void testCreateOrderWhenProductPriceIsZeroShouldCalculateTotalAsZero() {
        OrderRequest request = OrderRequest.builder()
                .productId(1)
                .quantity(5)
                .provisionNumber("PROV-123")
                .customerId(100)
                .customerAddress("123 Main St")
                .city("Istanbul")
                .build();

        when(paymentService.validateProvision("PROV-123")).thenReturn(true);
        when(productService.findById(1)).thenReturn(new Product(1, "FreeItem", 0.0));
        when(productService.getShipmentCode(1, "Istanbul")).thenReturn(new ProductShipmentCode(1, 1, "Istanbul", "IST-001"));
        when(shipmentService.startShipment(100, "123 Main St", "IST-001")).thenReturn(true);

        Orders savedOrder = Orders.builder()
                .id(502)
                .customerId(100)
                .productId(1)
                .quantity(5)
                .shipped(true)
                .paid(true)
                .build();
        when(ordersRepository.save(any(Orders.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(0.0, response.getTotal());
        verify(ordersRepository, times(1)).save(any(Orders.class));
    }

    @Test
    void testCreateOrderWhenQuantityIsOneShouldCalculateTotalCorrectly() {
        OrderRequest request = OrderRequest.builder()
                .productId(1)
                .quantity(1)
                .provisionNumber("PROV-123")
                .customerId(100)
                .customerAddress("123 Main St")
                .city("Istanbul")
                .build();

        when(paymentService.validateProvision("PROV-123")).thenReturn(true);
        when(productService.findById(1)).thenReturn(new Product(1, "Keyboard", 49.99));
        when(productService.getShipmentCode(1, "Istanbul")).thenReturn(new ProductShipmentCode(1, 1, "Istanbul", "IST-001"));
        when(shipmentService.startShipment(100, "123 Main St", "IST-001")).thenReturn(true);

        Orders savedOrder = Orders.builder()
                .id(503)
                .customerId(100)
                .productId(1)
                .quantity(1)
                .shipped(true)
                .paid(true)
                .build();
        when(ordersRepository.save(any(Orders.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(49.99, response.getTotal());
        assertEquals(1, response.getQuantity());
        verify(ordersRepository, times(1)).save(any(Orders.class));
    }

    @Test
    void testCreateOrderWhenLargeQuantityShouldCalculateTotalCorrectly() {
        OrderRequest request = OrderRequest.builder()
                .productId(1)
                .quantity(100)
                .provisionNumber("PROV-123")
                .customerId(100)
                .customerAddress("123 Main St")
                .city("Istanbul")
                .build();

        when(paymentService.validateProvision("PROV-123")).thenReturn(true);
        when(productService.findById(1)).thenReturn(new Product(1, "Keyboard", 49.99));
        when(productService.getShipmentCode(1, "Istanbul")).thenReturn(new ProductShipmentCode(1, 1, "Istanbul", "IST-001"));
        when(shipmentService.startShipment(100, "123 Main St", "IST-001")).thenReturn(true);

        Orders savedOrder = Orders.builder()
                .id(504)
                .customerId(100)
                .productId(1)
                .quantity(100)
                .shipped(true)
                .paid(true)
                .build();
        when(ordersRepository.save(any(Orders.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(4999.0, response.getTotal());
        assertEquals(100, response.getQuantity());
        verify(ordersRepository, times(1)).save(any(Orders.class));
    }
}

