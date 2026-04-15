package com.project.smarthomehub.Repo;

import com.project.smarthomehub.Domain.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepo extends JpaRepository<Routine, Integer> {
}
