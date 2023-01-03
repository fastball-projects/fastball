package dev.fastball.portal;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.fastball.core.utils.JsonUtils;
import dev.fastball.core.component.ComponentInfo_AutoValue;
import dev.fastball.core.material.MaterialRegistry;
import dev.fastball.core.utils.YamlUtils;
import dev.fastball.core.component.ComponentInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public class DevServer implements WebMvcConfigurer, InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource menuResource = applicationContext.getResource("classpath:/fastball-menu.yml");
        Map<String, MenuInfo> menus;
        try(InputStream inputStream = menuResource.getInputStream()) {
            menus = YamlUtils.fromYaml(inputStream, new TypeReference<Map<String, MenuInfo>>(){});
        }
        List<ComponentInfo<?>> componentInfoList = Arrays.stream(applicationContext.getResources("classpath*:/FASTBALL-INF/**/*.fbv.json"))
                .map(resource -> {
                    try {
                        ComponentInfo<?> componentInfo = JsonUtils.fromJson(resource.getInputStream(), ComponentInfo_AutoValue.class);
                        return componentInfo;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        CodeGenerator.generate(new File("./fastball-workspace"), new MaterialRegistry(DevServer.class.getClassLoader()), componentInfoList, menus);
    }
}
