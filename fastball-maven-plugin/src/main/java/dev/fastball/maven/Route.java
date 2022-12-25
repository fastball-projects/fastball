package dev.fastball.maven;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/26
 */
@Data
@Builder
public class Route {
    private String name;
    private String path;
    @JsonSerialize(using = ComponentSerialize.class)
    private String component;
}
