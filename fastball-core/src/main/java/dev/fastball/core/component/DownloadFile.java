package dev.fastball.core.component;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class DownloadFile {

    private String contentType;

    private String fileName;

    private InputStream inputStream;

}
