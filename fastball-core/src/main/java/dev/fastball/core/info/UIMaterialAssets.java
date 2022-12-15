package dev.fastball.core.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UIMaterialAssets {
    public UIMaterialAssets(Collection<UIMaterial> materials) {
        this.packages = materials.stream().map(UIPackage::new).collect(Collectors.toList());
        this.components = materials.stream().map(UIComponent::new).collect(Collectors.toList());
    }

    private List<UIPackage> packages;
    private List<UIComponent> components;
    private Map<String, List<String>> sort;
    private List<String> groupList;


    @Data
    @NoArgsConstructor
    static class UIPackage {

        UIPackage(UIMaterial material) {
            this.packageName = material.getNpmPackage();
            this.version = material.getNpmVersion();
            this.library = material.getMaterialName();
            this.urls = material.getComponentUrls();
        }

        private String title;
        @JsonProperty("package")
        private String packageName;
        private String version;
        private String library;
        private List<String> urls;
        private List<String> editUrls;
    }

    @Data
    static class UIComponent {

        private static final String EXPORT_NAME_SUFFIX = "Meta";

        UIComponent(UIMaterial material) {
            this.exportName = material.getMaterialName() + EXPORT_NAME_SUFFIX;
            this.url = material.getMetaUrl();
            this.npm = new Npm();
            this.npm.packageName = material.getNpmPackage();
            this.npm.version = material.getNpmVersion();
        }

        private String exportName;
        private String url;
        private Npm npm;
        private Map<String, String> urls;

        @Data
        static class Npm {
            @JsonProperty("package")
            private String packageName;
            private String exportName;
            private String version;
        }
    }
}
