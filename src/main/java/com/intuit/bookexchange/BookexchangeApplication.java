package com.intuit.bookexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.intuit.bookexchange.entities")
@EnableJpaRepositories("com.intuit.bookexchange.repositories")
public class BookexchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookexchangeApplication.class, args);
	}
}
