package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Domain.Routine;
import com.project.smarthomehub.Helpers.RoutineRequest;
import com.project.smarthomehub.Helpers.RoutineUpdateRequest;
import com.project.smarthomehub.Service.RoutineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoutineControllerTest {

    @Mock
    private RoutineService routineService;

    @InjectMocks
    private RoutineController routineController;

    @Test
    void createRoutineReturnsOkWhenCreated() {
        Routine routine = new Routine();
        routine.setId(1);
        routine.setName("Morning");

        when(routineService.addRoutine(any(RoutineRequest.class))).thenReturn(Optional.of(routine));

        ResponseEntity<?> response = routineController.createRoutine(new RoutineRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Routine body = (Routine) response.getBody();
        assertNotNull(body);
        assertEquals("Morning", body.getName());
    }

    @Test
    void updateRoutineReturnsBadRequestWhenUpdateFails() {
        when(routineService.updateRoutine(any(RoutineUpdateRequest.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = routineController.updateRoutine(new RoutineUpdateRequest());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testRoutineReturnsNotFoundWhenExecutionFails() {
        when(routineService.testRoutine(5)).thenReturn(false);

        ResponseEntity<?> response = routineController.testRoutine(5);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteRoutineReturnsOkWhenDeleted() {
        when(routineService.deleteRoutine(3)).thenReturn(true);

        ResponseEntity<?> response = routineController.deleteRoutine(3);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

