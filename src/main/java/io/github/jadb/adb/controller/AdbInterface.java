package io.github.jadb.adb.controller;

import io.github.jadb.device.model.Device;

import java.util.List;

public interface AdbInterface {
    public List<Device> getDevices();
}
