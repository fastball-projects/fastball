package dev.fastball.core.component.runtime;

import dev.fastball.core.component.LookupActionComponent;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author gr@fastball.dev
 * @since 2022/12/15
 */
@Data
public class LookupActionBean {

    private String lookupActionKey;
    private LookupActionComponent lookupAction;
    private Method lookupMethod;
}
