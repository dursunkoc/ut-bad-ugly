package io.github.dursunkoc.utbadugly.repository;

import io.github.dursunkoc.utbadugly.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {
}
