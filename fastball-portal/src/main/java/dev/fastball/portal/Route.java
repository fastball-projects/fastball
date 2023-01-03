package dev.fastball.portal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.util.List;

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
    private List<Route> routes;
}
