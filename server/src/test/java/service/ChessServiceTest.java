package service;

import dataAccess.DataAccessException;
import dataAccess.DataAccessMemory;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessServiceTest {

    static private ChessService service;

    @BeforeAll
    public static void init() {
        //this is using the memory implementation; should work the same with SQL
        var dataAccess = new DataAccessMemory();
        service = new ChessService(dataAccess);
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        service.clear();
    }

    @Test
    @DisplayName("Register User")
    void register() throws DataAccessException {
        var fred = new User("Fred", "pass", "@gmail");
        var carl = new User("Carl", "pass", "@gmail");

        assertDoesNotThrow(() -> service.registerUser(fred));
        assertNotNull(service.registerUser(carl));
    }

    @Test
    @DisplayName("Fail to Register Pre-existing User")
    void badRegister() throws DataAccessException {
        var fred = new User("Fred", "pass", "@gmail");

        service.registerUser(fred);
        Exception exception = assertThrows(DataAccessException.class, () ->
                service.registerUser(fred));

        String expectedMessage = "Error: already exists";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Clear Database")
    void clear() throws DataAccessException {
        var fred = new User("Fred", "pass", "@gmail");
        var carl = new User("Carl", "pass", "@gmail");

        service.registerUser(fred);
        service.registerUser(carl);

        assertDoesNotThrow(() -> service.clear());

        //TODO::expand this test once you've implemented listGames
    }

    @Test
    @DisplayName("Login")
    void login() throws DataAccessException {
        var fred = new User("Fred", "pass", "@gmail");

        service.registerUser(fred);
        assertDoesNotThrow(() -> service.login("Fred", "pass"));
    }

    @Test
    @DisplayName("Login in Non-existing User")
    void nonexistentLogin() throws DataAccessException {
        Exception exception = assertThrows(DataAccessException.class, () ->
                service.login("Fred", "pass"));

        String expectedMessage = "Error: unauthorized";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Wrong Password")
    void wrongPassword() throws DataAccessException {
        service.registerUser(new User("Fred", "pass"));

        Exception exception = assertThrows(DataAccessException.class, () ->
                service.login("Fred", "Password"));

        String expectedMessage = "Error: unauthorized";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Logout")
    void logout() throws DataAccessException {
        var fred = new User("Fred", "pass", "@gmail");

        var authToken = service.registerUser(fred);
        assertDoesNotThrow(() -> service.logout(authToken));
    }

    @Test
    @DisplayName("Logout Non-Existing User")
    void nonexistentLogout() throws DataAccessException {
        Exception exception = assertThrows(DataAccessException.class, () ->
                service.logout(1238));

        String expectedMessage = "Error: unauthorized";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        var fred = new User("Fred", "pass", "@gmail");
        var authToken = service.registerUser(fred);
        service.logout(authToken);

        exception = assertThrows(DataAccessException.class, () -> service.logout(authToken));

        actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Create Game")
    void createGame() throws DataAccessException {
        var fred = new User("Fred", "pass", "@gmail");
        var authToken = service.registerUser(fred);
        assertNotNull(service.createGame(authToken, "My Game"));
    }

    @Test
    @DisplayName("List Games")
    void listGames() throws DataAccessException {
        var fred = new User("Fred", "pass", "@gmail");
        var authToken = service.registerUser(fred);
        assertEquals(0, service.listGames(authToken).size());

        service.createGame(authToken, "My Game");
        service.createGame(authToken, "My Game 2");
        service.createGame(authToken, "My Game 3");
    }
}