package com.adinuegroho.service;

import com.adinuegroho.entity.Product;
import com.adinuegroho.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }
}
