package io.github.dursunkoc.utbadugly.domain;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartShipmentRequest {
    private int customerId;
    private String customerAddress;
    private String productShipmentCode;
}

