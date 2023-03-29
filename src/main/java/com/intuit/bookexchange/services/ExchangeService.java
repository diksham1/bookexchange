package com.intuit.bookexchange.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.intuit.bookexchange.dto.BorrowProductRequest;
import com.intuit.bookexchange.dto.CompleteExchangeRequest;
import com.intuit.bookexchange.dto.InitiateExchangeRequest;
import com.intuit.bookexchange.dto.InitiateExchangeResponse;
import com.intuit.bookexchange.entities.Book;
import com.intuit.bookexchange.entities.Exchange;
import com.intuit.bookexchange.entities.Product;
import com.intuit.bookexchange.entities.User;
import com.intuit.bookexchange.entities.Exchange.Status;
import com.intuit.bookexchange.exceptions.NotFoundException;
import com.intuit.bookexchange.repositories.ExchangesRepository;

@Service
public class ExchangeService {
    private final ExchangesRepository exchangesRepository;
    private final ProductService productsService;
    private final UserService userService;

    public ExchangeService(ProductService productsService, ExchangesRepository exchangesRepository,
            UserService userService) {
        this.exchangesRepository = exchangesRepository;
        this.productsService = productsService;
        this.userService = userService;
    }

    public InitiateExchangeResponse initiateExchange(InitiateExchangeRequest request) {
        User user = userService.getUser(request.getOwnerUserId());

        Product savedProduct = productsService.createProduct(Book.builder()
                .name(request.getBookName())
                .authorName(request.getAuthorName())
                .ownerId(user.getId())
                .rating(0.0)
                .build());

        Exchange savedExchange = exchangesRepository.save(Exchange.builder()
                .senderProductId(savedProduct.getProductId())
                .senderUserId(request.getOwnerUserId())
                .status(Exchange.Status.EXCHANGE_PENDING)
                .build());

        return InitiateExchangeResponse.builder()
                .exchangeRequestId(savedExchange.getId())
                .exchangeProduct(savedProduct)
                .build();
    }

    public Exchange completeExchange(int initiatorExchangeRequestId, CompleteExchangeRequest request) {

        Exchange initiatorExchange = getExchange(initiatorExchangeRequestId);
        Exchange acceptorExchange = getExchange(request.getAcceptorExchangeRequestId());

        if (initiatorExchange.getStatus() != Exchange.Status.EXCHANGE_PENDING
                || acceptorExchange.getStatus() != Exchange.Status.EXCHANGE_PENDING) {
            throw new IllegalStateException("Exchange request is not in pending state");
        }

        Exchange completedInitiatorExchange = markExchangeAsCompleted(initiatorExchange,
                acceptorExchange.getSenderProductId(),
                acceptorExchange.getSenderUserId());

        Exchange completedAcceptorExchange = markExchangeAsCompleted(acceptorExchange,
                initiatorExchange.getSenderProductId(),
                initiatorExchange.getSenderUserId());

        if (!completedInitiatorExchange.equals(completedAcceptorExchange)) {
            throw new IllegalStateException(
                    "Exchange request does not match for exchanger and exchangee.");
        }

        updateUserRewardPoints(initiatorExchange.getSenderUserId(),
                /* incrementInRewardPoints= */1);
        updateUserRewardPoints(acceptorExchange.getSenderUserId(),
                /* incrementInRewardPoints= */1);

        return completedInitiatorExchange;
    }

    public Exchange borrowProduct(int exchangeId, BorrowProductRequest request) {
        Exchange exchange = getExchange(exchangeId);

        Exchange completedExchange = exchange.builder()
                .status(Status.BOOK_BORROWED)
                .receiverProductId(null)
                .receiverUserId(request.getBorrowerUserId())
                .build();

        exchangesRepository.save(completedExchange);

        updateUserRewardPoints(exchange.getSenderUserId(), /* incrementInRewardPoints= */1);
        updateUserRewardPoints(request.getBorrowerUserId(), /* incrementInRewardPoints= */ -1);

        return completedExchange;
    }

    public Exchange getExchange(int exchangeId) {
        return exchangesRepository.findById(exchangeId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Exchange with id %d not found", exchangeId)));
    }

    public List<Exchange> getAllExchanges() {
        return StreamSupport.stream(exchangesRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    // ---------------------- Helper Methods ------------------------------

    private Exchange markExchangeAsCompleted(Exchange exchange,
            Integer receiverProductId, Integer receiverUserId) {
        return exchangesRepository.save(exchange.builder()
                .status(Status.EXCHANGE_COMPLETED)
                .receiverProductId(receiverProductId)
                .receiverUserId(receiverUserId)
                .build());
    }

    private User updateUserRewardPoints(int userId, int incrementInRewardPoints) {
        User user = userService.getUser(userId);
        user.setRewardPoints(user.getRewardPoints() + incrementInRewardPoints);
        return userService.updateUser(user);
    }
}
