package com.intuit.bookexchange.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.bookexchange.dto.BorrowProductRequest;
import com.intuit.bookexchange.dto.CompleteExchangeRequest;
import com.intuit.bookexchange.dto.ExchangeResponse;
import com.intuit.bookexchange.dto.InitiateExchangeRequest;
import com.intuit.bookexchange.dto.InitiateExchangeResponse;
import com.intuit.bookexchange.entities.Exchange;
import com.intuit.bookexchange.entities.Product;
import com.intuit.bookexchange.entities.User;
import com.intuit.bookexchange.services.ExchangeService;
import com.intuit.bookexchange.services.ProductService;
import com.intuit.bookexchange.services.UserService;
import static com.intuit.bookexchange.common.UserHelper.buildUserResponse;

@RestController
@RequestMapping("/exchanges")
public class ExchangeController {
    private final ExchangeService exchangeService;
    private final ProductService productService;
    private final UserService userService;

    public ExchangeController(ExchangeService exchangeService, ProductService productService,
            UserService userService) {
        this.exchangeService = exchangeService;
        this.productService = productService;
        this.userService = userService;
    }

    // ------------- Post Mappings -----------------------------

    @PostMapping("")
    public ResponseEntity<InitiateExchangeResponse> initiateExchange(
            @RequestBody InitiateExchangeRequest request) {
        return ResponseEntity.ok(exchangeService.initiateExchange(request));
    }

    @PostMapping("/{exchangeId}/complete")
    public ResponseEntity<ExchangeResponse> completedExchange(@PathVariable int exchangeId,
            @RequestBody CompleteExchangeRequest request) {
        Exchange exchange = exchangeService.completeExchange(exchangeId, request);
        return ResponseEntity.ok(buildExchangeResponse(exchange));
    }

    @PostMapping("/{exchangeId}/borrow")
    public ResponseEntity<ExchangeResponse> borrowProduct(@PathVariable int exchangeId,
            @RequestBody BorrowProductRequest request) {
        Exchange exchange = exchangeService.borrowProduct(exchangeId, request);
        return ResponseEntity.ok(buildExchangeResponse(exchange));
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
        User exchanger = userService.getUser(exchange.getSenderUserId());
        Product exchangeProduct = productService.getProduct(exchange.getSenderProductId());

        return ExchangeResponse.builder()
                .exchanger(buildUserResponse(exchanger))
                .exchangeProduct(exchangeProduct)
                .build();
    }
}
