package dev.fastball.maven;

import lombok.Data;

import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/26
 */
@Data
public class PackageModel {
    private String name;
    private String version;
    private Map<String, String> scripts;
    private Map<String, String> dependencies;
    private Map<String, String> devDependencies;
}