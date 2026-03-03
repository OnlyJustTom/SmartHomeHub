package com.project.smarthomehub.Service;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.DeviceControllers.LIFX;
import com.project.smarthomehub.DeviceControllers.MicroController;
import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Repo.DeviceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepo deviceRepo;
    @Autowired
    private LinkService linkService;
    @Autowired
    LIFX lifx;
    @Autowired
    MicroController microController;
    public Optional<Device> addDevice(Device device) {
            deviceRepo.save(device);
            return Optional.of(device);
    }

    public Optional<Device> getDeviceById(Integer id) {
        Optional<Device> deviceToFind = deviceRepo.findById(id);
        if (deviceToFind.isPresent()) {
            return deviceToFind;
        }
        return Optional.empty();
    }

    private boolean doesDeviceExist(String name) {
        Optional<Device> device = deviceRepo.findByName(name);
        return device.isPresent();
    }

    public boolean DecodeDeviceCommand(DeviceRequest Request){
        System.out.println(Request.toString());
        //Check if user and device are linked, if the command is GET_INFO then the user and device dont need to be linked
        if(!linkService.isUserLinkedToDevice(Request.getUserId(), Request.getDeviceId()) && Request.getCommandType() != CommandType.GET_INFO){
            //Devices are not linked
            System.out.println("User is not linked to the device / User or device doesnt exist");
            return false;
        }
        switch (Request.getDeviceType()) {
            case LIFX:
                lifx.ExecuteCommand(Request);
                break;
            case MICROCONTROLLER:
                microController.ExecuteCommand(Request);
        }
        return true;
    }
}
