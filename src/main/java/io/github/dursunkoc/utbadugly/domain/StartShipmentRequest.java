package io.github.dursunkoc.utbadugly.domain;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartShipmentRequest {
    private int customer_id;
    private String customer_address;
    private String product_shipment_code;
}

