package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.Routine;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Repo.RoutineRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SchedulingService {

    @Autowired
    private RoutineRepo routineRepo;
    @Autowired
    private DeviceService deviceService;



    @Scheduled(fixedRate = 30000) //Check every 30 seconds
    @Transactional
    public void checkRoutines(){
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime().withSecond(0).withNano(0);


        for (Routine routine : routineRepo.findAll()){
            if(!routine.isEnabled()){
                continue;
            }
            if(routine.getDaysToExecute() == null || !routine.getDaysToExecute().contains(today.getDayOfWeek())){
                continue;
            }
            if(!routine.getTimeToExecute().equals(currentTime)){
                continue;
            }
            if(routine.getLastExecuted() != null && routine.getLastExecuted().toLocalTime().equals(currentTime.plusMinutes(1))
                    && routine.getLastExecuted().toLocalDate().equals(today)){
                continue;
            }

            executeRoutine(routine);
            routine.setLastExecuted(LocalDateTime.now());
            routineRepo.save(routine);
        }
    }

    @Transactional
    public boolean executeRoutineNow(Integer routineId) {
        Routine routine = routineRepo.findById(routineId).orElse(null);
        if (routine == null) {
            return false;
        }

        executeRoutine(routine);
        routine.setLastExecuted(LocalDateTime.now());
        routineRepo.save(routine);
        return true;
    }

    private void executeRoutine(Routine routine){
        List<DeviceRequest> deviceRequests = new ArrayList<>();
        routine.getRoutineDevices().forEach(routineDevice -> {
            DeviceRequest deviceRequest = new DeviceRequest();
            deviceRequest.setUserId(routineDevice.getRoutine().getRoutineUsers().get(0).getUser().getId());
            deviceRequest.setDeviceType(routineDevice.getDevice().getType());
            deviceRequest.setDeviceId(routineDevice.getDevice().getId());
            deviceRequest.setCommandType(routineDevice.getCommandType());
            deviceRequest.setCommandData(routineDevice.getCommandData());
            deviceRequests.add(deviceRequest);
        });

        deviceRequests.forEach(deviceService::DecodeDeviceCommand);

    }

}
