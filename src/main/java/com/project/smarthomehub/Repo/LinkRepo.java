package com.project.smarthomehub.Repo;

import com.project.smarthomehub.Domain.LinkedDevice;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Helpers.LinkedDeviceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepo extends JpaRepository<LinkedDevice, LinkedDeviceId> {
    boolean existsByUser_IdAndDevice_Id(int userId, int deviceId);
    void deleteByUser_IdAndDevice_Id(int userId, int deviceId);
    List<LinkedDevice> findAllByUser_Id(int userId);
    Optional<LinkedDevice> findByDevice_Id(int deviceId);
}
