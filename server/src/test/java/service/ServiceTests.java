package service;

import dataaccess.MemoryDataAccess;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServiceTests {
    static private final Service service = new Service(new MemoryDataAccess());
    static private final UserData userFred = new UserData("Fred", "password", "@me");
    static private final UserData userSue = new UserData("Sue", "pass", "@you");
    static private final UserData loginFred = new UserData("Fred", "password", null);
    static private final UserData wrongPasswordSue = new UserData("Sue", "Pass", null);

    @Test
    @DisplayName("Successful register user")
    public void registerUserSuccess() throws ChessException {
        service.clear();
        var userAuth = service.registerUser(userFred);
        Assertions.assertEquals(userFred.username(), userAuth.username());
        Assertions.assertNotNull(userAuth.authToken());
    }


    @Test
    @DisplayName("Fail to register existing user")
    public void registerUserFail() throws ChessException {
        service.clear();
        service.registerUser(userFred);
        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.registerUser(userFred));

        String expectedMessage = "already taken";
        String actualMessage = exception.getMessage();
        int expectedStatus = 403;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Clear database")
    public void clearDatabase() throws ChessException {
        //TODO:: add stuff first so you can prove it's being cleared, update as you implement functions like
        // getUserByAuth
        service.clear();

        Assertions.assertDoesNotThrow(() -> service.registerUser(userFred));
        Assertions.assertThrows(ChessException.class, () ->
                service.registerUser(userFred));

        var userAuth = service.registerUser(userSue);
        Assertions.assertEquals(userSue.username(), userAuth.username());

        Assertions.assertDoesNotThrow(service::clear);
        Assertions.assertDoesNotThrow(() -> service.registerUser(userFred));
    }

    @Test
    @DisplayName("Successful login")
    public void successfulLogin() throws ChessException {
        service.clear();
        service.registerUser(userFred);

        var userAuth = service.loginUser(userFred);
        Assertions.assertEquals(userFred.username(), userAuth.username());
        Assertions.assertNotNull(userAuth.authToken());
    }

    @Test
    @DisplayName("Login non-existent user")
    public void loginBadUser() throws ChessException {
        service.clear();
        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.loginUser(userFred));

        String expectedMessage = "unauthorized";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Login wrong password")
    public void loginWrongPassword() throws ChessException {
        service.clear();
        service.registerUser(userSue);

        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.loginUser(wrongPasswordSue));

        String expectedMessage = "unauthorized";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Successful logout")
    public void logoutSuccess() throws ChessException {
        service.clear();
        var firstAuth = service.registerUser(userFred);

        Assertions.assertDoesNotThrow(() -> service.logoutUser(firstAuth.authToken()));
        var secondAuth = Assertions.assertDoesNotThrow(() -> service.loginUser(userFred));
        Assertions.assertDoesNotThrow(() -> service.logoutUser(secondAuth.authToken()));
    }

    @Test
    @DisplayName("Logout twice")
    public void logoutTwice() throws ChessException {
        service.clear();

        var userAuth = service.registerUser(userFred);

        Assertions.assertDoesNotThrow(() -> service.logoutUser(userAuth.authToken()));
        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.logoutUser(userAuth.authToken()));

        Assertions.assertEquals(exception.getStatus(), 401);
        Assertions.assertEquals(exception.getMessage(), "unauthorized");
    }
}