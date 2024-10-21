package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.UserMemoryDAO;
import model.UserData;

import javax.xml.crypto.Data;

public class UserService {
    private UserDAO dataAccess;

    public UserService(boolean useSQLDatabase) {
        if (useSQLDatabase) {
            //TODO:: add UserSQLDAO
        } else {
            dataAccess = new UserMemoryDAO();
        }
    }

    public Object registerUser(UserData user) throws DataAccessException {
        if (dataAccess.getUserByUsername(user.username()) != null) {
            throw (new DataAccessException("403"));
        }

        dataAccess.createUser(user);
        return user;
    }
}
