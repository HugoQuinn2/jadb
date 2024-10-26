package com.hq.jadb.model;

import com.hq.jadb.constant.DeviceLevel;
import com.hq.jadb.constant.DeviceState;
import com.hq.jadb.controller.AdbController;
import com.hq.jadb.controller.DeviceController;
import lombok.*;

import java.util.List;

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
