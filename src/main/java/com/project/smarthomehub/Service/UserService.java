package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
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

    public Map<Boolean, String> validateUser(User user) {
        Optional<User> userToValidate = userRepo.findByUsername(user.getUsername());

        if (userToValidate.isEmpty()) {
            return Map.of(false, "Username or Password incorrect");
        }

        if (bCryptPasswordEncoder.matches(
                user.getPassword(),
                userToValidate.get().getPassword())) {
            return Map.of(true, "User valid");
        }

        return Map.of(false, "Username or Password incorrect");
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
