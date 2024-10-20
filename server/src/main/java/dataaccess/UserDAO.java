package dataaccess;

import model.UserData;

public interface UserDAO {
    String getUserByUsername(String username);

    void createUser(UserData user);
}
