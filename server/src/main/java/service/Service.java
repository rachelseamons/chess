package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData registerUser(UserData user) throws ChessException {
        if (dataAccess.getUserByUsername(user.username()) != null) {
            throw (new ChessException("already taken", 403));
        }
        var registeredUser = dataAccess.createUser(user);
        return dataAccess.createAuth(registeredUser.username());
    }

    public void clear() {
        dataAccess.clear();
    }
}
