package io.github.jadb.device.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceData {
    private String ip;
    private String mac;
    private long storage;
    private long storageUsed;
    private String androidId;
    private String simContract;
    private String androidVersion;
}
