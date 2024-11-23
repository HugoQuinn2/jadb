package io.github.hugoquinn2.jadb.device.model;

import io.github.hugoquinn2.jadb.device.constant.FileType;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@ToString
public class File {
    private String fileName;
    private String user;
    private long size;
    private String path;
    private String absolutePath;
    private FileType fileType;
    private Date lastModify;
}
