package Project.dao;

import Project.model.Room;
import Project.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO implements RoomDAOInterface {

    @Override
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO Rooms (room_name, capacity, location, fixed_equipment) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getLocation());
            ps.setString(4, room.getFixedEquipment());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi thêm phòng: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateRoom(Room room) {
        String sql = "UPDATE Rooms SET room_name = ?, capacity = ?, location = ?, fixed_equipment = ? WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getLocation());
            ps.setString(4, room.getFixedEquipment());
            ps.setInt(5, room.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật phòng: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM Rooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi xóa phòng: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM Rooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomName(rs.getString("room_name"));
                room.setCapacity(rs.getInt("capacity"));
                room.setLocation(rs.getString("location"));
                room.setFixedEquipment(rs.getString("fixed_equipment"));
                room.setCreatedAt(rs.getString("created_at"));
                room.setUpdatedAt(rs.getString("updated_at"));
                return room;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy phòng theo id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms ORDER BY room_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomName(rs.getString("room_name"));
                room.setCapacity(rs.getInt("capacity"));
                room.setLocation(rs.getString("location"));
                room.setFixedEquipment(rs.getString("fixed_equipment"));
                room.setCreatedAt(rs.getString("created_at"));
                room.setUpdatedAt(rs.getString("updated_at"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách phòng: " + e.getMessage());
        }
        return rooms;
    }
}