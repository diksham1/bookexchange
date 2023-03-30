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
import com.intuit.bookexchange.dto.CreateUserRequest;
import com.intuit.bookexchange.entities.User;
import com.intuit.bookexchange.exceptions.InvalidCredentialsException;
import com.intuit.bookexchange.exceptions.NotFoundException;
import com.intuit.bookexchange.services.UserService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserServiceTest {

    private static final int USER_ID_1 = 1;

    private static final String USER_NAME_1 = "Diksha";
    private static final String USER_NAME_2 = "Tux";

    private static final String EMAIL_ID_1 = "diksha@gmail.com";
    private static final String EMAIL_ID_2 = "tux@gmail.com";

    @Autowired
    UserService userService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void createUser_success() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(USER_NAME_1)
                .password("password")
                .email(EMAIL_ID_1)
                .build();

        User user = userService.createUser(createUserRequest);

        assertEquals(USER_NAME_1, user.getUsername());
        assertEquals(EMAIL_ID_1, user.getEmail());
        assertEquals(0, user.getRewardPoints());
        assertEquals(5.0, user.getRating());
    }

    @Test
    public void loginUser_success() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(USER_NAME_1)
                .password("password")
                .email(EMAIL_ID_1)
                .build();
        userService.createUser(createUserRequest);

        User user = userService.loginUser(USER_NAME_1, "password");

        assertEquals(USER_NAME_1, user.getUsername());
        assertEquals(EMAIL_ID_1, user.getEmail());
        assertEquals(0, user.getRewardPoints());
        assertEquals(5.0, user.getRating());
    }

    @Test
    public void loginUser_invalidPassword_throwsInvalidCredentialsException() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(USER_NAME_1)
                .password("password")
                .email(EMAIL_ID_1)
                .build();
        userService.createUser(createUserRequest);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.loginUser(USER_NAME_1, "wrong_password"));
    }

    @Test
    public void getUser_searchByUserId_success() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(USER_NAME_1)
                .password("password")
                .email(EMAIL_ID_1)
                .build();
        userService.createUser(createUserRequest);

        User user = userService.getUser(USER_ID_1);

        assertEquals(USER_NAME_1, user.getUsername());
        assertEquals(EMAIL_ID_1, user.getEmail());
        assertEquals(0, user.getRewardPoints());
        assertEquals(5.0, user.getRating());
    }

    @Test
    public void getUser_searchByUserId_userDoesNotExist_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.getUser(USER_ID_1));
    }

    @Test
    public void getUser_searchByUsername_success() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(USER_NAME_1)
                .password("password")
                .email(EMAIL_ID_1)
                .build();
        userService.createUser(createUserRequest);

        User user = userService.getUser(USER_NAME_1);

        assertEquals(USER_NAME_1, user.getUsername());
        assertEquals(EMAIL_ID_1, user.getEmail());
        assertEquals(0, user.getRewardPoints());
        assertEquals(5.0, user.getRating());
    }

    @Test
    public void getUser_searchByUsername_userDoesNotExist_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.getUser(USER_NAME_1));
    }

    @Test
    public void getAllUsers_success() {
        // Arrange.
        CreateUserRequest createUserRequest1 = CreateUserRequest.builder()
                .username(USER_NAME_1)
                .password("password")
                .email(EMAIL_ID_1)
                .build();
        userService.createUser(createUserRequest1);

        CreateUserRequest createUserRequest2 = CreateUserRequest.builder()
                .username(USER_NAME_2)
                .password("password")
                .email(EMAIL_ID_2)
                .build();
        userService.createUser(createUserRequest2);

        // Act.
        List<User> users = userService.getAllUsers();

        // Assert.
        assertEquals(2, users.size());
        assertEquals(USER_NAME_1, users.get(0).getUsername());
        assertEquals(EMAIL_ID_1, users.get(0).getEmail());
        assertEquals(0, users.get(0).getRewardPoints());
        assertEquals(5.0, users.get(0).getRating());
        assertEquals(USER_NAME_2, users.get(1).getUsername());
        assertEquals(EMAIL_ID_2, users.get(1).getEmail());
        assertEquals(0, users.get(1).getRewardPoints());
        assertEquals(5.0, users.get(1).getRating());
    }

    @Test
    public void getAllUsers_noUsersExist_returnsEmptyList() {
        assertEquals(0, userService.getAllUsers().size());
    }

    @Test
    public void updateUser_success() {
        // Arrange.
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(USER_NAME_1)
                .password("password")
                .email(EMAIL_ID_1)
                .build();
        userService.createUser(createUserRequest);

        // Act.
        User user = userService.getUser(USER_ID_1);
        user.setUsername(USER_NAME_2);
        userService.updateUser(user);

        // Assert.
        User updatedUser = userService.getUser(USER_ID_1);
        assertEquals(USER_NAME_2, updatedUser.getUsername());
    }

    @Test
    public void awardRewardPoints_success() {
        // Arrange.
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(USER_NAME_1)
                .password("password")
                .email(EMAIL_ID_1)
                .build();
        userService.createUser(createUserRequest);

        // Act.
        userService.awardRewardPoints(USER_ID_1, 10);

        // Assert.
        User user = userService.getUser(USER_ID_1);
        assertEquals(10, user.getRewardPoints());
    }

    @Test
    public void awardRewardPoints_userDoesNotExist_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.awardRewardPoints(USER_ID_1, 10));
    }
}
