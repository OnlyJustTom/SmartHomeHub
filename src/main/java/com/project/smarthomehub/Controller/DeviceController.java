package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public ResponseEntity<Device> getDevice(@RequestBody Device device) {
        Optional<Device> foundDevice = deviceService.getDeviceById(device.getId());
        return ResponseEntity.ok().body(foundDevice.get());
    }

    @PutMapping
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        Optional<Device> DeviceToAdd = deviceService.addDevice(device);
        if (DeviceToAdd.isPresent()) {
            return ResponseEntity.ok().body(DeviceToAdd.get());
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/control")
    public ResponseEntity<?> controlDevice(@RequestBody DeviceRequest deviceRequest) {
        boolean result = deviceService.DecodeDeviceCommand(deviceRequest);
        if (result) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

}
