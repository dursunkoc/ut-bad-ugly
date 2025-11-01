package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.entity.Product;
import io.github.dursunkoc.utbadugly.entity.ProductShipmentCode;
import io.github.dursunkoc.utbadugly.repository.ProductRepository;
import io.github.dursunkoc.utbadugly.repository.ProductShipmentCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductShipmentCodeRepository productShipmentCodeRepository;

    public Product findById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    public ProductShipmentCode getShipmentCode(int productId, String city) {
        return productShipmentCodeRepository.findByProductIdAndCity(productId, city).orElse(null);
    }
}
