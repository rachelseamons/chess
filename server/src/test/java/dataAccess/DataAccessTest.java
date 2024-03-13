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
    @DisplayName("Clear Database")
    void clear(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);

    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("User Exists")
    boolean userExists(Class<? extends DataAccess> dbClass) throws DataAccessException {
    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("Create User")
    void createUser(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);

    }

    @ParameterizedTest
    @ValueSource(classes = DataAccessMemory.class)
    @DisplayName("Login")
    Integer login(Class<? extends DataAccess> dbClass) throws DataAccessException {
        var dataAccess = getDataAccess(dbClass);
    }
}