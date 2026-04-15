package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {


    //TODO - Implement Security + Configs to allow frontend communication (ie IP address whitelist) - Partially Done
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepo userRepo;


    public Optional<User> addUser(String username, String password) {
        if(!doesUserExist(username)) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(bCryptPasswordEncoder.encode(password));
            userRepo.save(newUser);
            return Optional.of(newUser);    //TODO - Fix return not showing correct ID - always 0.
        }
        else{
            return Optional.empty();
        }
    }

    public Optional<User> updateUser(User user) {
        if(!doesUserExist(user.getUsername())) {
            return Optional.empty();
        }
        else{
            User userToUpdate = getUserByUsername(user.getUsername()).get();
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepo.save(userToUpdate);
            return Optional.of(userToUpdate);
        }
    }

    public Optional<User> deleteUser(String username) {
        if(!doesUserExist(username)) {
            return Optional.empty();
        }
        else{
            User userToDelete = getUserByUsername(username).get();
            userRepo.deleteById(userToDelete.getId());
            return Optional.of(userToDelete);
        }
    }

    public Optional<User> validateUser(User user) {
        Optional<User> userToValidate = userRepo.findByUsername(user.getUsername());

        if (userToValidate.isEmpty()) {
            return Optional.empty();
        }

        if (bCryptPasswordEncoder.matches(
                user.getPassword(),
                userToValidate.get().getPassword())) {
            return userToValidate;
        }
        return Optional.empty();
    }


    private boolean doesUserExist(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        return user.isPresent();
    }


    public Optional<User> getUserById(int id) {
        Optional<User> retrivedUser = userRepo.findById(id);
        if (retrivedUser.isPresent()) {
            return retrivedUser;
        }
            return Optional.empty();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

}
