package com.project.smarthomehub.Repo;

import com.project.smarthomehub.Domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findById(int id);

}
