package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.LinkedDevice;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Repo.LinkRepo;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LinkService {

    @Autowired
    private LinkRepo linkRepo;

    @Transactional
    //Links users to devices
    public void linkDevice(User user , Device device) {
        LinkedDevice linkedDevice = new LinkedDevice();
        linkedDevice.setDevice(device);
        linkedDevice.setUser(user);
        linkRepo.save(linkedDevice);
    }

    @Transactional
    public void unlinkDevice(User user , Device device) {
        linkRepo.deleteByUser_IdAndDevice_Id(user.getId(),device.getId());
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

