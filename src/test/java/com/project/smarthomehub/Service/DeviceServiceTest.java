package com.project.smarthomehub.Service;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.DeviceControllers.LIFX;
import com.project.smarthomehub.DeviceControllers.MicroController;
import com.project.smarthomehub.DeviceType;
import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Repo.DeviceRepo;
import com.project.smarthomehub.Repo.LinkRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepo deviceRepo;
    @Mock
    private LinkService linkService;
    @Mock
    private LIFX lifx;
    @Mock
    private MicroController microController;
    @Mock
    private LinkRepo linkRepo;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void decodeDeviceCommandRejectsUnlinkedNonGetInfoRequests() {
        DeviceRequest request = new DeviceRequest();
        request.setUserId(1);
        request.setDeviceId(100);
        request.setDeviceType(DeviceType.LIFX);
        request.setCommandType(CommandType.POWER);

        when(linkService.isUserLinkedToDevice(1, 100)).thenReturn(false);

        boolean result = deviceService.DecodeDeviceCommand(request);

        assertFalse(result);
        verify(lifx, never()).ExecuteCommand(any(DeviceRequest.class));
        verify(microController, never()).ExecuteCommand(any(DeviceRequest.class));
    }

    @Test
    void decodeDeviceCommandAllowsGetInfoWithoutLink() {
        DeviceRequest request = new DeviceRequest();
        request.setUserId(1);
        request.setDeviceId(100);
        request.setDeviceType(DeviceType.MICROCONTROLLER);
        request.setCommandType(CommandType.GET_INFO);

        when(linkService.isUserLinkedToDevice(1, 100)).thenReturn(false);

        boolean result = deviceService.DecodeDeviceCommand(request);

        assertTrue(result);
        verify(microController).ExecuteCommand(request);
    }

    @Test
    void decodeDeviceCommandRoutesToLifxForLifxDevices() {
        DeviceRequest request = new DeviceRequest();
        request.setUserId(1);
        request.setDeviceId(100);
        request.setDeviceType(DeviceType.LIFX);
        request.setCommandType(CommandType.POWER);

        when(linkService.isUserLinkedToDevice(1, 100)).thenReturn(true);

        boolean result = deviceService.DecodeDeviceCommand(request);

        assertTrue(result);
        verify(lifx).ExecuteCommand(request);
        verify(microController, never()).ExecuteCommand(any(DeviceRequest.class));
    }

    @Test
    void updateDeviceReturnsEmptyWhenDeviceNameDoesNotExist() {
        Device device = new Device();
        device.setName("missing");

        when(deviceRepo.findByName("missing")).thenReturn(Optional.empty());

        Optional<Device> result = deviceService.updateDevice(device);

        assertTrue(result.isEmpty());
        verify(deviceRepo, never()).save(any(Device.class));
    }

    @Test
    void updateDeviceSavesWhenDeviceNameExists() {
        Device existing = new Device();
        existing.setName("lamp");

        Device update = new Device();
        update.setName("lamp");

        when(deviceRepo.findByName("lamp")).thenReturn(Optional.of(existing));

        Optional<Device> result = deviceService.updateDevice(update);

        assertTrue(result.isPresent());
        verify(deviceRepo).save(update);
    }
}

