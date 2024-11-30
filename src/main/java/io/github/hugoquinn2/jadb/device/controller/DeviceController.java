package io.github.hugoquinn2.jadb.device.controller;

import io.github.hugoquinn2.jadb.adb.config.AdbConfig;
import io.github.hugoquinn2.jadb.device.constant.*;
import io.github.hugoquinn2.jadb.device.exceptions.DeviceNotAvailable;
import io.github.hugoquinn2.jadb.device.exceptions.NoResponse;
import io.github.hugoquinn2.jadb.device.exceptions.PermissionDenied;
import io.github.hugoquinn2.jadb.device.utils.DeviceUtils;
import io.github.hugoquinn2.jadb.device.model.Device;
import io.github.hugoquinn2.jadb.device.model.DeviceApp;
import io.github.hugoquinn2.jadb.device.model.DeviceData;
import io.github.hugoquinn2.jadb.device.model.File;
import io.github.hugoquinn2.jadb.adb.service.AdbService;
import io.github.hugoquinn2.jadb.device.utils.Parsing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class DeviceController implements DeviceInterface {
    private final AdbService adbService;
    private DeviceUtils deviceUtils;
    protected Device device;

    public DeviceController(Device device) {
        adbService = new AdbService();
        this.deviceUtils = new DeviceUtils();
        this.device = device;
    }

    // *****************************************************************
    // **                    Getter functions                         **
    // *****************************************************************
    @Override
    public String getIp(){
        String command = Command.WLAN0.command();
        String responseWLAN0 = shell(command);
        return Parsing.extractIp(responseWLAN0);
    }

    @Override
    public String getMac(){
        String command = Command.WLAN0.command();
        String responseWLAN0 = shell(command);
        return Parsing.extractMAC(responseWLAN0);
    }

    @Override
    public String getSimContract(){
        String command = Command.SIM_CONTRACT.command();
        return shell(command);
    }

    @Override
    public String getAndroidId(){
        String command = Command.ANDROID_ID.command();
        return shell(command);
    }

    @Override
    public String getAndroidVersion(){
        String command = Command.ANDROID_VERSION.command();
        return shell(command);
    }

    @Override
    public long getStorage(){
        String command = Command.STORAGE.command();
        String responseStorage = shell(command);
        return Parsing.extractStorage(responseStorage);
    }

    @Override
    public long getStorageUsed(){
        String command = Command.STORAGE.command();
        String responseStorage = shell(command);
        return Parsing.extractStorageUsed(responseStorage);
    }

    @Override
    public DeviceData getDeviceData() {
        return DeviceData
                .builder()
                .ip(getIp())
                .mac(getMac())
                .androidVersion(getAndroidVersion())
                .simContract(getSimContract())
                .androidId(getAndroidId())
                .storage(getStorage())
                .storageUsed(getStorageUsed())
                .build();
    }

    @Override
    public List<DeviceApp> getDeviceApps() {
        List<String> commandPackage = Parsing.buildCommand(Command.USER_PACKAGES.command(device.getDeviceName()));
        String responsePackages = adbService.executeCommand(commandPackage);
        List<DeviceApp> deviceApps = new ArrayList<>();

        List<String> packages = List.of(responsePackages.split("\n"));

        List<Callable<DeviceApp>> allTasks = new ArrayList<>();

        for (String pack : packages) {
            allTasks.add(() -> deviceUtils.createDeviceApp(device, pack));
        }

        ExecutorService executor = Executors.newFixedThreadPool(allTasks.size());
        try {
            for (int i = 0 ; i < allTasks.size(); i += AdbConfig.maxThread) {
                List<Callable<DeviceApp>> batchTasks = allTasks.subList(i, Math.min(i + AdbConfig.maxThread, allTasks.size()));
                List<Future<DeviceApp>> futures = executor.invokeAll(batchTasks);

                for (Future<DeviceApp> future : futures) {
                    deviceApps.add(future.get());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error extracting applications: " + e.getMessage());
        } finally {
            executor.shutdown();
        }

        return deviceApps;
    }

    @Override
    public List<File> getFilesFrom(File file) {
        if (file == null)
            throw new NullPointerException(ExceptionMessage.NULL_POINTER.message("File"));
        else if (file.getAbsolutePath() == null)
            throw new NullPointerException(ExceptionMessage.NULL_POINTER.message("absolutePath"));
        else if (!file.getFileType().equals(FileType.FOLDER))
            throw new NoResponse(ExceptionMessage.FILE_NOT_FOLDER.message(file.getFileType().toString()));

        return getFilesFrom(file.getAbsolutePath());
    }

    @Override
    public List<File> getFilesFrom(String remotePath) {
        if (!remotePath.endsWith("/"))
            return null;

        List<File> fileDevices = new ArrayList<>();
        List<String> lsFiles = lsM(remotePath);
        String formatStat = "'%n,%s,%U,%y,%A'";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (lsFiles.size() == 1 && lsFiles.getFirst().isBlank())
            throw new NoResponse(String.format("%s: Folder empty or permission no available", remotePath));

        for (String lsFile : lsFiles) {
            String absolutePath = remotePath + lsFile;
            String commandText = String.format("%s %s %s" ,
                    Command.STAT.command(device.getDeviceName()),
                    formatStat,
                    absolutePath);

            String response = shell(commandText);

            String[] reformatStat = Objects.requireNonNull(Parsing.extractOutputStat(response)).split(",");

            if (reformatStat.length == 5) {
                try {
                    // FileType, User, Size, Date, Name
                    File file = File
                            .builder()
                            .fileType(Parsing.textToFileType(reformatStat[0]))
                            .user(reformatStat[1])
                            .size(Long.parseLong(reformatStat[2]))
                            .lastModify(format.parse(reformatStat[3]))
                            .fileName(lsFile)
                            .path(remotePath)
                            .absolutePath(absolutePath)
                            .build();

                    if (file.getFileType().equals(FileType.FOLDER))
                        file.setAbsolutePath(file.getAbsolutePath() + "/");

                    fileDevices.add(file);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }

        return fileDevices;
    }

    // *****************************************************************
    // **                    Action functions                         **
    // *****************************************************************
    @Override
    public boolean ping(String ip) {
        List<String> command = Parsing.buildCommand(Command.PING.command(device.getDeviceName(), ip));
        String response = adbService.executeCommand(command);
        return !response.contains("100% packet loss");
    }

    @Override
    public boolean install(String pathApk) {
        List<String> command = Parsing.buildCommand(Command.INSTALL.command(device.getDeviceName(), pathApk));
        String response = adbService.executeCommand(command);
        return response.contains("Performing Streamed Install\nSuccess\n");
    }

    @Override
    public boolean uninstall(String packageApp) {
        List<String> command = Parsing.buildCommand(Command.UNINSTALL.command(device.getDeviceName(), packageApp));
        String response = adbService.executeCommand(command);
        return response.contains("Success");
    }

    @Override
    public boolean uninstall(DeviceApp deviceApp) {
        return uninstall(deviceApp.getAppPackage());
    }

//    @Override
//    public void getApps(Device device) {
//        List<String> commandPackage = Parsing.buildCommand(Command.USER_PACKAGES.command(device.getDeviceName()));
//        String responsePackages = adbService.executeCommand(commandPackage);
//        List<DeviceApp> deviceApps = new ArrayList<>();
//
//        List<String> packages = List.of(responsePackages.split("\n"));
//        List<Callable<DeviceApp>> ioTasks = new ArrayList<>();
//
//        for (String pack : packages) {
//            ioTasks.add(() -> createDeviceApp(device, pack));
//        }
//
//        try {
//            ExecutorService executor = Executors.newFixedThreadPool(ioTasks.size());
//            List<Future<DeviceApp>> futures = executor.invokeAll(ioTasks);
//
//            for (Future<DeviceApp> future : futures) {
//                deviceApps.add(future.get());
//            }
//        } catch (Exception e) {
//            System.err.println(e);
//        }
//
//        device.setDeviceApps(deviceApps);
//    }

    @Override
    public boolean pull(String remotePath, String localPath) {
        List<String> command = Parsing.buildCommand(Command.PULL.command(device.getDeviceName(), remotePath, localPath));
        String response = adbService.executeCommand(command);
        return response.contains("1 file pulled");
    }

    @Override
    public boolean pull(File file, String localPath) {
        return pull(file.getAbsolutePath(), localPath);
    }

    @Override
    public boolean push(String localPath, String remotePath) {
        List<String> command = Parsing.buildCommand(Command.PUSH.command(device.getDeviceName(), localPath, remotePath));
        String response = adbService.executeCommand(command);
        return response.contains("1 file pushed");
    }

    @Override
    public boolean push(String localPath, File file) {
        return push(localPath, file.getAbsolutePath());
    }

    @Override
    public void root() {
        if (device.getDeviceState() != DeviceState.DEVICE)
            return;

        List<String> command = Parsing.buildCommand(Command.ROOT.command(device.getDeviceName()));
        adbService.executeCommand(command);

        command = Parsing.buildCommand(Command.ROOT_VER.command(device.getDeviceName()));
        String response = adbService.executeCommand(command);

        if (response.contains("root"))
            device.setDeviceLevel(DeviceLevel.ROOT);
    }

    @Override
    public String shell(String command) {
        if (!device.getDeviceState().equals(DeviceState.DEVICE))
            throw new DeviceNotAvailable(
                    ExceptionMessage.DEVICE_NOT_AVAILABLE.message(
                            DeviceState.DEVICE.toString(),
                            device.getDeviceState().toString()
                    )
            );

        String newCommand = String.format("-s %s shell %s", device.getDeviceName(), command);
        List<String> commandList = Parsing.buildCommand(newCommand);
        String response = adbService.executeCommand(commandList);

        if (response.contains("permission denied"))
            throw new PermissionDenied(
                    ExceptionMessage.PERMISSION_DENIED.message(
                            device.getDeviceName(),
                            command
                    )
            );

        return response;
    }

    @Override
    public String adb(String command) {
        String newCommand = String.format("-s %s %s", device.getDeviceName(), command);
        List<String> commandList = Parsing.buildCommand(newCommand);
        return adbService.executeCommand(commandList);
    }

    public List<String> lsM(String from) {
        String command = Command.LS_M.command(from);
        return List.of(shell(command)
                .replaceAll("\n", "")
                .replaceAll("\r", "")
                .replace(" ", "")
                .split(","));
    }

}
