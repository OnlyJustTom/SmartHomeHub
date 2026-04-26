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
        Optional<Device> deviceToGet = deviceService.getDeviceById(device.getId());
        if (deviceToGet.isPresent()) {
            return ResponseEntity.ok().body(deviceToGet.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDevices() {
        return ResponseEntity.ok().body(deviceService.getAllDevices());
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

    @PatchMapping
    public ResponseEntity<?> updateDevice(@RequestBody Device device) {
        Optional<Device> DeviceToUpdate = deviceService.updateDevice(device);
        if (DeviceToUpdate.isPresent()) {
            return ResponseEntity.ok().body(DeviceToUpdate.get());
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping
        public ResponseEntity<?> deleteDevice(@RequestBody Device device) {
            Optional<Device> deviceToDelete = deviceService.deleteDevice(device);
            if (deviceToDelete.isPresent()) {
                return ResponseEntity.ok().body(deviceToDelete.get());
            }
            return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/microcontroller")
    public ResponseEntity<?> resetMicrocontroller(@RequestBody Integer microcontrollerId) {
        boolean result = deviceService.resetMicrocontroller(microcontrollerId);
        if (result) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

}
