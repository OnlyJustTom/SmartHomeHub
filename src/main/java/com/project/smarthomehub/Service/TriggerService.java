package com.project.smarthomehub.Service;

import com.project.smarthomehub.DeviceType;
import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.Trigger;
import com.project.smarthomehub.Domain.TriggerDevices;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Helpers.TriggerControlRequest;
import com.project.smarthomehub.Helpers.TriggerRequest;
import com.project.smarthomehub.Repo.DeviceRepo;
import com.project.smarthomehub.Repo.LinkRepo;
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
    @Autowired
    private LinkRepo linkRepo;


    @Transactional
    public Optional<Trigger> addTrigger(TriggerRequest triggerToAdd) {
        Trigger trigger = new Trigger();
        try {

            trigger.setTriggerCondition(triggerToAdd.getTriggerCondition());
            System.out.println(triggerToAdd.getTriggerCondition());
            System.out.println(triggerToAdd.getTriggerDevice());
            trigger.setSourceDevice(deviceRepo.findById(triggerToAdd.getTriggerDevice()).orElseThrow(() -> new RuntimeException("Device not found")));
            Trigger savedTrigger = triggerRepo.save(trigger);

            for (TriggerRequest.TriggerDeviceRequest targetDevice : triggerToAdd.getTargetDevices()) {
                TriggerDevices triggerDevice = new TriggerDevices();
                triggerDevice.setTrigger(savedTrigger);
                System.out.println("Trigger Device ID" + targetDevice.getDeviceId());
                triggerDevice.setDevice(deviceRepo.findById(targetDevice.getDeviceId()).orElseThrow(() -> new RuntimeException("Device not found")));
                triggerDevice.setCommandType(targetDevice.getCommandType());
                triggerDevice.setCommandData(targetDevice.getCommandData());
                triggerDeviceRepo.save(triggerDevice);
            }
            return Optional.of(savedTrigger);
        } catch (Exception e) {
            triggerRepo.delete(trigger);
            return Optional.empty();
        }
    }

    @Transactional
    public String executeTrigger(TriggerControlRequest triggerControlRequest) {
        try {
            System.out.println("Executing trigger for device IP: " + triggerControlRequest.getDeviceIPAddress() + " with condition: " + triggerControlRequest.getTriggerCondition());
            Device triggerSource = deviceRepo
                    .findByTypeAndAPIKeyIP(DeviceType.SENSOR, triggerControlRequest.getDeviceIPAddress())
                    .orElseGet(() -> deviceRepo.findAll().stream()
                            .filter(d -> d.getType() == DeviceType.MICROCONTROLLER)
                            .filter(d -> d.getAPIKeyIP().equals(triggerControlRequest.getDeviceIPAddress()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Device not found")));
            List<Trigger> triggers = triggerRepo.findAllBySourceDevice(triggerSource).orElseThrow(() -> new RuntimeException("Trigger not found"));
            for (Trigger t : triggers) {
                if (t.getTriggerCondition().equals(triggerControlRequest.getTriggerCondition())) {
                    for (TriggerDevices td : t.getTargetDevices()) {
                        DeviceRequest deviceRequest = new DeviceRequest();
                        deviceRequest.setUserId(linkRepo.findByDevice_Id(td.getDevice().getId()).orElseThrow(() -> new RuntimeException("Link not found")).getUser().getId());
                        deviceRequest.setDeviceId(td.getDevice().getId());
                        deviceRequest.setDeviceType(td.getDevice().getType());
                        deviceRequest.setCommandType(td.getCommandType());
                        deviceRequest.setCommandData(td.getCommandData());
                        deviceService.DecodeDeviceCommand(deviceRequest);
                    }
                }
            }
            return "Trigger executed successfully";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    @Transactional
    public String testTrigger(Integer triggerID){
        try {
            Trigger trigger = triggerRepo.findById(triggerID).orElseThrow(() -> new RuntimeException("Trigger not found"));
            for (TriggerDevices td : trigger.getTargetDevices()) {
                DeviceRequest deviceRequest = new DeviceRequest();
                deviceRequest.setUserId(linkRepo.findByDevice_Id(td.getDevice().getId()).orElseThrow(() -> new RuntimeException("Link not found")).getUser().getId());
                deviceRequest.setDeviceId(td.getDevice().getId());
                deviceRequest.setDeviceType(td.getDevice().getType());
                deviceRequest.setCommandType(td.getCommandType());
                deviceRequest.setCommandData(td.getCommandData());
                deviceService.DecodeDeviceCommand(deviceRequest);
            }
            return "Trigger executed successfully";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    @Transactional
    public boolean deleteTrigger(Integer triggerId) {
        Optional<Trigger> triggerOpt = triggerRepo.findById(triggerId);
        if (triggerOpt.isEmpty()) {
            return false;
        }
        Trigger trigger = triggerOpt.get();
        triggerDeviceRepo.deleteAll(trigger.getTargetDevices());
        triggerRepo.delete(trigger);
        return true;
    }

    public Optional<List<Trigger>> getAllTriggersByDeviceId(Integer deviceId) {
        Optional<Device> deviceOpt = deviceRepo.findById(deviceId);
        if (deviceOpt.isEmpty()) {
            return Optional.empty();
        }
        List<Trigger> triggers = triggerRepo.findAllBySourceDevice(deviceOpt.get()).orElse(List.of());
        return Optional.of(triggers);
    }

}
