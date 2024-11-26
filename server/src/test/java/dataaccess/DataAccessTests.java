package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class DataAccessTests {
    private final DataAccess dataAccess = new SQLDataAccess();
    private final UserData userFred = new UserData("Fred", "password", "@me");

    public DataAccessTests() throws DataAccessException {
    }

    @Test
    @DisplayName("get user with empty set")
    public void emptySet() {
        Assertions.assertNull(dataAccess.getUserByUsername("test"));
    }

    @Test
    @DisplayName("add user Fred")
    public void createFred() throws Exception {
        var username = UUID.randomUUID().toString();
        UserData newUser = new UserData(username, "password", "@me");
        var createdUser = dataAccess.createUser(newUser);
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(username, createdUser.username());

        var user = dataAccess.getUserByUsername(username);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(username, user.username());
    }
}
