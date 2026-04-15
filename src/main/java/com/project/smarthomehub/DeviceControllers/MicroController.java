package com.project.smarthomehub.DeviceControllers;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.DeviceType;
import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Repo.DeviceRepo;
import com.project.smarthomehub.Helpers.DeviceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.smarthomehub.Helpers.MicroControllerResponse;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;


import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class MicroController {

    @Autowired
    DeviceRepo deviceRepo;

    public void ExecuteCommand(DeviceRequest request){
        String IPAddress;
        if(request.getCommandType() == CommandType.GET_INFO){
            IPAddress = "null";
        }
        else{
            IPAddress = deviceRepo.findById(request.getDeviceId()).get().getAPIKeyIP();
        }
        switch (request.getCommandType()){
            case POWER:
                TogglePower(request, IPAddress);
                break;
            case COLOUR:
                break;
            case BRIGHTNESS:
                break;
            case GET_INFO:
                //No IP address is passed as initial program has no stored Addresses, this function discovers them and stores them in the database
                DiscoverDevices();
                break;
            default:
                break;
        }
    }

    void TogglePower(DeviceRequest request, String IPAddress){
        String uri = "http://" + IPAddress + "/api/toggle";
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

    void DiscoverDevices() {
        List<String> devicesIPDiscovered = new ArrayList<>();

        try{
            InetAddress local = InetAddress.getLocalHost();
            System.out.println("getLocalHost(): " + local.getHostAddress());

            NetworkInterface ni = NetworkInterface.getByInetAddress(local);

            if (ni != null) {
                System.out.println("Interface name: " + ni.getName());
                System.out.println("Display name: " + ni.getDisplayName());
                System.out.println("Is up: " + ni.isUp());
                System.out.println("Is loopback: " + ni.isLoopback());
                System.out.println("Is virtual: " + ni.isVirtual());
                System.out.println("Supports multicast: " + ni.supportsMulticast());
            }

            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost().getHostAddress());
            System.out.println("JmDNS instance created with local IP: " + jmdns.getInetAddress().getHostAddress());
            Thread.sleep(2000);

            ServiceInfo[] services = jmdns.list("_smarthub._tcp.local.");

            for(ServiceInfo service : services){
                String deviceIP = service.getHostAddresses()[0];
                System.out.println("Device found at IP: " + deviceIP);
                devicesIPDiscovered.add(deviceIP);
            }

            jmdns.close();
            //Check if device is running webserver by calling *IPAddress*/api/status

            for(String IPAddress : devicesIPDiscovered){
                String uri = "http://" + IPAddress + "/api/status";
                HttpRequest http = HttpRequest.newBuilder()
                        .uri(URI.create(uri))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();
                HttpResponse<String> response;
                try{
                    response = HttpClient.newHttpClient().send(http, HttpResponse.BodyHandlers.ofString());
                    if(response.statusCode() == 200){
                        //System.out.println(response.body());
                        //Deserialize response
                        ObjectMapper mapper = JsonMapper.builder()
                                .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                                .build();

                        MicroControllerResponse device = mapper.readValue(
                                response.body(),
                                MicroControllerResponse.class
                        );
                        //Check if device already exists in database
                        if(deviceRepo.findByName(device.getName()).isEmpty()){
                            //If not, add it to the database
                            Device newDevice = new Device();
                            newDevice.setName(device.getName());
                            newDevice.setType(device.getDeviceType());
                            newDevice.setAPIKeyIP(IPAddress);
                            deviceRepo.save(newDevice);
                            System.out.println("Device " + device.getName() + " added to database with IP: " + IPAddress);
                        }
                        else{
                            System.out.println("Device " + device.getName() + " already exists in database");
                        }
                    }
                    else{
                        continue;
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("Error connecting to device at IP: " + IPAddress);
                    continue;
                }
            }


        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
