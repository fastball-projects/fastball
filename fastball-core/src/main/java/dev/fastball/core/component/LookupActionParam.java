package dev.fastball.core.component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookupActionParam<S> {
    private String keyWords;
    private S search;
}
