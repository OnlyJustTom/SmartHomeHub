package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.Routine;
import com.project.smarthomehub.Domain.RoutineDevices;
import com.project.smarthomehub.Domain.RoutineUsers;
import com.project.smarthomehub.Helpers.RoutineRequest;
import com.project.smarthomehub.Helpers.RoutineUpdateRequest;
import com.project.smarthomehub.Repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Optional<List<Routine>> getAllRoutineByUserId(Integer UserId) {
        if (UserId == null || userRepo.findById(UserId).isEmpty()) {
            return Optional.empty();
        }

        List<Routine> routines = routineUserRepo.findAllByUser_Id(UserId)
                .stream()
                .map(RoutineUsers::getRoutine)
                .toList();

        return Optional.of(routines);
    }

    @Transactional
    public Optional<Routine> updateRoutine(RoutineUpdateRequest routineToUpdate) {
        Optional<Routine> routineOpt = routineRepo.findById(routineToUpdate.getId());
        if (routineOpt.isEmpty()) {
            return Optional.empty();
        }
        Routine routine = routineOpt.get();
        routine.setName(routineToUpdate.getName());
        routine.setTimeToExecute(routineToUpdate.getTimeToExecute());
        routine.setDaysToExecute(routineToUpdate.getDaysToExecute());
        routine.setEnabled(routineToUpdate.isEnabled());

        // Update routine users if userId is provided
        if (routineToUpdate.getUserId() != null) {
            Optional<com.project.smarthomehub.Domain.User> userOpt = userRepo.findById(routineToUpdate.getUserId());
            if (userOpt.isPresent()) {
                // Remove existing routine users for this routine
                routine.getRoutineUsers().clear();
                
                // Add new routine user
                RoutineUsers routineUser = new RoutineUsers();
                routineUser.setRoutine(routine);
                routineUser.setUser(userOpt.get());
                routine.getRoutineUsers().add(routineUser);
                routineUserRepo.save(routineUser);
            }
        }
        
        // Update routine devices if provided
        if (routineToUpdate.getRoutineDevices() != null && !routineToUpdate.getRoutineDevices().isEmpty()) {
            // Remove existing routine devices for this routine
            routine.getRoutineDevices().clear();
            
            // Add new routine devices
            for (RoutineUpdateRequest.RoutineUpdateDeviceRequest deviceRequest : routineToUpdate.getRoutineDevices()) {
                Optional<com.project.smarthomehub.Domain.Device> deviceOpt = deviceRepo.findById(deviceRequest.getDeviceId());
                if (deviceOpt.isPresent()) {
                    RoutineDevices routineDevice = new RoutineDevices();
                    routineDevice.setRoutine(routine);
                    routineDevice.setDevice(deviceOpt.get());
                    routineDevice.setCommandType(deviceRequest.getCommandType());
                    routineDevice.setCommandData(deviceRequest.getCommandData());
                    routine.getRoutineDevices().add(routineDevice);
                    routineDeviceRepo.save(routineDevice);
                }
            }
        }
        
        routineRepo.save(routine);
        return Optional.of(routine);
    }

    @Transactional
    public boolean deleteRoutine(Integer routineId) {
        Optional<Routine> routineOpt = routineRepo.findById(routineId);
        if (routineOpt.isEmpty()) {
            return false;
        }
        Routine routine = routineOpt.get();
        routine.getRoutineUsers().forEach(routineUserRepo::delete);
        routine.getRoutineDevices().forEach(routineDeviceRepo::delete);
        routineRepo.delete(routine);
        return true;
    }
}
