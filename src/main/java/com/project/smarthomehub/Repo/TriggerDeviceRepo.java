package com.project.smarthomehub.Repo;

import com.project.smarthomehub.Domain.Trigger;
import com.project.smarthomehub.Domain.TriggerDevices;
import com.project.smarthomehub.Helpers.TriggerDeviceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriggerDeviceRepo extends JpaRepository<TriggerDevices, TriggerDeviceId> {
}
