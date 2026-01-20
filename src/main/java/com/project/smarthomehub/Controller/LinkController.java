package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Helpers.Link;
import com.project.smarthomehub.Service.DeviceService;
import com.project.smarthomehub.Service.LinkService;
import com.project.smarthomehub.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserService userService;

    @PutMapping
    public ResponseEntity<?> LinkDeviceToUser(@RequestBody Link link) {
        System.out.println(link.getDeviceID());
        System.out.println(link.getUserID());
        Optional<Device> device = deviceService.getDeviceById(link.getDeviceID());
        Optional<User> user = userService.getUserById(link.getUserID());
        if(device.isPresent() && user.isPresent()) {
            linkService.linkDevice(user.get(), device.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<?> IsUserLinkedToDevice(@RequestBody Link link) {
        Optional<Device> device = deviceService.getDeviceById(link.getDeviceID());
        Optional<User> user = userService.getUserById(link.getUserID());
        if(device.isPresent() && user.isPresent()) {
            if (linkService.isUserLinkedToDevice(user.get(), device.get()))
                return ResponseEntity.ok().build();
            }
        return ResponseEntity.notFound().build();
    }

}

