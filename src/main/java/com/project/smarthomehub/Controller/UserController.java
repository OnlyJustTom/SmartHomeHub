package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping()
    public ResponseEntity<?> userCreate(@RequestBody User user1) {
        Optional<User> user = userService.addUser(user1.getUsername(), user1.getPassword());
        if(user.isPresent()) {
            return ResponseEntity.ok(user);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error, Username in use");
        }
    }

    @GetMapping()
    public ResponseEntity<String> userLogin(@RequestBody User user) {
        Map<Boolean,String> isUserValid = userService.validateUser(user);
        String returnString;
        if (isUserValid.containsKey(true)) {
            returnString = isUserValid.get(true);
            return ResponseEntity.ok(returnString);
        }
        else {
            returnString = isUserValid.get(false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(returnString);
        }
    }

    @PatchMapping()
    public ResponseEntity<String> userUpdate(@RequestBody User user) {
        Optional<User> userToUpdate = userService.updateUser(user);
        if(userToUpdate.isPresent()) {
            return ResponseEntity.ok(userToUpdate.get().getUsername());
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error, User not found");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> userDelete(@RequestBody User user) {
        Optional<User> userToUpdate = userService.updateUser(user);
        if(userToUpdate.isPresent()) {
            return ResponseEntity.ok(userToUpdate.get().getUsername());
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error, User not found");
        }
    }
}
