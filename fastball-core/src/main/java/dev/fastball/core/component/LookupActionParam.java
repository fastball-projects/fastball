package dev.fastball.core.component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookupActionParam<S> {
    private String keywords;
    private S search;
}
