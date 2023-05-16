package dev.fastball.core.component;

import java.util.Map;

public interface DataRecord {
    Map<String, Boolean> getRecordActionAvailableFlags();

    void setRecordActionAvailableFlags(Map<String, Boolean> recordActionAvailableFlags);
}
