package com.project.smarthomehub.Helpers;

import java.io.Serializable;
import java.util.Objects;

public class LinkedDeviceId implements Serializable {

    private Integer user;
    private Integer device;

    public LinkedDeviceId() {}

    public LinkedDeviceId(Integer user, Integer device) {
        this.user = user;
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkedDeviceId)) return false;
        LinkedDeviceId that = (LinkedDeviceId) o;
        return Objects.equals(user, that.user) && Objects.equals(device, that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, device);
    }
}
