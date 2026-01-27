package com.project.smarthomehub.DeviceControllers;

import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Repo.DeviceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;

@Service
public class LIFX {

    @Autowired
    DeviceRepo deviceRepo;

    public void ExecuteCommand(DeviceRequest request){
        String APIKey = deviceRepo.findById(request.GetDeviceID()).get().getAPIKey();
        switch (request.GetCommandType()){
            case POWER:
                Power(request, APIKey);
            case COLOUR:
                Colour(request);
            case BRIGHTNESS:
                Brightness(request);
            default:
        };
    }
    private void Power(DeviceRequest command, String APIKey) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.lifx.com/v1/lights/all/toggle")) //TODO - Fix Selector issue - should be id or label of device
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + APIKey)
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"duration\":1}"))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
    }

    private void Colour(DeviceRequest command) {
    }

    private void Brightness(DeviceRequest command) {
    }
}
