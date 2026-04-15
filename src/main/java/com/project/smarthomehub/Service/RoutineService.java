package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.Routine;
import com.project.smarthomehub.Domain.RoutineDevices;
import com.project.smarthomehub.Domain.RoutineUsers;
import com.project.smarthomehub.Helpers.RoutineRequest;
import com.project.smarthomehub.Repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoutineService {

    @Autowired
    private RoutineRepo routineRepo;

    @Autowired
    private RoutineDeviceRepo routineDeviceRepo;

    @Autowired
    private RoutineUserRepo routineUserRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DeviceRepo deviceRepo;

    @Autowired
    private SchedulingService schedulingService;

    @Transactional
    public Optional<Routine> addRoutine(RoutineRequest routineToAdd){
        Routine routine = new Routine();
        routine.setName(routineToAdd.getName());
        routine.setTimeToExecute(routineToAdd.getTimeToExecute());
        routine.setDaysToExecute(routineToAdd.getDaysToExecute());
        routine.setEnabled(routineToAdd.isEnabled());
        routineRepo.save(routine);

        try{
            RoutineUsers routineUser = new RoutineUsers();
            routineUser.setRoutine(routine);
            routineUser.setUser(userRepo.findById(routineToAdd.getUserId()).orElseThrow(() -> new RuntimeException("User not found")));
            routineUserRepo.save(routineUser);

            for (RoutineRequest.RoutineDeviceRequest deviceRequest : routineToAdd.getRoutineDevices()) {
                RoutineDevices routineDevice = new RoutineDevices();
                routineDevice.setRoutine(routine);
                routineDevice.setDevice(deviceRepo.findById(deviceRequest.getDeviceId()).orElseThrow(() -> new RuntimeException("Device not found")));
                routineDevice.setCommandType(deviceRequest.getCommandType());
                routineDevice.setCommandData(deviceRequest.getCommandData());
                routineDeviceRepo.save(routineDevice);
            }
        } catch (RuntimeException e) {
            routineRepo.delete(routine);
            return Optional.empty();
        }
        return Optional.of(routine);
    }

    public boolean testRoutine(Integer routineId) {
        return schedulingService.executeRoutineNow(routineId);
    }
}
