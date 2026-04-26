package com.project.smarthomehub.Service;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.LinkedDevice;
import com.project.smarthomehub.Domain.User;
import com.project.smarthomehub.Repo.LinkRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepo linkRepo;

    @InjectMocks
    private LinkService linkService;

    @Test
    void linkDeviceSavesRelationship() {
        User user = new User();
        user.setId(1);
        Device device = new Device();
        device.setId(2);

        linkService.linkDevice(user, device);

        ArgumentCaptor<LinkedDevice> captor = ArgumentCaptor.forClass(LinkedDevice.class);
        verify(linkRepo).save(captor.capture());
        assertEquals(1, captor.getValue().getUser().getId());
        assertEquals(2, captor.getValue().getDevice().getId());
    }

    @Test
    void unlinkDeviceDeletesRelationshipByIds() {
        User user = new User();
        user.setId(10);
        Device device = new Device();
        device.setId(20);

        linkService.unlinkDevice(user, device);

        verify(linkRepo).deleteByUser_IdAndDevice_Id(10, 20);
    }

    @Test
    void userDevicesReturnsOnlyDeviceListFromLinks() {
        Device d1 = new Device();
        d1.setId(1);
        Device d2 = new Device();
        d2.setId(2);

        LinkedDevice l1 = new LinkedDevice();
        l1.setDevice(d1);
        LinkedDevice l2 = new LinkedDevice();
        l2.setDevice(d2);

        when(linkRepo.findAllByUser_Id(99)).thenReturn(List.of(l1, l2));

        List<Device> result = linkService.userDevices(99);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }
}

