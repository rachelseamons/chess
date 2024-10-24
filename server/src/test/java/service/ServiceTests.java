package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServiceTests {
    static private final Service service = new Service(new MemoryDataAccess());
    static private final UserData userFred = new UserData("Fred", "password", "@me");
    static private final UserData userSue = new UserData("Sue", "pass", "@you");

    @Test
    @DisplayName("Register user")
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
}