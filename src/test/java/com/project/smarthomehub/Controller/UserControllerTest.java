package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void userCreateReturnsOkWhenServiceSucceeds() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("hashed");

        when(userService.addUser("alice", "secret")).thenReturn(Optional.of(user));

        User request = new User();
        request.setUsername("alice");
        request.setPassword("secret");

        ResponseEntity<?> response = userController.userCreate(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void userLoginReturnsNotFoundWhenCredentialsInvalid() {
        when(userService.validateUser(any(User.class))).thenReturn(Optional.empty());

        User request = new User();
        request.setUsername("alice");
        request.setPassword("wrong");

        ResponseEntity<?> response = userController.userLogin(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error, Username or Password incorrect", response.getBody());
    }

    @Test
    void userUpdateReturnsOkWhenServiceSucceeds() {
        User updated = new User();
        updated.setUsername("alice");

        when(userService.updateUser(any(User.class))).thenReturn(Optional.of(updated));

        User request = new User();
        request.setUsername("alice");
        request.setPassword("new");

        ResponseEntity<?> response = userController.userUpdate(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void userDeleteReturnsBadRequestWhenMissing() {
        when(userService.deleteUser("missing")).thenReturn(Optional.empty());

        User request = new User();
        request.setUsername("missing");

        ResponseEntity<?> response = userController.userDelete(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error, User not found", response.getBody());
    }
}

