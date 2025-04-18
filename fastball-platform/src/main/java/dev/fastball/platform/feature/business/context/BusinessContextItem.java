package dev.fastball.platform.feature.business.context;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Geng Rong
 */
@JsonSerialize(using = BusinessContextSerializer.class)
public interface BusinessContextItem {
    String id();

    String title();
}
