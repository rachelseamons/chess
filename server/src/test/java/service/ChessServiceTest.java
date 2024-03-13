package service;

import dataAccess.DataAccessException;
import dataAccess.DataAccessMemory;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ChessServiceTest {

    static private ChessService service;

    @BeforeAll
    public static void init() {
        var dataAccess = new DataAccessMemory();
        service = new ChessService(dataAccess);
    }

    @Test
    void clear() throws DataAccessException {
        service.clear();

    }

    @Test
    void registerUser() throws DataAccessException {
        var user = new User("Fred", "x");
        service.registerUser(user);
    }
}