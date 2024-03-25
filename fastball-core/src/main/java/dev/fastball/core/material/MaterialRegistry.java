package dev.fastball.core.material;

import dev.fastball.core.utils.YamlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dev.fastball.core.Constants.FASTBALL_MATERIAL_PATTERN;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class MaterialRegistry {

    private final Map<String, UIMaterial> materialMap = new ConcurrentHashMap<>();

    public MaterialRegistry(ClassLoader classLoader) {
    }

    public UIMaterial getMaterial(Class<?> compilerClass) {
        return loadMaterial(compilerClass);
    }

    public Collection<UIMaterial> getMaterials() {
        return materialMap.values();
    }

    private UIMaterial loadMaterial(Class<?> compilerClass) {
        URL url = compilerClass.getResource(FASTBALL_MATERIAL_PATTERN);
        if (url == null) {
            return null;
        }
        if (!materialMap.containsKey(url.getFile())) {
            try (InputStream inputStream = url.openStream()) {
                UIMaterial material = YamlUtils.fromYaml(inputStream, UIMaterial.class);
                if (material.getMetaUrl() != null) {
                    material.setMetaUrl(buildUnPkgUrl(material) + "/build/lowcode/meta.js");
                }
                if (material.getComponentUrls() == null || material.getComponentUrls().isEmpty()) {
                    material.setComponentUrls(Arrays.asList(
                            buildUnPkgUrl(material) + "/build/lowcode/view.js",
                            buildUnPkgUrl(material) + "/build/lowcode/view.css"
                    ));
                }
                materialMap.put(url.getFile(), material);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return materialMap.get(url.getFile());
    }

    private String buildUnPkgUrl(UIMaterial material) {
        return "https://unpkg.com/" + material.getNpmPackage() + "@" + material.getNpmVersion();
    }
}
