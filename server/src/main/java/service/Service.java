package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData registerUser(UserData user) throws DataAccessException {
        if (dataAccess.getUserByUsername(user.username()) != null) {
            throw (new DataAccessException("403"));
        }
        var registeredUser = dataAccess.createUser(user);
        return dataAccess.createAuth(registeredUser.username());
    }
}
