package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.LinkedDevice;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Repo.LinkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LinkService {

    @Autowired
    private LinkRepo linkRepo;

    //Links users to devices
    public void linkDevice(User user , Device device) {
        LinkedDevice linkedDevice = new LinkedDevice();
        linkedDevice.setDevice(device);
        linkedDevice.setUser(user);
        linkRepo.save(linkedDevice);
    }

    public Boolean isUserLinkedToDevice(Integer userID, Integer deviceID) {
        return linkRepo.existsByUser_IdAndDevice_Id(userID,deviceID);
    }
    public List<Device> userDevices(Integer userID) {
        List<LinkedDevice> linkedDevices = linkRepo.findAllByUser_Id(userID);
        List<Device> devices = new ArrayList<>();

        for (LinkedDevice linkedDevice : linkedDevices) {
            devices.add(linkedDevice.getDevice());
        }
        return devices;
    }
}

