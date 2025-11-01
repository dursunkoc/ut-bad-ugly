package io.github.dursunkoc.utbadugly.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("PRODUCT_SHIPMENT_CODE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductShipmentCode {
    @Id
    private int id;
    private int productId;
    private String city;
    private String shipmentCode;
}
