package dev.fastball.generate.generator;

import dev.fastball.core.info.component.ComponentInfo;
import dev.fastball.core.utils.JsonUtils;
import dev.fastball.generate.exception.GenerateException;
import dev.fastball.generate.model.Package;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.fastball.generate.Constants.PACKAGE_FILE_NAME;

/**
 * @author gr@fastball.dev
 * @since 2023/1/4
 */
public class PackageJsonGenerator {

    private PackageJsonGenerator() {
    }

    public static void generate(File generatedCodeDir, List<ComponentInfo<?>> componentInfoList, String packageJsonSourcePath) {
        try (InputStream inputStream = PackageJsonGenerator.class.getResourceAsStream(packageJsonSourcePath)) {
            Package nodePackage = JsonUtils.fromJson(inputStream, Package.class);
            Map<String, String> materialPackageMap = new HashMap<>();
            for (ComponentInfo<?> componentInfo : componentInfoList) {
                if (!materialPackageMap.containsKey(componentInfo.material().getNpmPackage())) {
                    materialPackageMap.put(componentInfo.material().getNpmPackage(), componentInfo.material().getNpmVersion());
                }
            }
            for (Map.Entry<String, String> dependency : materialPackageMap.entrySet()) {
                nodePackage.getDependencies().put(dependency.getKey(), dependency.getValue());
            }
            File packageJsonFile = new File(generatedCodeDir, PACKAGE_FILE_NAME);
            FileUtils.write(packageJsonFile, JsonUtils.toPrettyJson(nodePackage), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GenerateException(e);
        }
    }
}
