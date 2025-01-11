package dev.fastball.core.intergration.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectStorageFormDataUpload {

    private String url;

    private String fileUrl;

    private Map<String, String> formData;
}
