package service;

import dataaccess.UserDataAccess;
import dataaccess.UserMemoryDAO;
import model.UserData;

public class UserService {
    private UserDataAccess dataAccess;

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

        return user;
    }
}
