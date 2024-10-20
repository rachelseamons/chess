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
        var service = new UserService(false);
        var Fred = new UserData("Fred", "password", "@me");
    }

    @Test
    @DisplayName("Register user")
}
