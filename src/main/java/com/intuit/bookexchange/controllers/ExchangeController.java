package com.intuit.bookexchange.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.bookexchange.dto.BorrowProductRequest;
import com.intuit.bookexchange.dto.CompleteExchangeRequest;
import com.intuit.bookexchange.dto.ErrorResponse;
import com.intuit.bookexchange.dto.ExchangeResponse;
import com.intuit.bookexchange.dto.InitiateExchangeRequest;
import com.intuit.bookexchange.dto.InitiateExchangeResponse;
import com.intuit.bookexchange.entities.Exchange;
import com.intuit.bookexchange.entities.Product;
import com.intuit.bookexchange.exceptions.ExchangeNotPossibleException;
import com.intuit.bookexchange.exceptions.NotFoundException;
import com.intuit.bookexchange.services.ExchangeService;
import com.intuit.bookexchange.services.ProductService;

@RestController
@RequestMapping("/exchanges")
public class ExchangeController {
    private final ExchangeService exchangeService;
    private final ProductService productService;

    public ExchangeController(ExchangeService exchangeService, ProductService productService) {
        this.exchangeService = exchangeService;
        this.productService = productService;
    }

    // ------------- Post Mappings -----------------------------

    @PostMapping("")
    public ResponseEntity<InitiateExchangeResponse> initiateExchange(
            @RequestBody InitiateExchangeRequest request) {
        return ResponseEntity.ok(exchangeService.initiateExchange(request));
    }

    @PostMapping("/{exchangeId}/complete")
    public ResponseEntity<?> completedExchange(@PathVariable int exchangeId,
            @RequestBody CompleteExchangeRequest request) {
        Exchange exchange = exchangeService.completeExchange(exchangeId, request);
        return ResponseEntity.ok(buildExchangeResponse(exchange));
    }

    @PostMapping("/{exchangeId}/borrow")
    public ResponseEntity<?> borrowProduct(@PathVariable int exchangeId,
            @RequestBody BorrowProductRequest request) {
        Exchange exchange = exchangeService.borrowProduct(exchangeId, request);
        return ResponseEntity.ok(buildExchangeResponse(exchange));
    }

    @ExceptionHandler({
            ExchangeNotPossibleException.class,
            NotFoundException.class
    })
    ResponseEntity<ErrorResponse> handleException(Exception e) {
        HttpStatus status;
        String errorMessage;

        if (e instanceof ExchangeNotPossibleException) {
            status = HttpStatus.FORBIDDEN;
            errorMessage = e.getMessage();
        } else if (e instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorMessage = e.getMessage();
        }
        else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = "Something went wrong";
        }

        return ResponseEntity.status(status)
                .body(ErrorResponse.builder().message(errorMessage).build());
    }

    // -------------------- Get Mappings ----------------------------------

    @GetMapping("/{exchangeId}")
    public ResponseEntity<ExchangeResponse> getExchange(@PathVariable int exchangeId) {
        Exchange exchange = exchangeService.getExchange(exchangeId);
        return ResponseEntity.ok(buildExchangeResponse(exchange));
    }

    @GetMapping("")
    public ResponseEntity<List<ExchangeResponse>> getAllExchanges() {
        List<ExchangeResponse> response = exchangeService.getAllExchanges().stream()
                .map(exchange -> buildExchangeResponse(exchange))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private ExchangeResponse buildExchangeResponse(Exchange exchange) {
        Product exchangeProduct = productService.getProduct(exchange.getSenderProductId());

        return ExchangeResponse.builder()
                .exchangeRequestId(exchange.getId())
                .exchangeProduct(exchangeProduct)
                .status(exchange.getStatus())
                .build();
    }
}
