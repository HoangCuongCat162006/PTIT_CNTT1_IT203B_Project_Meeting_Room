package Project.presentation;

import Project.util.DatabaseConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                System.out.println("Kết nối MySQL thành công.");
            }
        } catch (Exception e) {
            System.out.println("Kết nối MySQL thất bại: " + e.getMessage());
            return;
        }

        UserMenu.showMainMenu();
    }
}