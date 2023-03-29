package com.intuit.bookexchange.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.intuit.bookexchange.entities.Exchange;

@Repository
public interface ExchangesRepository extends CrudRepository<Exchange, Integer> {
    
}
