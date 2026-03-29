package Project.dao;

import Project.model.User;

import java.util.List;

public interface UserDAOInterface {
    boolean usernameExists(String username);

    boolean addUser(User user);

    User getUserByUsername(String username);

    List<User> getAllUsers();
}