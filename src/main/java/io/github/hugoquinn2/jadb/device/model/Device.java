package io.github.hugoquinn2.jadb.device.model;

import io.github.hugoquinn2.jadb.device.constant.DeviceLevel;
import io.github.hugoquinn2.jadb.device.constant.DeviceState;
import io.github.hugoquinn2.jadb.device.controller.DeviceController;
import lombok.*;

@Getter
@Setter
@ToString
public class Device extends DeviceController {
    private String deviceName;
    private String serial;
    private DeviceLevel deviceLevel;
    private DeviceState deviceState;
    private String model;
    private String manufacturer;

    public Device() {
        super(null);
    }

    public Device(String deviceName, String serial, DeviceLevel deviceLevel, DeviceState deviceState, String model, String manufacturer) {
        super(null);
        this.deviceName = deviceName;
        this.serial = serial;
        this.deviceLevel = deviceLevel;
        this.deviceState = deviceState;
        this.model = model;
        this.manufacturer = manufacturer;
        this.device = this;
    }
}
