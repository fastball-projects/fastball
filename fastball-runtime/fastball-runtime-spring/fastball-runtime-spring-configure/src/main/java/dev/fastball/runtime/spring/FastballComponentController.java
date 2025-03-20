package dev.fastball.runtime.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fastball.core.Result;
import dev.fastball.core.component.DataRecord;
import dev.fastball.core.component.DataResult;
import dev.fastball.core.component.DownloadFile;
import dev.fastball.core.component.LookupActionComponent;
import dev.fastball.core.component.RecordActionFilter;
import dev.fastball.core.component.runtime.ComponentBean;
import dev.fastball.core.component.runtime.ComponentRegistry;
import dev.fastball.core.component.runtime.LookupActionBean;
import dev.fastball.core.component.runtime.LookupActionRegistry;
import dev.fastball.core.component.runtime.RecordActionFilterRegistry;
import dev.fastball.core.component.runtime.UIApiMethod;
import dev.fastball.core.exception.BusinessException;
import dev.fastball.core.exception.FastballRuntimeException;
import dev.fastball.core.intergration.storage.ObjectStorageFormDataUpload;
import dev.fastball.core.intergration.storage.ObjectStorageService;
import dev.fastball.core.intergration.storage.ObjectStorageUpload;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/15
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fastball")
public class FastballComponentController {
    private final ComponentRegistry componentRegistry;
    private final LookupActionRegistry lookupActionRegistry;
    private final RecordActionFilterRegistry recordActionFilterRegistry;
    private final ObjectMapper objectMapper;
    private final ObjectStorageService objectStorageService;

    @PostMapping("/storage/generateUploadUrl")
    public Result<ObjectStorageUpload> generateUploadUrl() {
        ObjectStorageUpload upload = objectStorageService.generateUploadUrl(objectStorageService.getDefaultBucket(), objectStorageService.generateObjectName("public"));
        return Result.success(upload);
    }

    @PostMapping("/storage/generatePresignedPostFormData")
    public Result<ObjectStorageFormDataUpload> generatePresignedPostFormData() {
        ObjectStorageFormDataUpload upload = objectStorageService.generatePresignedPostFormData(objectStorageService.getDefaultBucket(), "public");
        return Result.success(upload);
    }

