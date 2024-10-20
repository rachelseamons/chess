package service;

import dataaccess.UserDataAccess;
import dataaccess.UserMemoryDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserDataAccessTests {
    UserDataAccess dataAccess = new UserMemoryDAO();

    @Test
    @DisplayName("getUserByUsername with empty set")
    public void emptySet() {
        Assertions.assertNull(dataAccess.getUserByUsername("test"));
    }

}
