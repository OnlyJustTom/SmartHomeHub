package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Helpers.Link;
import com.project.smarthomehub.Service.DeviceService;
import com.project.smarthomehub.Service.LinkService;
import com.project.smarthomehub.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LinkControllerTest {

    @Mock
    private LinkService linkService;

    @Mock
    private DeviceService deviceService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LinkController linkController;

    @Test
    void linkDeviceToUserReturnsOkWhenBothExist() {
        User user = new User();
        user.setId(1);
        Device device = new Device();
        device.setId(2);
        Link link = new Link(1, 2);

        when(userService.getUserById(1)).thenReturn(Optional.of(user));
        when(deviceService.getDeviceById(2)).thenReturn(Optional.of(device));

        ResponseEntity<?> response = linkController.LinkDeviceToUser(link);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(linkService).linkDevice(user, device);
    }

    @Test
    void isUserLinkedToDeviceReturnsNotFoundWhenNotLinked() {
        Link link = new Link(1, 2);
        when(linkService.isUserLinkedToDevice(1, 2)).thenReturn(false);

        ResponseEntity<?> response = linkController.IsUserLinkedToDevice(link);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUserDevicesReturnsDeviceArray() {
        Device device = new Device();
        device.setId(7);
        device.setName("sensor");
        User user = new User();
        user.setId(1);

        when(linkService.userDevices(1)).thenReturn(List.of(device));

        ResponseEntity<?> response = linkController.GetUserDevices(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
    }

    @Test
    void unlinkDeviceReturnsNotFoundWhenMissingEntities() {
        Link link = new Link(1, 2);
        when(userService.getUserById(anyInt())).thenReturn(Optional.empty());
        when(deviceService.getDeviceById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<?> response = linkController.UnlinkDeviceFromUser(link);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(linkService, never()).unlinkDevice(any(User.class), any(Device.class));
    }
}

