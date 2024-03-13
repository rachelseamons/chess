package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.DataAccessMemory;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessServiceTest {

    static private ChessService service;

    @BeforeAll
    public static void init() {
        //this is using the memory implementation; should work the same with SQL
        var dataAccess = new DataAccessMemory();
        service = new ChessService(dataAccess);
    }

    @Test
    @DisplayName("Register User")
    void register() throws DataAccessException {
        var fred = new User("Fred", "pass", "@gmail");
        var carl = new User("Carl", "pass", "@gmail");

        assertDoesNotThrow(() -> service.registerUser(fred));
        assertNotNull(service.registerUser(carl));
    }

    @Test
    @DisplayName("Register Pre-existing User")
    void badRegister() throws DataAccessException {
        var fred = new User("Fred", "pass", "@gmail");

        service.registerUser(fred);
        Exception exception = assertThrows(DataAccessException.class, () ->
                service.registerUser(fred));

        String expectedMessage = "Error: already exists";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}