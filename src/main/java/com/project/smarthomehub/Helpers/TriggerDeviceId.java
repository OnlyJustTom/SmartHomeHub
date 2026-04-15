package com.project.smarthomehub.Helpers;

import java.io.Serializable;
import java.util.Objects;

public class TriggerDeviceId implements Serializable {

    private Integer trigger;
    private Integer device;

    public TriggerDeviceId() {}

    public TriggerDeviceId(Integer trigger, Integer device) {
        this.trigger = trigger;
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TriggerDeviceId)) return false;
        TriggerDeviceId that = (TriggerDeviceId) o;
        return Objects.equals(trigger, that.trigger) && Objects.equals(device, that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trigger, device);
    }
}

