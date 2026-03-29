package Project.dao;

import Project.model.Room;

import java.util.List;

public interface RoomDAOInterface {
    boolean addRoom(Room room);

    boolean updateRoom(Room room);

    boolean deleteRoom(int roomId);

    Room getRoomById(int roomId);

    List<Room> getAllRooms();
}