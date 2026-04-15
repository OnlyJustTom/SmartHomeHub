package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Domain.Trigger;
import com.project.smarthomehub.Helpers.TriggerControlRequest;
import com.project.smarthomehub.Service.TriggerService;
import com.project.smarthomehub.Helpers.TriggerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/triggers")
public class TriggerController {

    @Autowired
    private TriggerService triggerService;

    @PutMapping
    public ResponseEntity<?> createTrigger(@RequestBody TriggerRequest triggerRequest) {
        Optional<Trigger> trigger = triggerService.addTrigger(triggerRequest);
        if(trigger.isEmpty()){
            return ResponseEntity.badRequest().body("Failed to create trigger. Please check the request data and try again.");
        }
        else {
            return ResponseEntity.ok(trigger);
        }
    }

    @PostMapping
    public ResponseEntity<?> executeTrigger(@RequestBody TriggerControlRequest triggerControlRequest) {
        String statusMsg = triggerService.executeTrigger(triggerControlRequest);
        return ResponseEntity.ok(statusMsg);
    }
}
