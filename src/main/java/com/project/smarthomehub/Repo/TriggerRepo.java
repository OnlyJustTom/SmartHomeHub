package com.project.smarthomehub.Repo;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.Trigger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TriggerRepo extends JpaRepository<Trigger, Integer> {

    public Optional<Trigger> findDeviceById(Integer id);
    public Optional<List<Trigger>> findAllBySourceDevice(Device device);

}
