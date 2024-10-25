package dataAccess;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DataAccessTests {
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final UserData userFred = new UserData("Fred", "password", "@me");

    @Test
    @DisplayName("get user with empty set")
    public void emptySet() {
        Assertions.assertNull(dataAccess.getUserByUsername("test"));
    }

    @Test
    @DisplayName("add user Fred")
    public void createFred() {
        dataAccess.createUser(userFred);
        Assertions.assertEquals("Fred", dataAccess.getUserByUsername("Fred").username());
    }
}
