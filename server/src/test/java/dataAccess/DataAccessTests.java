package dataAccess;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DataAccessTests {
    private DataAccess dataAccess = new MemoryDataAccess();
    private UserData Fred = new UserData("Fred", "password", "@me");

    @Test
    @DisplayName("get user with empty set")
    public void emptySet() {
        Assertions.assertNull(dataAccess.getUserByUsername("test"));
    }

    @Test
    @DisplayName("add user Fred")
    public void createFred() {
        dataAccess.createUser(Fred);
        Assertions.assertEquals("Fred", dataAccess.getUserByUsername("Fred").username());
    }

    @Test
    @DisplayName("clear database")
    public void clearDatabase() {
        dataAccess.clear();
        //Assertions.assertTrue(dataAccess.users.isEmpty());
        //TODO:: is there a way to access the private variables of dataAccess to check things like this?
    }
}
