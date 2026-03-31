package Project.dao.impl;

import Project.dao.EquipmentDAOInterface;
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
        List<Equipment> equipmentList = new ArrayList<Equipment>();
        String sql = "SELECT * FROM Equipment ORDER BY equipment_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                equipmentList.add(mapEquipment(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay danh sach thiet bi: " + e.getMessage());
        }

        return equipmentList;
    }

    @Override
    public Equipment getEquipmentById(int equipmentId) {
        String sql = "SELECT * FROM Equipment WHERE equipment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEquipment(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay thiet bi theo id: " + e.getMessage());
        }

        return null;
    }

    public Equipment getEquipmentById(int equipmentId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM Equipment WHERE equipment_id = ? FOR UPDATE";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEquipment(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean addEquipment(Equipment equipment) {
        String sql = "INSERT INTO Equipment (equipment_name, total_quantity, available_quantity, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipment.getEquipmentName());
            ps.setInt(2, equipment.getTotalQuantity());
            ps.setInt(3, equipment.getAvailableQuantity());
            ps.setString(4, equipment.getStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi them thiet bi: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateEquipment(Equipment equipment) {
        String sql = "UPDATE Equipment SET equipment_name = ?, total_quantity = ?, available_quantity = ?, status = ? WHERE equipment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipment.getEquipmentName());
            ps.setInt(2, equipment.getTotalQuantity());
            ps.setInt(3, equipment.getAvailableQuantity());
            ps.setString(4, equipment.getStatus());
            ps.setInt(5, equipment.getEquipmentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi cap nhat thiet bi: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteEquipment(int equipmentId) {
        String sql = "DELETE FROM Equipment WHERE equipment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi xoa thiet bi: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateAvailableQuantity(int equipmentId, int availableQuantity) {
        String sql = "UPDATE Equipment SET available_quantity = ?, status = ? WHERE equipment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, availableQuantity);
            ps.setString(2, availableQuantity > 0 ? "Available" : "Unavailable");
            ps.setInt(3, equipmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi cap nhat so luong thiet bi: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAvailableQuantity(int equipmentId, int availableQuantity, Connection conn) throws SQLException {
        String sql = "UPDATE Equipment SET available_quantity = ?, status = ? WHERE equipment_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, availableQuantity);
            ps.setString(2, availableQuantity > 0 ? "Available" : "Unavailable");
            ps.setInt(3, equipmentId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean equipmentNameExists(String equipmentName) {
        String sql = "SELECT 1 FROM Equipment WHERE LOWER(equipment_name) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipmentName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra trung ten thiet bi: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean equipmentNameExistsExceptId(String equipmentName, int equipmentId) {
        String sql = "SELECT 1 FROM Equipment WHERE LOWER(equipment_name) = LOWER(?) AND equipment_id <> ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipmentName);
            ps.setInt(2, equipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra trung ten thiet bi khi cap nhat: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hasBookingReference(int equipmentId) {
        String sql1 = "SELECT 1 FROM Bookings WHERE equipment_id = ? LIMIT 1";
        String sql2 = "SELECT 1 FROM booking_details WHERE equipment_id = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setInt(1, equipmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setInt(1, equipmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra rang buoc thiet bi: " + e.getMessage());
            return true;
        }
    }

    private Equipment mapEquipment(ResultSet rs) throws SQLException {
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
}