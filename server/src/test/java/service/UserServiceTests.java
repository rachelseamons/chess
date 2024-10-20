package service;

import dataaccess.UserDAO;
import dataaccess.UserMemoryDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserServiceTests {
    @BeforeAll
    public static void init() {
        UserService service = new UserService(false);
        var Fred = new UserData("Fred", "password", "@me");
    }

    @Test
    @DisplayName("Register user")
    public void registerUserSuccess() {
        //TODO:: init is not persisting to here, so might need to use private class variables instead
        service.registerUser(Fred);
    }
}
