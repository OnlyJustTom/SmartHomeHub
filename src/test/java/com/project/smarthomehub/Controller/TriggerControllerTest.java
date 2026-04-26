package com.project.smarthomehub.Controller;

import com.project.smarthomehub.Helpers.TriggerControlRequest;
import com.project.smarthomehub.Helpers.TriggerRequest;
import com.project.smarthomehub.Service.TriggerService;
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
class TriggerControllerTest {

    @Mock
    private TriggerService triggerService;

    @InjectMocks
    private TriggerController triggerController;

    @Test
    void createTriggerReturnsBadRequestWhenCreationFails() {
        when(triggerService.addTrigger(any(TriggerRequest.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = triggerController.createTrigger(new TriggerRequest());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Failed to create trigger. Please check the request data and try again.", response.getBody());
    }

    @Test
    void executeTriggerReturnsServiceStatusMessage() {
        when(triggerService.executeTrigger(any(TriggerControlRequest.class))).thenReturn("Trigger executed successfully");

        ResponseEntity<?> response = triggerController.executeTrigger(new TriggerControlRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trigger executed successfully", response.getBody());
    }

    @Test
    void deleteTriggerReturnsNotFoundWhenDeleteFails() {
        when(triggerService.deleteTrigger(77)).thenReturn(false);

        ResponseEntity<?> response = triggerController.deleteTrigger(77);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testTriggerReturnsOkWithStatusMessage() {
        when(triggerService.testTrigger(12)).thenReturn("Trigger executed successfully");

        ResponseEntity<?> response = triggerController.testTrigger(12);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trigger executed successfully", response.getBody());
    }
}

