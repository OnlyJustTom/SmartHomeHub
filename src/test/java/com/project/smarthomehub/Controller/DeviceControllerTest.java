package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Service.DeviceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeviceControllerTest {

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceController deviceController;

    @Test
    void getAllDevicesReturnsOkWithBody() {
        Device device = new Device();
        device.setId(1);
        device.setName("lamp");

        when(deviceService.getAllDevices()).thenReturn(List.of(device));

        ResponseEntity<?> response = deviceController.getAllDevices();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
    }

    @Test
    void controlDeviceReturnsBadRequestWhenServiceRejects() {
        when(deviceService.DecodeDeviceCommand(any(DeviceRequest.class))).thenReturn(false);

        DeviceRequest request = new DeviceRequest();
        ResponseEntity<?> response = deviceController.controlDevice(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateDeviceReturnsOkWhenServiceUpdates() {
        Device updated = new Device();
        updated.setId(2);
        updated.setName("updated");

        when(deviceService.updateDevice(any(Device.class))).thenReturn(Optional.of(updated));

        ResponseEntity<?> response = deviceController.updateDevice(new Device());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Device body = (Device) response.getBody();
        assertNotNull(body);
        assertEquals("updated", body.getName());
    }

    @Test
    void resetMicrocontrollerReturnsOkOnSuccess() {
        when(deviceService.resetMicrocontroller(9)).thenReturn(true);

        ResponseEntity<?> response = deviceController.resetMicrocontroller(9);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

