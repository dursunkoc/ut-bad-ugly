package io.github.dursunkoc.utbadugly.domain;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentValidationRequest {
        private String provisionNumber;
        private String clientId;
        private String clientSecret;
}
