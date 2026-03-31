package Project.dao;

import Project.model.Equipment;

import java.util.List;

public interface EquipmentDAOInterface {
    List<Equipment> getAllEquipments();

    Equipment getEquipmentById(int equipmentId);

    boolean addEquipment(Equipment equipment);

    boolean updateEquipment(Equipment equipment);

    boolean deleteEquipment(int equipmentId);

    boolean updateAvailableQuantity(int equipmentId, int availableQuantity);

    boolean equipmentNameExists(String equipmentName);

    boolean equipmentNameExistsExceptId(String equipmentName, int equipmentId);

    boolean hasBookingReference(int equipmentId);
}