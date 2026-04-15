package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Helpers.RoutineRequest;
import com.project.smarthomehub.Service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routine")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @PostMapping
    public ResponseEntity<?> createRoutine(@RequestBody RoutineRequest routineRequest) {
        if (routineService.addRoutine(routineRequest).isPresent()) {
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> testRoutine(@RequestBody Integer routineId) {
        if (routineService.testRoutine(routineId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
