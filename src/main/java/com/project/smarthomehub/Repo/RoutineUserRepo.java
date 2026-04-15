package com.project.smarthomehub.Repo;

import com.project.smarthomehub.Domain.RoutineUsers;
import com.project.smarthomehub.Helpers.RoutineUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineUserRepo extends JpaRepository<RoutineUsers, RoutineUserId> {
}
