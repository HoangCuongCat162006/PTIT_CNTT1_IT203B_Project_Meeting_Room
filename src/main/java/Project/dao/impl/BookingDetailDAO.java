package Project.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDetailDAO {

    public boolean addEquipmentToBooking(int bookingId, int equipmentId, Connection conn) throws SQLException {
        String sql = "INSERT INTO booking_details (booking_id, equipment_id) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, equipmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Integer> getEquipmentIdsByBookingId(int bookingId, Connection conn) throws SQLException {
        List<Integer> equipmentIds = new ArrayList<Integer>();
        String sql = "SELECT equipment_id FROM booking_details WHERE booking_id = ? AND equipment_id IS NOT NULL";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    equipmentIds.add(rs.getInt("equipment_id"));
                }
            }
        }

        return equipmentIds;
    }
}