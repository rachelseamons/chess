package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserServiceTests {
    static private final Service service = new Service(new MemoryDataAccess());
    static private final UserData userFred = new UserData("Fred", "password", "@me");

    @Test
    @DisplayName("Register user")
    public void registerUserSuccess() throws DataAccessException {
        //TODO:: need clear so that if this runs second, it's not a problem
        var userAuth = service.registerUser(userFred);
        Assertions.assertEquals(userFred.username(), userAuth.username());
        Assertions.assertNotNull(userAuth.authToken());
    }


    @Test
    @DisplayName("Fail to register existing user")
    public void registerUserFail() throws DataAccessException {
        service.registerUser(userFred);
        Exception exception = Assertions.assertThrows(DataAccessException.class, () ->
                service.registerUser(userFred));

        String expectedMessage = "403";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);
    }
}
