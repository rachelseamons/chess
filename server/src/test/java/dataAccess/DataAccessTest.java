package dataAccess;

import model.Game;
import model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {
    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws DataAccessException {
        DataAccess db;
        if (databaseClass == DataAccessMemory.class) {
            db = new DataAccessMemory();
        } else {
            //when implementing the SQL Database, change this to that implementation
            db = new DataAccessMemory();
        }

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

        var fred = new User("Fred", "pass", "@gmail");
        var carl = new User("Carl", "pass", "@yahoo");
        var alex = new User("Alex", "pass", "@gmail");

        dataAccess.createUser(fred);
        dataAccess.createUser(carl);
        dataAccess.createUser(alex);

        var authToken = dataAccess.login("Alex");
        dataAccess.createGame(authToken, "My Game");
        dataAccess.createGame(authToken, "My Game 2");
        dataAccess.createGame(authToken, "My Game 3");

        assertDoesNotThrow(dataAccess::clear);
        authToken = dataAccess.login("Alex");
        assertEquals(0, dataAccess.getGames(authToken).size());
    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("Verify User")
    void verifyUser(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);
        var fred = new User("Fred", "pass", "@gmail");
        dataAccess.createUser(fred);

        assertTrue(dataAccess.verifyUser("Fred", "pass"));
    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("Logout")
    void logout(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);
        var fred = new User("Fred", "pass", "@gmail");
        dataAccess.createUser(fred);
        var authToken = dataAccess.login("Fred");

        assertDoesNotThrow(() -> dataAccess.logout(authToken));
    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("Create Game")
    void createGame(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);
        dataAccess.createUser(new User("Fred", "pass"));
        var authToken = dataAccess.login("Fred");

        assertDoesNotThrow(() -> dataAccess.createGame(authToken, "My Game"));
        assertNotNull(dataAccess.createGame(authToken, "My Game 2"));
    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("Get Games")
    void getGames(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);
        dataAccess.createUser(new User("Fred", "pass"));
        var authToken = dataAccess.login("Fred");

        assertEquals(0, dataAccess.getGames(authToken).size());

        dataAccess.createGame(authToken, "My Game");
        dataAccess.createGame(authToken, "My Game 2");
        dataAccess.createGame(authToken, "My Game 3");

        var expected = new HashMap<Integer, Game>();
        expected.put(0, new Game("My Game"));
        expected.put(1, new Game("My Game 2"));
        expected.put(2, new Game("My Game 3"));

        var actual = dataAccess.getGames(authToken);
        assertEquals(expected, actual);
    }
}