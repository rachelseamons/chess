package service;

import dataaccess.UserDAO;
import dataaccess.UserMemoryDAO;
import model.UserData;

public class UserService {
    private UserDAO dataAccess;

    public UserService(boolean useSQLDatabase) {
        if (useSQLDatabase) {
            //TODO:: add UserSQLDAO
        } else {
            dataAccess = new UserMemoryDAO();
        }
    }

    public Object registerUser(UserData user) {
        if (dataAccess.getUserByUsername(user.username()) == null) {
            return null;
        }

        dataAccess.createUser(user);
        return user;
    }
}
