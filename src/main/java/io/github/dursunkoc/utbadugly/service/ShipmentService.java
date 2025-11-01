package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.domain.StartShipmentRequest;
import io.github.dursunkoc.utbadugly.domain.StartShipmentResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ShipmentService {
    private final RestTemplate restTemplate;
    private final String shipmenStartUrl;

    public ShipmentService(@Qualifier("shipmentServiceRestTemplate") RestTemplate restTemplate,
    @Value("${shipment.start.url:http://localhost:8082/start-shipment}") String shipmentStartUrl) {
        this.restTemplate = restTemplate;
        this.shipmenStartUrl = shipmentStartUrl;
    }

    public boolean startShipment(int customerId, String customerAddress, String shipmentCode) {
        StartShipmentRequest shipmentRequest = StartShipmentRequest.builder()
                .customerId(customerId)
                .customerAddress(customerAddress)
                .productShipmentCode(shipmentCode)
                .build();
        StartShipmentResponse shipmentResponse = restTemplate.postForObject(
                shipmenStartUrl,
                shipmentRequest,
                StartShipmentResponse.class
        );
        return shipmentResponse != null && shipmentResponse.isShipped();

    }
}
