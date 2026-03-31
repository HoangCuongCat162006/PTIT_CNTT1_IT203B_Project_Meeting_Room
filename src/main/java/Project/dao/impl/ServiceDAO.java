package Project.dao.impl;

import Project.dao.ServiceDAOInterface;
import Project.model.Service;
import Project.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO implements ServiceDAOInterface {

    @Override
    public boolean addService(Service service) {
        String sql = "INSERT INTO Services (service_name, description, price) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, service.getServiceName());
            ps.setString(2, service.getDescription());
            ps.setDouble(3, service.getPrice());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi them dich vu: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateService(Service service) {
        String sql = "UPDATE Services SET service_name = ?, description = ?, price = ? WHERE service_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, service.getServiceName());
            ps.setString(2, service.getDescription());
            ps.setDouble(3, service.getPrice());
            ps.setInt(4, service.getServiceId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi cap nhat dich vu: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteService(int serviceId) {
        String sql = "DELETE FROM Services WHERE service_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loi xoa dich vu: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Service getServiceById(int serviceId) {
        String sql = "SELECT * FROM Services WHERE service_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapService(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi lay dich vu theo id: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<Service>();
        String sql = "SELECT * FROM Services ORDER BY service_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                services.add(mapService(rs));
            }
        } catch (SQLException e) {
            System.out.println("Loi lay danh sach dich vu: " + e.getMessage());
        }

        return services;
    }

    @Override
    public boolean serviceNameExists(String serviceName) {
        String sql = "SELECT 1 FROM Services WHERE LOWER(service_name) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, serviceName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra trung ten dich vu: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean serviceNameExistsExceptId(String serviceName, int serviceId) {
        String sql = "SELECT 1 FROM Services WHERE LOWER(service_name) = LOWER(?) AND service_id <> ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, serviceName);
            ps.setInt(2, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra trung ten dich vu khi cap nhat: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hasBookingReference(int serviceId) {
        String sql1 = "SELECT 1 FROM Bookings WHERE service_id = ? LIMIT 1";
        String sql2 = "SELECT 1 FROM booking_details WHERE service_id = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setInt(1, serviceId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setInt(1, serviceId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Loi kiem tra rang buoc dich vu: " + e.getMessage());
            return true;
        }
    }

    private Service mapService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setServiceId(rs.getInt("service_id"));
        service.setServiceName(rs.getString("service_name"));
        service.setDescription(rs.getString("description"));
        service.setPrice(rs.getDouble("price"));
        service.setCreatedAt(rs.getString("created_at"));
        service.setUpdatedAt(rs.getString("updated_at"));
        return service;
    }
}