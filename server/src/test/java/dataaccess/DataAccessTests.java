package dataaccess;

import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.ChessException;

import java.util.UUID;

public class DataAccessTests {
    private final DataAccess dataAccess = new SQLDataAccess();

    public DataAccessTests() throws DataAccessException {
    }

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess.clear();
    }

    @Test
    @DisplayName("fail to get user with empty set")
    public void emptySet() throws ChessException {
        dataAccess.clear();
        Assertions.assertNull(dataAccess.getUserByUsername("test"));
    }

    @Test
    @DisplayName("get user by username success")
    public void getExistingUser() throws ChessException {
        var username = UUID.randomUUID().toString();
        UserData newUser = new UserData(username, "password", "@me");
        dataAccess.createUser(newUser);

        var user = dataAccess.getUserByUsername(username);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(username, user.username());
    }

    @Test
    @DisplayName("add user success")
    public void createUser() throws Exception {
        var username = UUID.randomUUID().toString();
        UserData newUser = new UserData(username, "password", "@me");
        var createdUser = dataAccess.createUser(newUser);
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(username, createdUser.username());

        var user = dataAccess.getUserByUsername(username);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(username, user.username());
    }

    @Test
    @DisplayName("fail to add existing user")
    public void createUserFail() throws Exception {
        var username = UUID.randomUUID().toString();
        UserData newUser = new UserData(username, "password", "@me");
        dataAccess.createUser(newUser);

        ChessException exception = Assertions.assertThrows(ChessException.class, () -> dataAccess.createUser(newUser));
        String expectedMessage = "already taken";
        String actualMessage = exception.getMessage();
        int expectedStatus = 403;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("create auth success")
    public void createAuth() throws Exception {
        var username = UUID.randomUUID().toString();
        AuthData newAuth = dataAccess.createAuth(username);

        Assertions.assertNotNull(newAuth);
        Assertions.assertEquals(username, newAuth.username());
    }

    @Test
    @DisplayName("fail to create auth with no username")
    public void failCreateAuth() throws Exception {
        ChessException exception = Assertions.assertThrows(ChessException.class,
                () -> dataAccess.createAuth(null));
        String expectedMessage = "bad request";
        String actualMessage = exception.getMessage();
        int expectedStatus = 400;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }
}
