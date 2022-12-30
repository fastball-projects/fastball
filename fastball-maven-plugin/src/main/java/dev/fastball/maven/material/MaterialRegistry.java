package dev.fastball.maven.material;

import dev.fastball.core.info.UIMaterial;
import dev.fastball.core.info.UIMaterialAssets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class MaterialRegistry {
    private static final String FASTBALL_MATERIAL_PATTERN = "classpath*:/fastball-material.yml";
    private final Map<String, UIMaterial> materialMap = new ConcurrentHashMap<>();
    private final UIMaterialAssets assets;
    private final Yaml yaml = new Yaml();

    public MaterialRegistry(ClassLoader classLoader) {
        assets = new UIMaterialAssets(loadMaterials(classLoader));
    }

    public UIMaterialAssets getAssets() {
        return assets;
    }

    public UIMaterial getMaterial(String sourcePath) {
        return materialMap.get(sourcePath);
    }

    public Collection<UIMaterial> getMaterials() {
        return materialMap.values();
    }

    private Collection<UIMaterial> loadMaterials(ClassLoader classLoader) {
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
            Resource[] materialYamlFile = resourcePatternResolver.getResources(FASTBALL_MATERIAL_PATTERN);
            return Arrays.stream(materialYamlFile).map(this::loadMaterialFromYamlResource).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private UIMaterial loadMaterialFromYamlResource(Resource resource) {
        File sourcePath;
        try {
            if (resource.isFile() && resource.getFile().isDirectory()) {
                sourcePath = resource.getFile();
            } else {
                JarURLConnection connection = (JarURLConnection) resource.getURL().openConnection();
                sourcePath = new File(connection.getJarFileURL().toURI());
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try (InputStream inputStream = resource.getInputStream()) {
            UIMaterial material = yaml.loadAs(inputStream, UIMaterial.class);
            if (StringUtils.isEmpty(material.getMetaUrl())) {
                material.setMetaUrl(buildUnPkgUrl(material) + "/build/lowcode/meta.js");
            }
            if (CollectionUtils.isEmpty(material.getComponentUrls())) {
                material.setComponentUrls(Arrays.asList(
                        buildUnPkgUrl(material) + "/build/lowcode/view.js",
                        buildUnPkgUrl(material) + "/build/lowcode/view.css"
                ));
            }
            materialMap.put(sourcePath.getAbsolutePath(), material);
            return material;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildUnPkgUrl(UIMaterial material) {
        return "https://unpkg.com/" + material.getNpmPackage() + "@" + material.getNpmVersion();
    }
}
