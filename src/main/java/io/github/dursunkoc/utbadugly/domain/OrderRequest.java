package io.github.dursunkoc.utbadugly.domain;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private int productId;
    private int quantity;
    private String provisionNumber;
    private int customerId;
    private String customerAddress;
    private String city;
}
