package Project.dao;

import Project.model.Equipment;
import Project.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO implements EquipmentDAOInterface {

    @Override
    public List<Equipment> getAllEquipments() {
        List<Equipment> equipments = new ArrayList<>();
        String sql = "SELECT * FROM Equipment ORDER BY equipment_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipment equipment = new Equipment();
                equipment.setEquipmentId(rs.getInt("equipment_id"));
                equipment.setEquipmentName(rs.getString("equipment_name"));
                equipment.setTotalQuantity(rs.getInt("total_quantity"));
                equipment.setAvailableQuantity(rs.getInt("available_quantity"));
                equipment.setStatus(rs.getString("status"));
                equipment.setCreatedAt(rs.getString("created_at"));
                equipment.setUpdatedAt(rs.getString("updated_at"));
                equipments.add(equipment);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách thiết bị: " + e.getMessage());
        }
        return equipments;
    }

    @Override
    public Equipment getEquipmentById(int equipmentId) {
        String sql = "SELECT * FROM Equipment WHERE equipment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, equipmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Equipment equipment = new Equipment();
                equipment.setEquipmentId(rs.getInt("equipment_id"));
                equipment.setEquipmentName(rs.getString("equipment_name"));
                equipment.setTotalQuantity(rs.getInt("total_quantity"));
                equipment.setAvailableQuantity(rs.getInt("available_quantity"));
                equipment.setStatus(rs.getString("status"));
                equipment.setCreatedAt(rs.getString("created_at"));
                equipment.setUpdatedAt(rs.getString("updated_at"));
                return equipment;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy thiết bị theo id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateAvailableQuantity(int equipmentId, int availableQuantity) {
        String sql = "UPDATE Equipment SET available_quantity = ?, status = ? WHERE equipment_id = ?";

        Equipment equipment = getEquipmentById(equipmentId);
        if (equipment == null) {
            return false;
        }

        String status = availableQuantity > 0 ? "Available" : "Unavailable";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, availableQuantity);
            ps.setString(2, status);
            ps.setInt(3, equipmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật số lượng khả dụng: " + e.getMessage());
            return false;
        }
    }
}