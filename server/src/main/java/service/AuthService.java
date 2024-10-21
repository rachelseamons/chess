package service;

import dataaccess.AuthDAO;
import dataaccess.AuthMemoryDAO;
import dataaccess.DataAccessException;

public class AuthService {
    private AuthDAO dataAccess;

    public AuthService(boolean useSQLDatabase) {
        if (useSQLDatabase) {
            //TODO:: add AuthSQLDAO
        } else {
            dataAccess = new AuthMemoryDAO();
        }
    }

    public Object createAuth(String username) throws DataAccessException {
        return dataAccess.createAuth(username);
    }
}
