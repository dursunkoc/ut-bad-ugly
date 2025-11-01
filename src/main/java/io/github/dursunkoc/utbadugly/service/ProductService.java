package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.entity.Product;
import io.github.dursunkoc.utbadugly.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    public Product findById(int id) {
        return productRepository.findById(id).orElse(null);
    }
}
