package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Repo.DeviceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepo deviceRepo;

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
}
