package dev.fastball.ui.common;

import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
public class LookupActionInfo {
    private String packageName;
    private String lookupKey;
    private String httpMethod;
    private String httpPath;
}