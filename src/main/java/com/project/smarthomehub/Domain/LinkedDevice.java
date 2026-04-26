package com.project.smarthomehub.Domain;

import com.project.smarthomehub.Helpers.LinkedDeviceId;
import jakarta.persistence.*;

@Entity
@IdClass(LinkedDeviceId.class)
public class LinkedDevice {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    // Getters & setters
    public User getUser() {
        return user;
    }

    public Device getDevice() {
        return device;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
