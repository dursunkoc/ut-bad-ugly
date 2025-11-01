package io.github.dursunkoc.utbadugly.domain;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentValidationResponse {
        private boolean valid;
        private String error;
}
