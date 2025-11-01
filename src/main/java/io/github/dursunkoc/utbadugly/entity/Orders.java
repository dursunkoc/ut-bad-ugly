package io.github.dursunkoc.utbadugly.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("ORDERS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Orders {
    @Id
    private int id;
    private int customerId;
    private int productId;
    private int quantity;
    private boolean paid;
    private boolean shipped;

}
