package io.github.dursunkoc.utbadugly;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class UtBadUglyApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(UtBadUglyApplication.class, args);
    }

    private final org.springframework.jdbc.core.JdbcTemplate jdbc;

    // Populate meta-data at startup
    @Override
    public void run(String... args) {
        jdbc.execute("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, name VARCHAR(255), price DOUBLE)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS warehouse (id INT PRIMARY KEY, location VARCHAR(255), stock INT)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS orders (id INT PRIMARY KEY, product_id INT, quantity INT, paid BOOLEAN, shipped BOOLEAN)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS product_shipment_code (id INT PRIMARY KEY, product_id INT, city VARCHAR(255), shipment_code VARCHAR(255))");
        jdbc.execute("MERGE INTO product (id, name, price) KEY(id) VALUES (1, 'Widget', 19.99)");
        jdbc.execute("MERGE INTO warehouse (id, location, stock) KEY(id) VALUES (1, 'Main Warehouse', 100)");
        jdbc.execute("MERGE INTO product_shipment_code (id, product_id, city, shipment_code) KEY(id) VALUES (1, 1, 'Istanbul', 'SHIP-IST-001')");
    }
}