    @PostMapping("/storage/upload")
    public Result<String> uploadFiles(@RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return Result.success(objectStorageService.upload(file.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping(value = "/component/{componentKey}/action/{actionKey}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object invokeAction(@PathVariable String componentKey, @PathVariable String actionKey, @RequestPart("data") String dataJson, @RequestPart(value = "file", required = false) MultipartFile file) throws IOException, InvocationTargetException, IllegalAccessException {
        return invokeComponentAction(componentKey, actionKey, dataJson, file);
    }

    @PostMapping(value = "/component/{componentKey}/action/{actionKey}/json")
    public Object invokeAction(@PathVariable String componentKey, @PathVariable String actionKey, @RequestBody String dataJson) throws IOException, InvocationTargetException, IllegalAccessException {
        return invokeComponentAction(componentKey, actionKey, dataJson, null);
    }

    @PostMapping(value = "/component/{componentKey}/downloadAction/{actionKey}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void invokeComponentDownloadAction(@PathVariable String componentKey, @PathVariable String actionKey, @RequestPart("data") String dataJson, @RequestPart(value = "file", required = false) MultipartFile file, HttpServletResponse response) throws IOException, InvocationTargetException, IllegalAccessException {
        ComponentBean componentBean = componentRegistry.getComponentBean(componentKey);
        UIApiMethod actionMethod = componentBean.getMethodMap().get(actionKey);
        Object result = invokeActionMethod(componentBean.getComponent(), actionMethod.getMethod(), dataJson, file, response);
        if (result instanceof DownloadFile) {
            DownloadFile downloadFile = ((DownloadFile) result);
            response.setContentType(downloadFile.getContentType());
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(downloadFile.getFileName(), "UTF-8"));
            response.addHeader("Cache-Control", "max-age=0");
            response.setCharacterEncoding("utf-8");
            IOUtils.copy(downloadFile.getInputStream(), response.getOutputStream());
        }
    }

    @PostMapping("/lookup/{lookupKey}")
    public Object loadLookupItems(@PathVariable String lookupKey, ServletRequest request) throws IOException, InvocationTargetException, IllegalAccessException {
        LookupActionBean lookupActionBean = lookupActionRegistry.getLookupActionBean(lookupKey);
        if (lookupActionBean == null) {
            throw new RuntimeException("Lookup action not found");
        }
        LookupActionComponent lookupActionComponent = lookupActionBean.getLookupAction();
        Method actionMethod = lookupActionBean.getLookupMethod();
        Object data = invokeActionMethod(lookupActionComponent, actionMethod, request);
        if (data instanceof Result) {
            return data;
        }
        return Result.success(data);
    }

    private void doRecordActionFilter(DataRecord record, ComponentBean componentBean) {
        Map<String, Boolean> recordActionAvailableFlags = new HashMap<>();
        componentBean.getRecordActionFilterClasses().forEach((key, value) -> {
            RecordActionFilter recordActionFilter = recordActionFilterRegistry.getRecordActionFilter(value);
            if (recordActionFilter != null) {
                recordActionAvailableFlags.put(key, recordActionFilter.filter(record));
            }
        });
        record.setRecordActionAvailableFlags(recordActionAvailableFlags);
    }

    public Object invokeComponentAction(String componentKey, String actionKey, String dataJson, MultipartFile file) throws IOException, InvocationTargetException, IllegalAccessException {
        ComponentBean componentBean = componentRegistry.getComponentBean(componentKey);
        UIApiMethod actionMethod = componentBean.getMethodMap().get(actionKey);
        Object data = invokeActionMethod(componentBean.getComponent(), actionMethod.getMethod(), dataJson, file, null);
        Result<?> result;
        if (data instanceof Result) {
            result = (Result<?>) data;
            data = result.getData();
        } else {
            result = Result.success(data);
        }
        if (actionMethod.isNeedRecordFilter()) {
            if (data instanceof DataResult) {
                DataResult<?> dataResult = (DataResult<?>) data;
                if (dataResult.getData() != null) {
                    dataResult.getData().stream().filter(DataRecord.class::isInstance).forEach(dataRecord -> doRecordActionFilter((DataRecord) dataRecord, componentBean));
                }
            } else if (data instanceof DataRecord) {
                doRecordActionFilter((DataRecord) data, componentBean);
            }
        }
        return result;
    }

    private Object invokeActionMethod(Object bean, Method actionMethod, ServletRequest request) throws IOException, IllegalAccessException {
        JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
        return invokeActionMethod(bean, actionMethod, jsonNode, null, null);
    }

    private Object invokeActionMethod(Object bean, Method actionMethod, String dataJson, MultipartFile file, HttpServletResponse response) throws IOException, IllegalAccessException {
        JsonNode jsonNode = objectMapper.readTree(dataJson);
        return invokeActionMethod(bean, actionMethod, jsonNode, file, response);
    }

    private Object invokeActionMethod(Object bean, Method actionMethod, JsonNode jsonNode, MultipartFile file, HttpServletResponse response) throws IOException, IllegalAccessException {
        Parameter[] parameterList = actionMethod.getParameters();
        Object[] params = new Object[parameterList.length];
        for (int i = 0; i < Math.min(jsonNode.size(), params.length); i++) {
            Parameter parameter = parameterList[i];
            if (jsonNode.get(i) == null) {
                continue;
            }
            try {
                Object param = objectMapper.readValue(jsonNode.get(i).toString(), new TypeReference<Object>() {
                    @Override
                    public Type getType() {
                        return parameter.getParameterizedType();
                    }
                });
                params[i] = param;
            } catch (Exception e) {
                log.warn("Method [{}] param [{}] read json failed", actionMethod, i, e);
            }
        }
        if (file != null) {
            for (int i = 0; i < parameterList.length; i++) {
                if (parameterList[i].getType() == MultipartFile.class) {
                    params[i] = file;
                }
                if (parameterList[i].getType() == InputStream.class) {
                    params[i] = file.getInputStream();
                }
                if (parameterList[i].getType() == Byte[].class) {
                    params[i] = file.getBytes();
                }
            }
        }
        try {
            return actionMethod.invoke(bean, params);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof FastballRuntimeException) {
                throw (FastballRuntimeException) e.getTargetException();
            }
            throw new BusinessException(e.getTargetException());
        }
    }
}
