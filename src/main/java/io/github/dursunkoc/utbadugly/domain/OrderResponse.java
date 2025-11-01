package io.github.dursunkoc.utbadugly.domain;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private int orderId;
    private String product;
    private int quantity;
    private double total;
    private boolean paid;
    private boolean shipped;
    private String error;
}

