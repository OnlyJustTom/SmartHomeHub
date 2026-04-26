package com.project.smarthomehub.Service;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.Routine;
import com.project.smarthomehub.Domain.RoutineUsers;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Helpers.RoutineRequest;
import com.project.smarthomehub.Repo.DeviceRepo;
import com.project.smarthomehub.Repo.RoutineDeviceRepo;
import com.project.smarthomehub.Repo.RoutineRepo;
import com.project.smarthomehub.Repo.RoutineUserRepo;
import com.project.smarthomehub.Repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineServiceTest {

    @Mock
    private RoutineRepo routineRepo;
    @Mock
    private RoutineDeviceRepo routineDeviceRepo;
    @Mock
    private RoutineUserRepo routineUserRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private DeviceRepo deviceRepo;
    @Mock
    private SchedulingService schedulingService;

    @InjectMocks
    private RoutineService routineService;

    @Test
    void addRoutineCreatesRoutineUserAndDeviceActions() {
        User user = new User();
        user.setId(1);

        Device device = new Device();
        device.setId(2);

        RoutineRequest.RoutineDeviceRequest routineDeviceRequest = new RoutineRequest.RoutineDeviceRequest();
        routineDeviceRequest.setDeviceId(2);
        routineDeviceRequest.setCommandType(CommandType.POWER);
        routineDeviceRequest.setCommandData("on");

        RoutineRequest request = new RoutineRequest();
        request.setName("Morning");
        request.setEnabled(true);
        request.setUserId(1);
        request.setTimeToExecute(LocalTime.of(8, 0));
        request.setDaysToExecute(List.of(DayOfWeek.MONDAY));
        request.setRoutineDevices(List.of(routineDeviceRequest));

        when(userRepo.findById(Integer.valueOf(1))).thenReturn(Optional.of(user));
        when(deviceRepo.findById(2)).thenReturn(Optional.of(device));

        Optional<Routine> result = routineService.addRoutine(request);

        assertTrue(result.isPresent());
        verify(routineRepo).save(any(Routine.class));
        verify(routineUserRepo).save(any());
        verify(routineDeviceRepo).save(any());
    }

    @Test
    void addRoutineReturnsEmptyAndDeletesRoutineWhenUserMissing() {
        RoutineRequest request = new RoutineRequest();
        request.setName("Invalid");
        request.setEnabled(true);
        request.setUserId(999);
        request.setRoutineDevices(List.of());

        when(userRepo.findById(Integer.valueOf(999))).thenReturn(Optional.empty());

        Optional<Routine> result = routineService.addRoutine(request);

        assertTrue(result.isEmpty());
        verify(routineRepo).delete(any(Routine.class));
    }

    @Test
    void getAllRoutineByUserIdReturnsEmptyForUnknownUser() {
        when(userRepo.findById(Integer.valueOf(404))).thenReturn(Optional.empty());

        Optional<List<Routine>> result = routineService.getAllRoutineByUserId(404);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRoutineByUserIdReturnsMappedRoutines() {
        User user = new User();
        user.setId(1);

        Routine routine = new Routine();
        routine.setName("Night");

        RoutineUsers routineUsers = new RoutineUsers();
        routineUsers.setUser(user);
        routineUsers.setRoutine(routine);

        when(userRepo.findById(Integer.valueOf(1))).thenReturn(Optional.of(user));
        when(routineUserRepo.findAllByUser_Id(1)).thenReturn(List.of(routineUsers));

        Optional<List<Routine>> result = routineService.getAllRoutineByUserId(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals("Night", result.get().get(0).getName());
    }

    @Test
    void testRoutineDelegatesToSchedulingService() {
        when(schedulingService.executeRoutineNow(7)).thenReturn(true);

        boolean result = routineService.testRoutine(7);

        assertTrue(result);
        verify(schedulingService).executeRoutineNow(7);
    }
}

