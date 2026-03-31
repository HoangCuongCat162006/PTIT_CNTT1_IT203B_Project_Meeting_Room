package Project.dao.impl;

import Project.dao.RoomDAOInterface;
import Project.model.Room;
import Project.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
            System.out.println("Loi them phong: " + e.getMessage());
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
            System.out.println("Loi cap nhat phong: " + e.getMessage());
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
            System.out.println("Loi xoa phong: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM Rooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRoom(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay phong theo id: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Room> getAllRooms() {
        List<Room> roomList = new ArrayList<Room>();
        String sql = "SELECT * FROM Rooms ORDER BY room_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roomList.add(mapRoom(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay danh sach phong: " + e.getMessage());
        }

        return roomList;
    }

    @Override
    public List<Room> getAvailableRooms(String startTime, String endTime) {
        List<Room> roomList = new ArrayList<Room>();

        String sql = "SELECT * " +
                "FROM Rooms r " +
                "WHERE r.room_id NOT IN ( " +
                "    SELECT b.room_id " +
                "    FROM Bookings b " +
                "    WHERE b.status IN ('PENDING', 'CONFIRMED') " +
                "      AND (? < b.end_time AND ? > b.start_time) " +
                ") " +
                "ORDER BY r.room_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(startTime));
            ps.setTimestamp(2, Timestamp.valueOf(endTime));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roomList.add(mapRoom(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi khi lay danh sach phong trong: " + e.getMessage());
        }

        return roomList;
    }

    @Override
    public List<Room> searchRoomsByName(String keyword) {
        List<Room> roomList = new ArrayList<Room>();
        String sql = "SELECT * FROM Rooms WHERE room_name LIKE ? ORDER BY room_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roomList.add(mapRoom(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi tim kiem phong theo ten: " + e.getMessage());
        }

        return roomList;
    }

    @Override
    public boolean roomNameExists(String roomName) {
        String sql = "SELECT 1 FROM Rooms WHERE LOWER(room_name) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra trung ten phong: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean roomNameExistsExceptId(String roomName, int roomId) {
        String sql = "SELECT 1 FROM Rooms WHERE LOWER(room_name) = LOWER(?) AND room_id <> ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomName);
            ps.setInt(2, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra trung ten phong khi cap nhat: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hasBookingReference(int roomId) {
        String sql = "SELECT 1 FROM Bookings WHERE room_id = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra rang buoc booking cua phong: " + e.getMessage());
            return true;
        }
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomName(rs.getString("room_name"));
        room.setCapacity(rs.getInt("capacity"));
        room.setLocation(rs.getString("location"));
        room.setFixedEquipment(rs.getString("fixed_equipment"));
        return room;
    }
}