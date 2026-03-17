package com.project.smarthomehub.DeviceControllers;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.DeviceType;
import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Helpers.DeviceRequest;
import com.project.smarthomehub.Helpers.LifxResponse;
import com.project.smarthomehub.Repo.DeviceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;


import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class LIFX {

    @Autowired
    DeviceRepo deviceRepo;

    public void ExecuteCommand(DeviceRequest request){
        String APIKey;
        String Name = "all";
        String selector = "";
        if(request.getCommandType() != CommandType.GET_INFO){
            APIKey = deviceRepo.findById(request.getDeviceId()).get().getAPIKeyIP();
            Name = "label:" + deviceRepo.findById(request.getDeviceId()).get().getName();
            selector = URLEncoder.encode(Name, StandardCharsets.UTF_8).replace("+", "%20"); // fix form encoding -> URI encoding;
            System.out.println(Name);
        }else{
            //Only the API key is passed by the user on initial discovery of lights after this all API keys are stored in the DB
            APIKey = request.getCommandData();
        }

        switch (request.getCommandType()){
            case POWER:
                Power(request, APIKey, selector);
                break;
            case COLOUR:
                Colour(request, APIKey, selector);
                break;
            case BRIGHTNESS:
                Brightness(request, APIKey, selector);
                break;
            case GET_INFO:
                getInfo(request, APIKey);
                break;
            default:
                break;
        }
    }
    private void Power(DeviceRequest command, String APIKey, String selector) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.lifx.com/v1/lights/" + selector + "/toggle")) //TODO - Fix Selector issue - should be id or label of device - almost done
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + APIKey)
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"duration\":1}"))
                .build();
        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
    }

    private void Colour(DeviceRequest command, String APIKey, String selector) {
        String colour = command.getCommandData();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.lifx.com/v1/lights/" + selector + "/state"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + APIKey)
                .method("PUT", HttpRequest.BodyPublishers.ofString("{\"duration\":1,\"fast\":false,\"color\":\"" + colour + "\"}"))
                .build();
        HttpResponse<String> response;
        try{
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
    }

    private void Brightness(DeviceRequest command, String APIKey, String selector) {
        float brightness = Float.parseFloat(command.getCommandData());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.lifx.com/v1/lights/" + selector + "/state"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + APIKey)
                .method("PUT", HttpRequest.BodyPublishers.ofString("{\"duration\":1,\"fast\":false,\"brightness\":" + brightness + "}"))
                .build();
        HttpResponse<String> response;
        try{
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
    }

    private void getInfo(DeviceRequest command, String APIKey) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.lifx.com/v1/lights/all"))
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + APIKey)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;

        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            ObjectMapper mapper = JsonMapper.builder()
                    .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                    .build();

            List<LifxResponse> lights = mapper.readValue(
                    response.body(),
                    new TypeReference<List<LifxResponse>>() {}
            );


            for (LifxResponse light : lights) {
                System.out.println(light.getLabel() + " | " + light.getPower());
                Device newDevice = new Device();
                newDevice.setAPIKeyIP(APIKey);
                newDevice.setName(light.getLabel());
                newDevice.setType(DeviceType.LIFX);
                deviceRepo.save(newDevice);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
