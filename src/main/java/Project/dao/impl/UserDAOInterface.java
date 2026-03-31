package Project.dao.impl;

import Project.model.User;

import java.util.List;

public interface UserDAOInterface {
    boolean usernameExists(String username);

    boolean userIdExists(int userId);

    boolean addUser(User user);

    boolean updateUserProfile(User user);

    User getUserById(int userId);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    List<User> getUsersByRole(String role);
}