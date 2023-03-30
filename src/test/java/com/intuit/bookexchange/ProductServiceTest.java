package com.intuit.bookexchange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import com.intuit.bookexchange.entities.Book;
import com.intuit.bookexchange.entities.Product;
import com.intuit.bookexchange.exceptions.NotFoundException;
import com.intuit.bookexchange.services.ProductService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProductServiceTest {

    private static final int USER_ID_1 = 1;
    private static final int USER_ID_2 = 2;

    private static final int PRODUCT_ID_1 = 1;

    @Autowired
    ProductService productService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void createProduct_success() {
        Book book = Book.builder()
                .ownerId(USER_ID_1)
                .build();

        Book createdBook = (Book) productService.createProduct(book);

        assertEquals(book.getOwnerId(), createdBook.getOwnerId());
    }

    @Test
    public void getProduct_success() {
        Book book = Book.builder()
                .ownerId(USER_ID_1)
                .build();
        Book createdBook = (Book) productService.createProduct(book);

        Product fetchedProduct = productService.getProduct(createdBook.getProductId());

        assertEquals(createdBook.getOwnerId(), fetchedProduct.getOwnerId());
    }

    @Test
    public void getProduct_notFound() {
        assertThrows(NotFoundException.class,
                () -> productService.getProduct(PRODUCT_ID_1));
    }

    @Test
    public void getAllProducts_success() {
        Book book1 = Book.builder()
                .ownerId(USER_ID_1)
                .build();
        Book book2 = Book.builder()
                .ownerId(USER_ID_2)
                .build();
        productService.createProduct(book1);
        productService.createProduct(book2);

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
    }

    @Test
    public void getAllProducts_noProductsAvailable_returnsEmptyList() {
        List<Product> products = productService.getAllProducts();

        assertEquals(0, products.size());
    }
}
