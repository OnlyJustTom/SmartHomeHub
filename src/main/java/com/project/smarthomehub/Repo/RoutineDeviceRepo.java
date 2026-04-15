package com.project.smarthomehub.Repo;

import com.project.smarthomehub.Domain.Routine;
import com.project.smarthomehub.Domain.RoutineDevices;
import com.project.smarthomehub.Helpers.RoutineDeviceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineDeviceRepo extends JpaRepository<RoutineDevices, RoutineDeviceId> {
}
