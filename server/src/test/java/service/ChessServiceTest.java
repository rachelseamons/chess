package service;

import dataAccess.DataAccessException;
import dataAccess.DataAccessMemory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

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
    void registerUser() {
    }
}