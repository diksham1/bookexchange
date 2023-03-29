package com.intuit.bookexchange.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.intuit.bookexchange.entities.Product;

@Repository
public interface ProductsRepository extends CrudRepository<Product, Integer> {

}
