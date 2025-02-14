package dev.fastball.meta.material;

import lombok.Data;

import java.util.List;

@Data
public class UIMaterial {

    private String materialName;
    private String npmPackage;
    private String npmVersion;
    private String platform;
    private String metaUrl;
    private List<String> componentUrls;
}
