package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.domain.StartShipmentRequest;
import io.github.dursunkoc.utbadugly.domain.StartShipmentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @InjectMocks
    private ShipmentService shipmentService;

    @Mock
    private RestTemplate restTemplate=new RestTemplate();

    private String shipmentStartUrl;

    @BeforeEach
    void setUp() {
        shipmentStartUrl = "http://localhost:9999/start-shipment";
        ReflectionTestUtils.setField(shipmentService, "shipmenStartUrl", shipmentStartUrl);
    }

    @Test
    void testStartShipmentWhenRemoteResponseShippedTrueShouldReturnTrue() {
        int customerId = 123;
        String customerAddress = "123 Main St";
        String shipmentCode = "SHP-001";

        StartShipmentResponse response = StartShipmentResponse.builder()
                .shipped(true)
                .error("")
                .build();

        when(restTemplate.postForObject(eq(shipmentStartUrl), any(),
                eq(StartShipmentResponse.class))).thenReturn(response);

        boolean result = shipmentService.startShipment(customerId, customerAddress, shipmentCode);

        assertTrue(result);
        ArgumentCaptor<StartShipmentRequest> captor = ArgumentCaptor.forClass(StartShipmentRequest.class);
        verify(restTemplate, times(1)).postForObject(
                eq(shipmentStartUrl),
                captor.capture(),
                eq(StartShipmentResponse.class)
        );

        StartShipmentRequest capturedRequest = captor.getValue();
        assertEquals(customerId, capturedRequest.getCustomerId());
        assertEquals(customerAddress, capturedRequest.getCustomerAddress());
        assertEquals(shipmentCode, capturedRequest.getProductShipmentCode());
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    void testStartShipmentWhenRemoteResponseShippedFalseShouldReturnFalse() {
        int customerId = 456;
        String customerAddress = "456 Oak Ave";
        String shipmentCode = "SHP-002";

        StartShipmentResponse response = StartShipmentResponse.builder()
                .shipped(false)
                .error("Address not found")
                .build();

        when(restTemplate.postForObject(eq(shipmentStartUrl), any(),
                eq(StartShipmentResponse.class))).thenReturn(response);

        boolean result = shipmentService.startShipment(customerId, customerAddress, shipmentCode);

        assertFalse(result);
        verify(restTemplate, times(1)).postForObject(
                eq(shipmentStartUrl),
                any(),
                eq(StartShipmentResponse.class)
        );
        verifyNoMoreInteractions(restTemplate);
    }

    @Test
    void testStartShipmentWhenRemoteResponseNullShouldReturnFalse() {
        int customerId = 789;
        String customerAddress = "789 Pine Rd";
        String shipmentCode = "SHP-003";

        when(restTemplate.postForObject(eq(shipmentStartUrl), any(),
                eq(StartShipmentResponse.class))).thenReturn(null);

        boolean result = shipmentService.startShipment(customerId, customerAddress, shipmentCode);

        assertFalse(result);
        verify(restTemplate, times(1)).postForObject(
                eq(shipmentStartUrl),
                any(),
                eq(StartShipmentResponse.class)
        );
        verifyNoMoreInteractions(restTemplate);
    }
}

