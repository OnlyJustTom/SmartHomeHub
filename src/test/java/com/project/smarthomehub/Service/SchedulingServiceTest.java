package com.project.smarthomehub.Service;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.DeviceType;
import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.Routine;
import com.project.smarthomehub.Domain.RoutineDevices;
import com.project.smarthomehub.Domain.RoutineUsers;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Repo.RoutineRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceTest {

    @Mock
    private RoutineRepo routineRepo;

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private SchedulingService schedulingService;

    @Test
    void executeRoutineNowReturnsFalseWhenRoutineDoesNotExist() {
        when(routineRepo.findById(123)).thenReturn(Optional.empty());

        boolean result = schedulingService.executeRoutineNow(123);

        assertFalse(result);
        verify(deviceService, never()).DecodeDeviceCommand(any(DeviceRequest.class));
    }

    @Test
    void executeRoutineNowBuildsCommandAndPersistsExecutionTime() {
        User user = new User();
        user.setId(11);

        Device device = new Device();
        device.setId(22);
        device.setType(DeviceType.MICROCONTROLLER);

        Routine routine = new Routine();
        routine.setId(5);
        routine.setName("Test Routine");
        routine.setTimeToExecute(LocalTime.of(9, 0));

        RoutineUsers routineUsers = new RoutineUsers();
        routineUsers.setRoutine(routine);
        routineUsers.setUser(user);
        routine.setRoutineUsers(List.of(routineUsers));

        RoutineDevices routineDevices = new RoutineDevices();
        routineDevices.setRoutine(routine);
        routineDevices.setDevice(device);
        routineDevices.setCommandType(CommandType.POWER);
        routineDevices.setCommandData("on");
        routine.setRoutineDevices(List.of(routineDevices));

        when(routineRepo.findById(5)).thenReturn(Optional.of(routine));

        boolean result = schedulingService.executeRoutineNow(5);

        assertTrue(result);

        ArgumentCaptor<DeviceRequest> requestCaptor = ArgumentCaptor.forClass(DeviceRequest.class);
        verify(deviceService).DecodeDeviceCommand(requestCaptor.capture());
        assertEquals(11, requestCaptor.getValue().getUserId());
        assertEquals(22, requestCaptor.getValue().getDeviceId());
        assertEquals(CommandType.POWER, requestCaptor.getValue().getCommandType());

        verify(routineRepo).save(routine);
        assertNotNull(routine.getLastExecuted());
    }

    @Test
    void checkRoutinesSkipsDisabledRoutines() {
        Routine disabledRoutine = new Routine();
        disabledRoutine.setEnabled(false);

        when(routineRepo.findAll()).thenReturn(List.of(disabledRoutine));

        schedulingService.checkRoutines();

        verify(deviceService, never()).DecodeDeviceCommand(any(DeviceRequest.class));
        verify(routineRepo, never()).save(disabledRoutine);
    }
}

