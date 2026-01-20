package com.project.smarthomehub.Repo;

import com.project.smarthomehub.Domain.Device;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DeviceRepo extends CrudRepository<Device, Integer> {

    Optional<Device> findByName(String name);

}
