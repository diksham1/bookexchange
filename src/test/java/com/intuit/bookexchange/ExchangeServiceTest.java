package com.intuit.bookexchange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import com.intuit.bookexchange.dto.BorrowProductRequest;
import com.intuit.bookexchange.dto.CompleteExchangeRequest;
import com.intuit.bookexchange.dto.InitiateExchangeRequest;
import com.intuit.bookexchange.dto.InitiateExchangeResponse;
import com.intuit.bookexchange.entities.Book;
import com.intuit.bookexchange.entities.Exchange;
import com.intuit.bookexchange.entities.User;
import com.intuit.bookexchange.exceptions.ExchangeNotPossibleException;
import com.intuit.bookexchange.exceptions.NotFoundException;
import com.intuit.bookexchange.services.ExchangeService;
import com.intuit.bookexchange.services.UserService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ExchangeServiceTest {

	private static final int USER_ID_1 = 1;
	private static final int USER_ID_2 = 2;

	private static final String BOOK_1 = "Book 1";
	private static final String BOOK_2 = "Book 2";

	private static final String AUTHOR_1 = "Author 1";
	private static final String AUTHOR_2 = "Author 2";

	private static final User USER_1 = User.builder()
			.id(USER_ID_1)
			.build();

	private static final User USER_2 = User.builder()
			.id(USER_ID_2)
			.build();

	private static final InitiateExchangeRequest INITIATE_EXCHANGE_REQUEST_USER_1 = InitiateExchangeRequest
			.builder()
			.ownerUserId(USER_ID_1)
			.bookName(BOOK_1)
			.authorName(AUTHOR_1)
			.build();

	private static final InitiateExchangeRequest INITIATE_EXCHANGE_REQUEST_USER_2 = InitiateExchangeRequest
			.builder()
			.ownerUserId(USER_ID_2)
			.authorName(AUTHOR_2)
			.bookName(BOOK_2)
			.build();

	@Autowired
	ExchangeService exchangeService;

	@MockBean
	UserService mockUserService;

	@BeforeEach
	public void setUp() {
		when(mockUserService.getUser(USER_ID_1)).thenReturn(USER_1);
		when(mockUserService.getUser(USER_ID_2)).thenReturn(USER_2);
	}

	@Test
	public void initiateExchange_successful() {
		// Arrange & Act.
		InitiateExchangeResponse response = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_1);

		// Assert.
		Book book = (Book) response.getExchangeProduct();
		assertEquals(USER_ID_1, response.getExchangeProduct().getOwnerId());
		assertEquals(BOOK_1, book.getName());
		assertEquals(AUTHOR_1, book.getAuthorName());
	}

