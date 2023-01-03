package dev.fastball.core.material;

import lombok.Data;

import java.util.List;

@Data
public class UIMaterial {

    private String materialName;
    private String npmPackage;
    private String npmVersion;
    private String metaUrl;
    private List<String> componentUrls;
}
