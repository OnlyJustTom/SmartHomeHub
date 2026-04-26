package com.project.smarthomehub.Repo;

import com.project.smarthomehub.DeviceType;
import com.project.smarthomehub.Domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepo extends JpaRepository<Device, Integer> {

    Optional<Device> findByName(String name);
    Optional<Device> findByTypeAndAPIKeyIP(DeviceType type, String apiKeyIP);

}
