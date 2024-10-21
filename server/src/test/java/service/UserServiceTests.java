package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.UserMemoryDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserServiceTests {
    static private final UserService service = new UserService(false);
    static private final UserData Fred = new UserData("Fred", "password", "@me");

    @Test
    @DisplayName("Register user")
    public void registerUserSuccess() throws DataAccessException {
        Assertions.assertEquals(Fred, service.registerUser(Fred));
    }

    @Test
    @DisplayName("Fail to register existing user")
    public void registerUserFail() throws DataAccessException {
        service.registerUser(Fred);
        Exception exception = Assertions.assertThrows(DataAccessException.class, () ->
                service.registerUser(Fred));

        String expectedMessage = "403";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(expectedMessage, actualMessage);
    }
}
