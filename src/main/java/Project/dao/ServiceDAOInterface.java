package Project.dao;

import Project.model.Service;

import java.util.List;

public interface ServiceDAOInterface {
    boolean addService(Service service);

    boolean updateService(Service service);

    boolean deleteService(int serviceId);

    Service getServiceById(int serviceId);

    List<Service> getAllServices();

    boolean serviceNameExists(String serviceName);

    boolean serviceNameExistsExceptId(String serviceName, int serviceId);

    boolean hasBookingReference(int serviceId);
}