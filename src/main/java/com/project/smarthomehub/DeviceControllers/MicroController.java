package com.project.smarthomehub.DeviceControllers;

import com.project.smarthomehub.Helpers.DeviceRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class MicroController {

    public void ExecuteCommand(DeviceRequest request){
        switch (request.getCommandType()){
            case POWER:
                TogglePower(request);
                break;
            case COLOUR:
                break;
            case BRIGHTNESS:
                break;
            case GET_INFO:
                break;
            default:
                break;
        }
    }

    void TogglePower(DeviceRequest request){
        String uri = "http://" + request.getCommandData() + "/api/toggle";
        System.out.println(uri);
        HttpRequest http = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response;
        try{
            response = HttpClient.newHttpClient().send(http, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
