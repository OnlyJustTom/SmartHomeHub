package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Domain.Routine;
import com.project.smarthomehub.Helpers.RoutineRequest;
import com.project.smarthomehub.Helpers.RoutineUpdateRequest;
import com.project.smarthomehub.Service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/routine")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @PostMapping("/user")
    public ResponseEntity<?> getRoutinesByUserId(@RequestBody Integer userId) {
        return ResponseEntity.ok().body(routineService.getAllRoutineByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<?> createRoutine(@RequestBody RoutineRequest routineRequest) {
        Routine routine = routineService.addRoutine(routineRequest).orElse(null);
        if (routine != null) {
            return ResponseEntity.ok().body(routine);
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping
    public ResponseEntity<?> updateRoutine(@RequestBody RoutineUpdateRequest routineRequest) {
        Routine routine = routineService.updateRoutine(routineRequest).orElse(null);
        if (routine != null) {
            return ResponseEntity.ok().body(routine);
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

    @DeleteMapping
    public ResponseEntity<?> deleteRoutine(@RequestBody Integer routineId) {
        if (routineService.deleteRoutine(routineId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
