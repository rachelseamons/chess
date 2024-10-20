package service;

import dataaccess.UserDataAccess;
import dataaccess.UserMemoryDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class UserDataAccessTests {
    private UserDataAccess dataAccess = new UserMemoryDAO();
    private UserData Fred = new UserData("Fred","password","@me");

    @Test
    @DisplayName("get user with empty set")
    public void emptySet() {
        Assertions.assertNull(dataAccess.getUserByUsername("test"));
    }

    @Test
    @DisplayName("add user Fred")
    public void createFred() {
        dataAccess.createUser(Fred);
        Assertions.assertEquals("Fred", dataAccess.getUserByUsername("Fred"));
    }
}
