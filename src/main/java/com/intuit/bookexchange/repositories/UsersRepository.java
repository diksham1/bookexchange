package com.intuit.bookexchange.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.intuit.bookexchange.entities.User;

// TODO(okdiksha): Investigate using a single database connection.
@Repository
public interface UsersRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
