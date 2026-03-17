package com.project.smarthomehub.Repo;

import com.project.smarthomehub.Domain.LinkedDevice;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Helpers.LinkedDeviceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LinkRepo extends JpaRepository<LinkedDevice, LinkedDeviceId> {
    Optional<LinkedDevice> findByUser(User user);
    boolean existsByUser_IdAndDevice_Id(int userId, int deviceId);
    List<LinkedDevice> findAllByUser_Id(int userId);
}
