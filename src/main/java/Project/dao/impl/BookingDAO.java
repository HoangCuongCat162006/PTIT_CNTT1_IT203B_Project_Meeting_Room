package Project.dao.impl;

import Project.model.Booking;
import Project.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM Bookings WHERE booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBooking(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi lay booking theo id: " + e.getMessage());
        }

        return null;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<Booking>();
        String sql = "SELECT * FROM Bookings ORDER BY booking_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi lay tat ca booking: " + e.getMessage());
        }

        return bookings;
    }

    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<Booking>();
        String sql = "SELECT * FROM Bookings WHERE status = ? ORDER BY booking_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapBooking(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi lay booking theo status: " + e.getMessage());
        }

        return bookings;
    }

    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<Booking>();
        String sql = "SELECT * FROM Bookings WHERE user_id = ? ORDER BY start_time DESC, booking_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapBooking(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi lay booking theo user: " + e.getMessage());
        }

        return bookings;
    }

    public List<Booking> getBookingsBySupportStaffId(int supportStaffId) {
        List<Booking> bookings = new ArrayList<Booking>();
        String sql = """
                SELECT *
                FROM Bookings
                WHERE support_staff_id = ?
                  AND status = 'CONFIRMED'
                ORDER BY start_time ASC, booking_id ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supportStaffId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapBooking(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi lay booking theo support staff: " + e.getMessage());
        }

        return bookings;
    }

    public boolean isTimeConflict(int roomId, String startTime, String endTime) {
        String sql = """
                SELECT COUNT(*)
                FROM Bookings
                WHERE room_id = ?
                  AND status IN ('PENDING', 'CONFIRMED')
                  AND (? < end_time AND ? > start_time)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setTimestamp(2, Timestamp.valueOf(startTime));
            ps.setTimestamp(3, Timestamp.valueOf(endTime));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra xung dot thoi gian: " + e.getMessage());
        }

        return false;
    }

    public int addBooking(Booking booking, Connection conn) {
        String sql = """
                INSERT INTO Bookings (
                    user_id, room_id, participant_count, booking_time, start_time, end_time,
                    service_id, equipment_id, status, support_staff_id, preparation_status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getRoomId());
            ps.setInt(3, booking.getParticipantCount());
            ps.setTimestamp(4, Timestamp.valueOf(booking.getBookingTime()));
            ps.setTimestamp(5, Timestamp.valueOf(booking.getStartTime()));
            ps.setTimestamp(6, Timestamp.valueOf(booking.getEndTime()));

            if (booking.getServiceId() == null) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, booking.getServiceId());
            }

            if (booking.getEquipmentId() == null) {
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(8, booking.getEquipmentId());
            }

            ps.setString(9, booking.getStatus());

            if (booking.getSupportStaffId() == null) {
                ps.setNull(10, Types.INTEGER);
            } else {
                ps.setInt(10, booking.getSupportStaffId());
            }

            ps.setString(11, booking.getPreparationStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows <= 0) {
                return 0;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi them booking: " + e.getMessage());
        }

        return 0;
    }

    public Booking getBookingByIdForUpdate(int bookingId, Connection conn) {
        String sql = "SELECT * FROM Bookings WHERE booking_id = ? FOR UPDATE";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBooking(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi khoa booking de cap nhat: " + e.getMessage());
        }

        return null;
    }

    public boolean approveBooking(int bookingId, Connection conn) {
        Booking booking = getBookingByIdForUpdate(bookingId, conn);
        if (booking == null) {
            return false;
        }

        String conflictSql = """
                SELECT COUNT(*)
                FROM Bookings
                WHERE room_id = ?
                  AND status = 'CONFIRMED'
                  AND booking_id <> ?
                  AND (? < end_time AND ? > start_time)
                """;

        try (PreparedStatement conflictPs = conn.prepareStatement(conflictSql)) {
            conflictPs.setInt(1, booking.getRoomId());
            conflictPs.setInt(2, bookingId);
            conflictPs.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            conflictPs.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));

            try (ResultSet rs = conflictPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Khong the duyet vi phong da trung lich voi booking CONFIRMED khac.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra xung dot khi duyet: " + e.getMessage());
            return false;
        }

        String updateSql = """
                UPDATE Bookings
                SET status = 'CONFIRMED',
                    support_staff_id = NULL,
                    preparation_status = 'NOT_ASSIGNED'
                WHERE booking_id = ? AND status = 'PENDING'
                """;

        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi duyet booking: " + e.getMessage());
            return false;
        }
    }

    public boolean rejectBooking(int bookingId, Connection conn) {
        String sql = "UPDATE Bookings SET status = 'CANCELLED' WHERE booking_id = ? AND status = 'PENDING'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi tu choi booking: " + e.getMessage());
            return false;
        }
    }

    public boolean assignSupportStaff(int bookingId, int supportStaffId) {
        String sql = """
                UPDATE Bookings
                SET support_staff_id = ?,
                    preparation_status = CASE
                        WHEN preparation_status IS NULL OR preparation_status = 'NOT_ASSIGNED' THEN 'PREPARING'
                        ELSE preparation_status
                    END
                WHERE booking_id = ? AND status = 'CONFIRMED'
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supportStaffId);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Loi phan cong support staff: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelPendingBooking(int bookingId, int userId, Connection conn) {
        String sql = """
                UPDATE Bookings
                SET status = 'CANCELLED'
                WHERE booking_id = ? AND user_id = ? AND status = 'PENDING'
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi huy booking pending: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePreparationStatus(int bookingId, int supportStaffId, String status) {
        String sql = """
                UPDATE Bookings
                SET preparation_status = ?
                WHERE booking_id = ? AND support_staff_id = ? AND status = 'CONFIRMED'
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, bookingId);
            ps.setInt(3, supportStaffId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Loi cap nhat preparation status: " + e.getMessage());
            return false;
        }
    }

    public boolean hasRoomConflict(int roomId, LocalDateTime startTime, LocalDateTime endTime, Integer excludeBookingId) {
        String sql = """
                SELECT COUNT(*)
                FROM Bookings
                WHERE room_id = ?
                  AND status = 'CONFIRMED'
                  AND (? < end_time AND ? > start_time)
                  AND (? IS NULL OR booking_id <> ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setTimestamp(2, Timestamp.valueOf(startTime));
            ps.setTimestamp(3, Timestamp.valueOf(endTime));

            if (excludeBookingId == null) {
                ps.setNull(4, Types.INTEGER);
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(4, excludeBookingId);
                ps.setInt(5, excludeBookingId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra trung lich: " + e.getMessage());
        }

        return false;
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setRoomId(rs.getInt("room_id"));
        booking.setParticipantCount(rs.getInt("participant_count"));

        booking.setBookingTime(formatTimestamp(rs.getTimestamp("booking_time")));
        booking.setStartTime(formatTimestamp(rs.getTimestamp("start_time")));
        booking.setEndTime(formatTimestamp(rs.getTimestamp("end_time")));

        int serviceId = rs.getInt("service_id");
        booking.setServiceId(rs.wasNull() ? null : serviceId);

        int equipmentId = rs.getInt("equipment_id");
        booking.setEquipmentId(rs.wasNull() ? null : equipmentId);

        booking.setStatus(rs.getString("status"));

        int supportStaffId = rs.getInt("support_staff_id");
        booking.setSupportStaffId(rs.wasNull() ? null : supportStaffId);

        booking.setPreparationStatus(rs.getString("preparation_status"));

        try {
            booking.setCreatedAt(formatTimestamp(rs.getTimestamp("created_at")));
        } catch (SQLException ignored) {
            booking.setCreatedAt(null);
        }

        try {
            booking.setUpdatedAt(formatTimestamp(rs.getTimestamp("updated_at")));
        } catch (SQLException ignored) {
            booking.setUpdatedAt(null);
        }

        return booking;
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime().format(FORMATTER);
    }
}