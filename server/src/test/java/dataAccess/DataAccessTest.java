package dataAccess;

import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import service.ChessService;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {
    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws DataAccessException {
        DataAccess db;
        //when implementing the database, add an if statement here to choose which implementation you want to use
        db = new DataAccessMemory();

        return db;
    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("Create User")
    void createUser(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);

        assertDoesNotThrow(() -> dataAccess.createUser(new User("Fred", "pass", "@gmail")));
    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("Login")
    void login(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);

        assertDoesNotThrow(() -> dataAccess.login("Fred"));
        assertNotNull(dataAccess.login("Fred"));
    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("User Exists")
    void userExists(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);

        var fred = new User("Fred", "pass", "@gmail");
        var carl = new User("Carl", "pass", "@yahoo");
        var alex = new User("Alex", "pass", "@gmail");

        dataAccess.createUser(fred);
        dataAccess.createUser(carl);
        dataAccess.createUser(alex);

        assertTrue(dataAccess.userExists(fred));
        assertTrue(dataAccess.userExists(carl));
        assertTrue(dataAccess.userExists(alex));

        var kenny = new User("Kenny", "pass", "@gmail");
        assertFalse(dataAccess.userExists(kenny));

        dataAccess.createUser(kenny);
        assertTrue(dataAccess.userExists(kenny));
    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("Clear Database")
    void clear(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);

    }
}