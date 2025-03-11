package dev.fastball.core.material;

import dev.fastball.meta.material.UIMaterial;
import dev.fastball.meta.utils.YamlUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static dev.fastball.core.Constants.FASTBALL_MATERIAL_FILE;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class MaterialRegistry {

    private final Map<Class<?>, UIMaterial> materialMap = new ConcurrentHashMap<>();

    public MaterialRegistry(ClassLoader classLoader) {
    }

    public UIMaterial getMaterial(Class<?> compilerClass) {
        if (materialMap.containsKey(compilerClass)) {
            return materialMap.get(compilerClass);
        }
        return loadMaterial(compilerClass);
    }

    public Collection<UIMaterial> getMaterials() {
        return materialMap.values();
    }

    private UIMaterial loadMaterial(Class<?> compilerClass) {
        UIMaterial material = getMaterialFromClass(compilerClass);
        materialMap.put(compilerClass, material);
        return material;
    }

    public String getJarPath(Class<?> compilerClass) throws URISyntaxException {
        CodeSource codeSource = compilerClass.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            throw new IllegalStateException("The class [" + compilerClass.getName() + "] does not have a code source.");
        }
        URL jarUrl = codeSource.getLocation();
        return new File(jarUrl.toURI()).getAbsolutePath();
    }

    public UIMaterial getMaterialFromClass(Class<?> clazz) {
        try (JarFile jarFile = new JarFile(getJarPath(clazz))) {
            JarEntry entry = jarFile.getJarEntry(FASTBALL_MATERIAL_FILE);
            if (entry != null) {
                try (InputStream inputStream = jarFile.getInputStream(entry)) {
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
                    return material;
                }
            }
            return null;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildUnPkgUrl(UIMaterial material) {
        return "https://unpkg.com/" + material.getNpmPackage() + "@" + material.getNpmVersion();
    }
}
