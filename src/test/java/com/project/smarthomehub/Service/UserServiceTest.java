package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    @Test
    void addUserHashesPasswordAndSaves() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());

        Optional<User> result = userService.addUser("alice", "plain-password");

        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
        assertNotEquals("plain-password", result.get().getPassword());
        assertTrue(result.get().getPassword().startsWith("$2"));
        verify(userRepo).save(result.get());
    }

    @Test
    void addUserReturnsEmptyWhenUsernameExists() {
        User existingUser = new User();
        existingUser.setUsername("alice");
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(existingUser));

        Optional<User> result = userService.addUser("alice", "any");

        assertTrue(result.isEmpty());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void validateUserReturnsUserWhenPasswordMatches() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User storedUser = new User();
        storedUser.setUsername("alice");
        storedUser.setPassword(encoder.encode("secret"));

        User loginRequest = new User();
        loginRequest.setUsername("alice");
        loginRequest.setPassword("secret");

        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(storedUser));

        Optional<User> result = userService.validateUser(loginRequest);

        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
    }

    @Test
    void updateUserReturnsEmptyWhenUserDoesNotExist() {
        User update = new User();
        update.setUsername("missing");
        update.setPassword("new-password");

        when(userRepo.findByUsername("missing")).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUser(update);

        assertTrue(result.isEmpty());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void deleteUserDeletesExistingUser() {
        User user = new User();
        user.setId(9);
        user.setUsername("alice");

        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));

        Optional<User> result = userService.deleteUser("alice");

        assertTrue(result.isPresent());
        verify(userRepo).deleteById(9);
    }
}

