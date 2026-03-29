package Project.dao;

import Project.model.Equipment;

import java.util.List;

public interface EquipmentDAOInterface {
    List<Equipment> getAllEquipments();

    Equipment getEquipmentById(int equipmentId);

    boolean updateAvailableQuantity(int equipmentId, int availableQuantity);
}