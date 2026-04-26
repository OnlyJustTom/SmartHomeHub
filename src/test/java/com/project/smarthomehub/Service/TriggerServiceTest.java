package com.project.smarthomehub.Service;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.DeviceType;
import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.LinkedDevice;
import com.project.smarthomehub.Domain.Trigger;
import com.project.smarthomehub.Domain.TriggerDevices;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Helpers.TriggerControlRequest;
import com.project.smarthomehub.Helpers.TriggerRequest;
import com.project.smarthomehub.Repo.DeviceRepo;
import com.project.smarthomehub.Repo.LinkRepo;
import com.project.smarthomehub.Repo.TriggerDeviceRepo;
import com.project.smarthomehub.Repo.TriggerRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.project.smarthomehub.TriggerCondition.MOTION_DETECTED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriggerServiceTest {

    @Mock
    private TriggerRepo triggerRepo;
    @Mock
    private TriggerDeviceRepo triggerDeviceRepo;
    @Mock
    private DeviceService deviceService;
    @Mock
    private DeviceRepo deviceRepo;
    @Mock
    private LinkRepo linkRepo;

    @InjectMocks
    private TriggerService triggerService;

    @Test
    void addTriggerCreatesTriggerAndTargetDevices() {
        Device source = new Device();
        source.setId(1);
        source.setType(DeviceType.SENSOR);

        Device target = new Device();
        target.setId(2);
        target.setType(DeviceType.MICROCONTROLLER);

        TriggerRequest request = new TriggerRequest();
        request.setTriggerDevice(1);
        request.setTriggerCondition(MOTION_DETECTED);

        TriggerRequest.TriggerDeviceRequest targetRequest = new TriggerRequest.TriggerDeviceRequest();
        targetRequest.setDeviceId(2);
        targetRequest.setCommandType(CommandType.POWER);
        targetRequest.setCommandData("on");
        request.setTargetDevices(List.of(targetRequest));

        when(deviceRepo.findById(1)).thenReturn(Optional.of(source));
        when(deviceRepo.findById(2)).thenReturn(Optional.of(target));
        when(triggerRepo.save(any(Trigger.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Trigger> result = triggerService.addTrigger(request);

        assertTrue(result.isPresent());
        verify(triggerRepo).save(any(Trigger.class));
        verify(triggerDeviceRepo).save(any(TriggerDevices.class));
    }

    @Test
    void addTriggerReturnsEmptyAndRollsBackWhenSourceMissing() {
        TriggerRequest request = new TriggerRequest();
        request.setTriggerDevice(999);
        request.setTriggerCondition(MOTION_DETECTED);
        request.setTargetDevices(List.of());

        when(deviceRepo.findById(999)).thenReturn(Optional.empty());

        Optional<Trigger> result = triggerService.addTrigger(request);

        assertTrue(result.isEmpty());
        verify(triggerRepo).delete(any(Trigger.class));
    }

    @Test
    void executeTriggerBuildsAndRoutesDeviceCommands() {
        Device source = new Device();
        source.setId(1);
        source.setType(DeviceType.SENSOR);
        source.setAPIKeyIP("192.168.1.2");

        Device target = new Device();
        target.setId(10);
        target.setType(DeviceType.MICROCONTROLLER);

        TriggerDevices triggerDevice = new TriggerDevices();
        triggerDevice.setDevice(target);
        triggerDevice.setCommandType(CommandType.POWER);
        triggerDevice.setCommandData("on");

        Trigger trigger = new Trigger();
        trigger.setTriggerCondition(MOTION_DETECTED);
        trigger.setTargetDevices(List.of(triggerDevice));

        User user = new User();
        user.setId(42);
        LinkedDevice link = new LinkedDevice();
        link.setUser(user);
        link.setDevice(target);

        TriggerControlRequest controlRequest = new TriggerControlRequest();
        controlRequest.setDeviceIPAddress("192.168.1.2");
        controlRequest.setTriggerCondition(MOTION_DETECTED);

        when(deviceRepo.findByTypeAndAPIKeyIP(DeviceType.SENSOR, "192.168.1.2")).thenReturn(Optional.of(source));
        when(triggerRepo.findAllBySourceDevice(source)).thenReturn(Optional.of(List.of(trigger)));
        when(linkRepo.findByDevice_Id(10)).thenReturn(Optional.of(link));

        String status = triggerService.executeTrigger(controlRequest);

        assertEquals("Trigger executed successfully", status);
        ArgumentCaptor<DeviceRequest> requestCaptor = ArgumentCaptor.forClass(DeviceRequest.class);
        verify(deviceService).DecodeDeviceCommand(requestCaptor.capture());
        assertEquals(42, requestCaptor.getValue().getUserId());
        assertEquals(10, requestCaptor.getValue().getDeviceId());
        assertEquals(CommandType.POWER, requestCaptor.getValue().getCommandType());
    }

    @Test
    void deleteTriggerReturnsFalseWhenTriggerMissing() {
        when(triggerRepo.findById(5)).thenReturn(Optional.empty());

        boolean deleted = triggerService.deleteTrigger(5);

        assertFalse(deleted);
        verify(triggerRepo, never()).delete(any(Trigger.class));
    }
}

