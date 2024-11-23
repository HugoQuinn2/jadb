package io.github.jadb.device.utils;

import io.github.jadb.device.constant.DeviceLevel;
import io.github.jadb.device.constant.DeviceState;
import io.github.jadb.device.constant.FileType;
import io.github.jadb.device.model.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parsing {
    public static List<String> buildCommand(String args) {
        List<String> commandParts = new ArrayList<>();

        Pattern pattern = Pattern.compile("'([^']*)'|\\S+");
        Matcher matcher = pattern.matcher(args);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                commandParts.add(matcher.group(1));
            } else {
                commandParts.add(matcher.group());
            }
        }

        return commandParts;
    }

    public static Device mapDevice(String line) {
        List<String> data = List.of(line.split("\t"));

        if (data.size() != 2)
            return null;

        return new Device(
                data.get(0),
                null,
                DeviceLevel.SHELL,
                mapDeviceState(data.get(1)),
                null,
                null
        );
    }

    public static DeviceState mapDeviceState(String state){
        if (state.contains("device"))
            return DeviceState.DEVICE;
        if (state.contains("offline"))
            return DeviceState.OFFLINE;

        return null;
    }

    public static String extractIp(String response) {
        String pat = "inet ([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})";

        Pattern pattern = Pattern.compile(pat);
        Matcher matcher = pattern.matcher(response);

        return matcher.find() ? matcher.group(1) : null;
    }

    public static String extractMAC(String response) {
        String pat = "link/ether ([0-9a-fA-F:]{17})";

        Pattern pattern = Pattern.compile(pat);
        Matcher matcher = pattern.matcher(response);

        return matcher.find() ? matcher.group().replace("link/ether ", "").toUpperCase() : null;
    }

    public static long extractStorage(String response) {
        long totalUsed = 0;
        long totalAvailable = 0;

        String[] lines = response.split("\n");

        for (String line : lines) {
            String[] columns = line.trim().split("\\s+");
            if (columns[0].startsWith("/dev/block/")) {
                totalUsed += Long.parseLong(columns[2]) + Long.parseLong(columns[3]);
                totalAvailable += Long.parseLong(columns[3]);
            }
        }

        return totalUsed;
    }

    public static long extractStorageUsed(String response) {
        long totalAvailable = 0;

        String[] lines = response.split("\n");

        for (String line : lines) {
            String[] columns = line.trim().split("\\s+");
            if (columns[0].startsWith("/dev/block/")) {
                totalAvailable += Long.parseLong(columns[3]);
            }
        }

        return totalAvailable;
    }

    public static String extractPackage(String response) {
        String[] extract = response.split(":");
        if (extract.length == 2)
            return extract[1];

        return null;
    }

    public static String extractNameFromBaseApk(String baseApk) {
        if (baseApk == null)
            return null;

        List<String> fullBaseApk = List.of(baseApk.split("/"));
        String appName = fullBaseApk.getLast().replace(".apk", "");

        if (appName.contains("base"))
            return null;

        return appName;
    }

    public static String extractOutputStat(String output)  {
        String[] data = output.split(",");

        FileType fileType = data.length > 4 ? getFileType(data[4]) : null;
        String user = data.length > 2 ? data[2] : null;
        String size = data.length > 1 ? data[1] : null;
        String date = data.length > 3 ? data[3] : null;
        String name = data.length > 0 ? List.of(data[0].split("/")).get(List.of(data[0].split("/")).size() - 1) : "N/A";

        // File, Size B, User, Last Modify, Permission -> FileType, User, Size, Date, Name
        return  String.format("%s,%s,%s,%s,%s",
                fileType,
                user,
                size,
                date,
                name
        );
    }

    private static FileType getFileType(String file) {
        if (file.startsWith("d")) {
            return FileType.FOLDER;
        } else if (file.startsWith("-")) {
            return FileType.FILE;
        } else if (file.startsWith("i")) {
            return FileType.SYMBOLIC_LINK;
        }

        return FileType.INDETERMINATE;
    }

    public static FileType textToFileType(String fileType) {
        if (fileType.equals("FOLDER"))
            return FileType.FOLDER;
        if (fileType.equals("FILE"))
            return FileType.FILE;
        if (fileType.equals("SYMBOLIC_LINK"))
            return FileType.SYMBOLIC_LINK;

        return FileType.INDETERMINATE;
    }
}
