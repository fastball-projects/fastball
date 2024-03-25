package dev.fastball.generate.model;

import lombok.Data;

import java.util.Map;

@Data
public class FastballFrontendConfig {

    private String logo;
    private String title;
    private String description;
    private String copyright;
    private boolean enableNotice;
    private Map<String,String> devServerProxy;
}
