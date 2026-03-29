package Project.service;

import Project.model.User;

import java.util.List;

public interface UserServiceInterface {
    boolean registerEmployee(String username, String password, String email, String phone);
    User login(String username, String password);
    boolean createSupportStaff(String username, String password, String email, String phone);
    void ensureDefaultAdmin();
    List<User> getAllUsers();
}