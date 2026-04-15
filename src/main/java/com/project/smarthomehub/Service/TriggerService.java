package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.Trigger;
import com.project.smarthomehub.Domain.TriggerDevices;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Helpers.TriggerControlRequest;
import com.project.smarthomehub.Helpers.TriggerRequest;
import com.project.smarthomehub.Repo.DeviceRepo;
import com.project.smarthomehub.Repo.TriggerDeviceRepo;
import com.project.smarthomehub.Repo.TriggerRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TriggerService {

    @Autowired
    private TriggerRepo triggerRepo;
    @Autowired
    private TriggerDeviceRepo triggerDeviceRepo;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceRepo deviceRepo;


    @Transactional
    public Optional<Trigger> addTrigger (TriggerRequest triggerToAdd){
        Trigger trigger = new Trigger();
        try{

            trigger.setTriggerCondition(triggerToAdd.getTriggerCondition());
            trigger.setSourceDevice(triggerRepo.findDeviceById(triggerToAdd.getTriggerDevice()).orElseThrow(() -> new RuntimeException("Device not found")).getSourceDevice());
            Trigger savedTrigger = triggerRepo.save(trigger);

            for (TriggerRequest.TriggerDeviceRequst targetDevice : triggerToAdd.getTargetDevices()) {
                var triggerDevice = new com.project.smarthomehub.Domain.TriggerDevices();
                triggerDevice.setTrigger(savedTrigger);
                triggerDevice.setDevice(triggerRepo.findDeviceById(targetDevice.getDeviceId()).orElseThrow(() -> new RuntimeException("Device not found")).getSourceDevice());
                triggerDevice.setCommandType(targetDevice.getCommandType());
                triggerDevice.setCommandData(targetDevice.getCommandData());
                triggerDeviceRepo.save(triggerDevice);
            }
            return Optional.of(savedTrigger);
        }
        catch (Exception e){
            triggerRepo.delete(trigger);
            return Optional.empty();
        }
    }

    @Transactional
    public String executeTrigger(TriggerControlRequest triggerControlRequest) {
        try{
            Device triggerSource = deviceRepo.findById(triggerControlRequest.getSensorDeviceId()).orElseThrow(() -> new RuntimeException("Device not found"));
            List<Trigger> triggers = triggerRepo.findAllBySourceDevice(triggerSource).orElseThrow(() -> new RuntimeException("Trigger not found"));
            for(Trigger t: triggers){
                if(t.getTriggerCondition().equals(triggerControlRequest.getTriggerCondition())){
                    for(TriggerDevices td: t.getTargetDevices()){
                        DeviceRequest deviceRequest = new DeviceRequest();
                        deviceRequest.setUserId(0);
                        deviceRequest.setDeviceId(td.getDevice().getId());
                        deviceRequest.setCommandType(td.getCommandType());
                        deviceRequest.setCommandData(td.getCommandData());
                        deviceService.DecodeDeviceCommand(deviceRequest);
                    }
                }
            }
            return "Trigger executed successfully";
        }
        catch (RuntimeException e){
            return e.getMessage();
        }
    }
}
