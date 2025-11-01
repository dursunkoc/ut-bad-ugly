package io.github.dursunkoc.utbadugly.domain;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartShipmentResponse {
    private boolean shipped;
    private String error;
}

