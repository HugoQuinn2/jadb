package com.hq.jadb.device.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceApp {
    private String appName;
    private String appPackage;
    private String baseApk;
}
