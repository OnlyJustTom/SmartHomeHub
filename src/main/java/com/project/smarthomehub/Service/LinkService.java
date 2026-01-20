package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.LinkedDevice;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Repo.LinkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Boolean isUserLinkedToDevice(User user , Device device) {
        Optional<LinkedDevice> link = linkRepo.findByUser_Id(user.getId()); //TODO - Fix NULL error here - repo not finding link
        return link.isPresent() && link.get().getDevice().equals(device);
    }

}
