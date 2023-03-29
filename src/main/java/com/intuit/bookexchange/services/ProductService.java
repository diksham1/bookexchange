package com.intuit.bookexchange.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.intuit.bookexchange.entities.Product;
import com.intuit.bookexchange.exceptions.NotFoundException;
import com.intuit.bookexchange.repositories.ProductsRepository;

@Service
public class ProductService {
    private final ProductsRepository productsRepository;

    public ProductService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public Product createProduct(Product product) {
        return productsRepository.save(product);
    }

    public Product getProduct(int productId) {
        return productsRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Product with id %d not found", productId)));
    }

    public List<Product> getAllProducts() {
        return StreamSupport.stream(productsRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
