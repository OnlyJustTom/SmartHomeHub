package com.project.smarthomehub.DeviceControllers;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.Helpers.DeviceRequest;
import org.springframework.stereotype.Service;

@Service
public class MicroController {

    public void ExecuteCommand(DeviceRequest request){
        switch (request.getCommandType()){
            case POWER:
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


}
