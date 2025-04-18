package dev.fastball.platform.feature.business.context;

/**
 * @author Geng Rong
 */
public class BusinessContext {
    private static final ThreadLocal<BusinessContextItem> BUSINESS_CONTEXT = new ThreadLocal<>();

    public static <T extends BusinessContextItem> T get() {
        return (T) BUSINESS_CONTEXT.get();
    }

    public static <T extends BusinessContextItem> void set(T businessContextItem) {
        BUSINESS_CONTEXT.set(businessContextItem);
    }

    public static void remove() {
        BUSINESS_CONTEXT.remove();
    }
}
