package com.hq.jadb.controller;

import com.hq.jadb.config.AdbConfig;
import com.hq.jadb.constant.Command;
import com.hq.jadb.constant.DeviceLevel;
import com.hq.jadb.constant.DeviceState;
import com.hq.jadb.model.Device;
import com.hq.jadb.model.DeviceApp;
import com.hq.jadb.model.DeviceData;
import com.hq.jadb.model.File;
import com.hq.jadb.service.AdbService;
import com.hq.jadb.util.Parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AdbController implements AdbInterface{
    private final AdbService  adbService;
    protected Device device;

    public AdbController() {
        this.adbService = new AdbService();
    }

    @Override
    public List<Device> getDevices() {
        List<Device> devices = new ArrayList<>();
        List<String> command = Parsing.buildCommand(Command.DEVICE.command());

        String response = adbService.executeCommand(command);
        List<String> lines = List.of(response.split("\n"));

        for (String line : lines) {
            Device device = Parsing.mapDevice(line);
            if (device != null) {
                root(device);
                getSerial(device);
                getModel(device);
                getManufacturer(device);
                devices.add(device);
            }
        }

        return devices;
    }

    public void root(Device device) {
        if (device.getDeviceState() != DeviceState.DEVICE)
            return;

        List<String> command = Parsing.buildCommand(Command.ROOT.command(device.getDeviceName()));
        adbService.executeCommand(command);

        command = Parsing.buildCommand(Command.ROOT_VER.command(device.getDeviceName()));
        String response = adbService.executeCommand(command);

        if (response.contains("root"))
            device.setDeviceLevel(DeviceLevel.ROOT);
    }

    private void getSerial(Device device) {
        List<String> command = Parsing.buildCommand(Command.SERIAL.command(device.getDeviceName()));
        String response = adbService.executeCommand(command);
        device.setSerial(response);
    }

    private void getModel(Device device) {
        List<String> command = Parsing.buildCommand(Command.MODEL.command(device.getDeviceName()));
        String response = adbService.executeCommand(command);
        device.setModel(response);
    }

    private void getManufacturer(Device device) {
        List<String> command = Parsing.buildCommand(Command.MANUFACTURER.command(device.getDeviceName()));
        String response = adbService.executeCommand(command);
        device.setManufacturer(response);
    }

}
