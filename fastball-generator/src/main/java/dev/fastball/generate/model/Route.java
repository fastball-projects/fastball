package dev.fastball.generate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.fastball.meta.utils.RefComponentSerialize;
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
    @JsonSerialize(using = RefComponentSerialize.class)
    private String component;
    private String componentFullName;
    private Object params;
    private List<Route> routes;
}
