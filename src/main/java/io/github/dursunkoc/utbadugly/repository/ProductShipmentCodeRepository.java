package io.github.dursunkoc.utbadugly.repository;

import io.github.dursunkoc.utbadugly.entity.Product;
import io.github.dursunkoc.utbadugly.entity.ProductShipmentCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductShipmentCodeRepository extends CrudRepository<ProductShipmentCode, Integer> {
    Optional<ProductShipmentCode> findByProductIdAndCity(int productId, String city);
}
