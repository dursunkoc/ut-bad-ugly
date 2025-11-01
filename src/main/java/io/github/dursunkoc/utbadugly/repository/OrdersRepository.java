package io.github.dursunkoc.utbadugly.repository;

import io.github.dursunkoc.utbadugly.entity.Orders;
import org.springframework.data.repository.CrudRepository;

public interface OrdersRepository extends CrudRepository<Orders, Integer> {
}
