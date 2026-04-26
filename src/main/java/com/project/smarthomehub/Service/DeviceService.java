package com.project.smarthomehub.Service;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.DeviceControllers.LIFX;
import com.project.smarthomehub.DeviceControllers.MicroController;
import com.project.smarthomehub.DeviceType;
import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Repo.DeviceRepo;
import com.project.smarthomehub.Repo.LinkRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
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
    @Autowired
    private LinkRepo linkRepo;

    @Transactional
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

    public List<Device> getAllDevices() {
        return deviceRepo.findAll();
    }

    public boolean DecodeDeviceCommand(DeviceRequest Request){
        System.out.println(Request.toString());
        //Check if user and device are linked, if the command is GET_INFO then the user and device don't need to be linked
        if(!linkService.isUserLinkedToDevice(Request.getUserId(), Request.getDeviceId()) && Request.getCommandType() != CommandType.GET_INFO){
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

    @Transactional
    public Optional<Device> updateDevice(Device device) {
        if (!doesDeviceExist(device.getName())) {
            return Optional.empty();
        }
        else{
            deviceRepo.save(device);
            return Optional.of(device);
        }
    }

    @Transactional
    public Optional<Device> deleteDevice(Device device) {
        if (!doesDeviceExist(device.getName())) {
            return Optional.empty();
        }
        else{
            deviceRepo.delete(device);
            return Optional.of(device);
        }
    }

    @Transactional
    public boolean resetMicrocontroller(Integer microcontrollerId) {
        Optional<Device> deviceOpt = deviceRepo.findById(microcontrollerId);
        System.out.println("Attempting to reset microcontroller with ID: " + deviceOpt.get().getId());
        if (deviceOpt.isEmpty() || deviceOpt.get().getType() == DeviceType.LIFX) {
            System.out.println("Device not found or device is not a microcontroller");
            return false;
        }


        Device device = deviceOpt.get();

        String ipAddress = device.getAPIKeyIP();
        if (ipAddress == null || ipAddress.isBlank()) {
            return false;
        }
        String baseUrl = "http://" + ipAddress;
        System.out.println("Sending reset command to microcontroller at: " + baseUrl + "/reset");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/reset"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.discarding());

            if(response.statusCode() == 200) {
                System.out.println("Microcontroller reset successfully");
                deleteDevice(device);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
