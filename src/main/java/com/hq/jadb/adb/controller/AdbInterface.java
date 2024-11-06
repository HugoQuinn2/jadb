package com.hq.jadb.adb.controller;

import com.hq.jadb.device.model.Device;

import java.util.List;

public interface AdbInterface {
    public List<Device> getDevices();
}
