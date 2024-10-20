package dataaccess;

import model.UserData;

import java.util.HashSet;
import java.util.Set;

public class UserMemoryDAO implements UserDataAccess {
    private Set<UserData> userData = new HashSet<>();

    public String getUserByUsername(String username) {
        for (UserData user : userData) {
            if (user.username().equals(username)) {
                return username;
            }
        }
        return null;
    }

    public void createUser(UserData user) {
        userData.add(user);
    }
}
