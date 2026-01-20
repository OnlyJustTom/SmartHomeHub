package com.project.smarthomehub.Repo;

import com.project.smarthomehub.Domain.LinkedDevice;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LinkRepo extends CrudRepository<LinkedDevice, Integer> {

    Optional<LinkedDevice> findByUser_Id(int userID);

}
