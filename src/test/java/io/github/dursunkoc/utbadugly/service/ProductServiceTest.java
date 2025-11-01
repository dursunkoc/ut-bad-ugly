package io.github.dursunkoc.utbadugly.service;

import io.github.dursunkoc.utbadugly.entity.Product;
import io.github.dursunkoc.utbadugly.entity.ProductShipmentCode;
import io.github.dursunkoc.utbadugly.repository.ProductRepository;
import io.github.dursunkoc.utbadugly.repository.ProductShipmentCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductShipmentCodeRepository productShipmentCodeRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testFindByIdWhenProductExistsShouldReturnProduct() {
        int id = 5;
        Product product = new Product(id, "Keyboard", 49.99);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Product result = productService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Keyboard", result.getName());
        assertEquals(49.99, result.getPrice());
        verify(productRepository, times(1)).findById(id);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void testFindByIdWhenProductNotExistsShouldReturnNull() {
        int id = 999;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        Product result = productService.findById(id);

        assertNull(result);
        verify(productRepository, times(1)).findById(id);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void testGetShipmentCodeWhenRecordExistsShouldReturnShipmentCode() {
        int productId = 10;
        String city = "Istanbul";
        ProductShipmentCode expected = new ProductShipmentCode(1, productId, city, "IST-10-A");
        when(productShipmentCodeRepository.findByProductIdAndCity(productId, city)).thenReturn(Optional.of(expected));

        ProductShipmentCode result = productService.getShipmentCode(productId, city);

        assertNotNull(result);
        assertEquals("IST-10-A", result.getShipmentCode());
        assertEquals(productId, result.getProductId());
        assertEquals(city, result.getCity());
        verify(productShipmentCodeRepository, times(1)).findByProductIdAndCity(productId, city);
        verifyNoMoreInteractions(productShipmentCodeRepository);
    }

    @Test
    void testGetShipmentCodeWhenRecordNotExistsShouldReturnNull() {
        int productId = 20;
        String city = "Ankara";
        when(productShipmentCodeRepository.findByProductIdAndCity(productId, city)).thenReturn(Optional.empty());

        ProductShipmentCode result = productService.getShipmentCode(productId, city);

        assertNull(result);
        verify(productShipmentCodeRepository, times(1)).findByProductIdAndCity(productId, city);
        verifyNoMoreInteractions(productShipmentCodeRepository);
    }


}