@Test
public void completeExchange_successful() {
		// Arrange.
		when(mockUserService.awardRewardPoints(USER_ID_1, /* rewaardPoints= */1)).thenReturn(USER_1);
		when(mockUserService.awardRewardPoints(USER_ID_2, /* rewaardPoints= */1)).thenReturn(USER_2);

		int exchangeRequestId1 = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_1).getExchangeRequestId();
		int exchangeRequestId2 = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_2).getExchangeRequestId();
		
		CompleteExchangeRequest completeExchangeRequest = CompleteExchangeRequest.builder()
						.acceptorExchangeRequestId(exchangeRequestId2)
						.build();

		// Act.
		Exchange completedExchange = exchangeService.completeExchange(exchangeRequestId1, completeExchangeRequest);

		// Assert.
		assertEquals(Exchange.Status.EXCHANGE_COMPLETED, completedExchange.getStatus());
		assertEquals(USER_ID_2, completedExchange.getReceiverUserId());
		verify(mockUserService).awardRewardPoints(USER_ID_1, /* rewaardPoints= */1);
		verify(mockUserService).awardRewardPoints(USER_ID_2, /* rewaardPoints= */1);
}

	@Test
	public void completeExchange_exchangeNotFound_throwsNotFoundException() throws Exception {
		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> exchangeService.completeExchange(/* initiatorExchangeRequestId= */ 299,
						CompleteExchangeRequest.builder().build()));
		assertEquals(String.format("Exchange with id %d not found", 299), exception.getMessage());
	}

	@Test
	public void completeExchange_exchangeNotInPendingState_throwsExchangeNotPossibleException() throws Exception {
		// Arrange.
		int exchangeRequestId1 = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_1).getExchangeRequestId();
		int exchangeRequestId2 = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_2).getExchangeRequestId();

		CompleteExchangeRequest completeExchangeRequest = CompleteExchangeRequest.builder()
				.acceptorExchangeRequestId(exchangeRequestId2)
				.build();

		exchangeService.completeExchange(exchangeRequestId1, completeExchangeRequest);

		// Act & Assert.
		assertThrows(ExchangeNotPossibleException.class,
				() -> exchangeService.completeExchange(exchangeRequestId1, completeExchangeRequest));
	}

	@Test
	public void borrowProduct_successful() {
		// Arrange.
		User borrower = User.builder()
				.id(USER_ID_2)
				.rewardPoints(2)
				.build();

		when(mockUserService.getUser(USER_ID_2)).thenReturn(borrower);

		when(mockUserService.awardRewardPoints(USER_ID_1, /* rewaardPoints= */1)).thenReturn(USER_1);
		when(mockUserService.awardRewardPoints(USER_ID_2, /* rewaardPoints= */-1)).thenReturn(USER_2);

		int exchangeRequestId1 = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_1)
				.getExchangeRequestId();

		BorrowProductRequest borrowProductRequest = BorrowProductRequest.builder()
				.borrowerUserId(USER_ID_2)
				.build();

		// Act.
		Exchange exchange = exchangeService.borrowProduct(exchangeRequestId1, borrowProductRequest);

		// Assert.
		assertEquals(Exchange.Status.BOOK_BORROWED, exchange.getStatus());
		assertEquals(USER_ID_2, exchange.getReceiverUserId());
		verify(mockUserService).awardRewardPoints(USER_ID_1, /* rewaardPoints= */1);
		verify(mockUserService).awardRewardPoints(USER_ID_2, /* rewaardPoints= */-1);
	}

	@Test
	public void borrowProduct_exchangeNotFound_throwsNotFoundException()
			throws Exception {
		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> exchangeService.borrowProduct(/* exchangeId= */ 2,
						BorrowProductRequest.builder().build()));
		assertEquals(String.format("Exchange with id %d not found", 2), exception.getMessage());
	}

	@Test
	public void borrowProduct_exchangeNotInPendingState_throwsExchangeNotPossibleException()
			throws Exception {
		// Arrange.
		User borrower = User.builder()
				.id(USER_ID_2)
				.rewardPoints(2)
				.build();

		when(mockUserService.getUser(USER_ID_2)).thenReturn(borrower);

		int exchangeRequestId1 = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_1)
				.getExchangeRequestId();
		int exchangeRequestId2 = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_2)
				.getExchangeRequestId();

		exchangeService.completeExchange(exchangeRequestId1, CompleteExchangeRequest.builder()
				.acceptorExchangeRequestId(exchangeRequestId2)
				.build());

		BorrowProductRequest borrowProductRequest = BorrowProductRequest.builder().borrowerUserId(USER_ID_2)
				.build();

		// Act & Assert.
		assertThrows(ExchangeNotPossibleException.class,
				() -> exchangeService.borrowProduct(exchangeRequestId1, borrowProductRequest));
	}

	@Test
	public void borrowProduct_userRewardPointsNotSufficient_throwsExchangeNotPossibleException()
			throws Exception {
		// Arrange.
		int exchangeId = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_1)
				.getExchangeRequestId();

		// Act & Assert.
		assertThrows(ExchangeNotPossibleException.class,
				() -> exchangeService.borrowProduct(/* exchangeId= */ exchangeId,
						BorrowProductRequest.builder().borrowerUserId(USER_ID_2).build()));
	}

	@Test
	public void getExchange_successful() {
		int exchangeId = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_1)
				.getExchangeRequestId();

		Exchange exchange = exchangeService.getExchange(exchangeId);

		assertEquals(exchangeId, exchange.getId());
		assertEquals(USER_ID_1, exchange.getSenderUserId());
	}

	@Test
	public void getExchange_exchangeNotFound_throwsNotFoundException()
			throws Exception {
		assertThrows(NotFoundException.class,
				() -> exchangeService.getExchange(/* exchangeId= */ 23));
	}

	@Test
	public void getAllExchanges_successful() {
		// Arrange.
		int exchangeRequestId1 = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_1)
				.getExchangeRequestId();
		int exchangeRequestId2 = exchangeService.initiateExchange(INITIATE_EXCHANGE_REQUEST_USER_2)
				.getExchangeRequestId();

		// Act.
		List<Exchange> exchanges = exchangeService.getAllExchanges();

		// Assert.
		assertEquals(2, exchanges.size());
		assertEquals(exchangeRequestId1, exchanges.get(0).getId());
		assertEquals(exchangeRequestId2, exchanges.get(1).getId());
	}

	@Test
	public void getAllExchanges_noExchangeExists_returnsEmptyList() {
		List<Exchange> exchanges = exchangeService.getAllExchanges();

		assertEquals(0, exchanges.size());
	}
}
