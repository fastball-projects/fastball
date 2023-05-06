package dev.fastball.compile;

import dev.fastball.compile.exception.CompilerException;
import dev.fastball.compile.utils.ElementCompileUtils;
import dev.fastball.core.annotation.*;
import dev.fastball.core.component.Component;
import dev.fastball.core.component.DownloadFile;
import dev.fastball.core.info.action.ActionInfo;
import dev.fastball.core.info.action.ApiActionInfo;
import dev.fastball.core.info.action.PopupActionInfo;
import dev.fastball.core.info.action.PrintActionInfo;
import dev.fastball.core.info.basic.PopupInfo;
import dev.fastball.core.info.basic.RefComponentInfo;
import dev.fastball.core.info.component.ComponentInfo;
import dev.fastball.core.info.component.ComponentInfo_AutoValue;
import dev.fastball.core.info.component.ComponentProps;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public abstract class AbstractComponentCompiler<T extends Component, P extends ComponentProps> implements ComponentCompiler<P> {

    private final Class<T> basicComponentClass = getBasicComponentClass();

    protected abstract P buildProps(CompileContext compileContext);

    protected abstract String getComponentName();

    protected void compileProps(P props, CompileContext compileContext) {
    }

    @Override
    public ComponentInfo<P> compile(CompileContext compileContext) {
        ComponentInfo_AutoValue<P> componentInfo = new ComponentInfo_AutoValue<>();

        P props = buildProps(compileContext);
        props.componentKey(ElementCompileUtils.getComponentKey(compileContext.getComponentElement()));
        compileActions(props, compileContext);
        compileRecordActions(props, compileContext);
        compileProps(props, compileContext);
        componentInfo.props(props);
        componentInfo.material(compileContext.getMaterialRegistry().getMaterial(this.getClass()));
        componentInfo.className(compileContext.getComponentElement().getQualifiedName().toString());
        componentInfo.componentKey(props.componentKey());
        componentInfo.componentPath(ElementCompileUtils.getComponentPath(compileContext.getComponentElement()));
        componentInfo.componentName(getComponentName());
        return componentInfo;
    }

    protected List<? extends TypeMirror> getGenericTypes(CompileContext compileContext) {
        DeclaredType declaredType = ElementCompileUtils.getDeclaredInterface(basicComponentClass, compileContext.getComponentElement());
        if (declaredType == null) {
            return Collections.emptyList();
        }
        return declaredType.getTypeArguments();
    }

    protected List<TypeElement> getGenericTypeElements(CompileContext compileContext) {
        return getGenericTypes(compileContext).stream()
                .map(type -> (TypeElement) compileContext.getProcessingEnv().getTypeUtils().asElement(type))
                .collect(Collectors.toList());
    }

    @Override
    public boolean support(CompileContext componentContext) {
        return ElementCompileUtils.isAssignableFrom(basicComponentClass, componentContext);
    }

    protected Class<T> getBasicComponentClass() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        while (genericSuperclass != null) {
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                if (parameterizedType.getRawType() instanceof Class
                        && ComponentCompiler.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())
                ) {
                    Type componentType = parameterizedType.getActualTypeArguments()[0];
                    if (componentType instanceof Class) {
                        return (Class<T>) componentType;
                    } else if (componentType instanceof ParameterizedType && ((ParameterizedType) componentType).getRawType() instanceof Class) {
                        return (Class<T>) ((ParameterizedType) componentType).getRawType();
                    }
                }
                genericSuperclass = parameterizedType.getRawType();
            } else if (genericSuperclass instanceof Class) {
                genericSuperclass = ((Class<?>) genericSuperclass).getGenericSuperclass();
            }
        }
        throw new CompilerException("can't happened");
    }

    private void compileActions(P props, CompileContext compileContext) {
        List<ActionInfo> actionInfoList = compileContext.getMethodMap().values().stream()
                .map(method -> buildApiActionInfo(method, compileContext.getProcessingEnv())).filter(Objects::nonNull).collect(Collectors.toList());
        props.actions(actionInfoList);
        ViewActions viewActions = compileContext.getComponentElement().getAnnotation(ViewActions.class);
        if (viewActions == null) {
            return;
        }
        ViewAction[] actions = viewActions.actions().length > 0 ? viewActions.actions() : viewActions.value();
        for (ViewAction action : actions) {
            ActionInfo viewActionInfo = buildViewActionInfo(action, props);
            actionInfoList.add(viewActionInfo);
        }
    }

    protected void compileRecordActions(P props, CompileContext compileContext) {
        List<ActionInfo> recordActions = compileContext.getMethodMap().values().stream()
                .map(method -> buildApiRecordActionInfo(method, compileContext.getProcessingEnv())).filter(Objects::nonNull).collect(Collectors.toList());
        props.recordActions(recordActions);
        RecordViewActions recordViewActions = compileContext.getComponentElement().getAnnotation(RecordViewActions.class);
        ViewActions viewActions = compileContext.getComponentElement().getAnnotation(ViewActions.class);
        ViewAction[] actions;
        if (viewActions != null && viewActions.recordActions().length > 0) {
            actions = viewActions.actions();
        } else if (recordViewActions != null) {
            actions = recordViewActions.value();
        } else {
            return;
        }
        for (ViewAction action : actions) {
            ActionInfo actionInfo = buildViewActionInfo(action, props);
            recordActions.add(actionInfo);
        }
    }

    protected ActionInfo buildApiRecordActionInfo(ExecutableElement method, ProcessingEnvironment processingEnv) {
        RecordAction actionAnnotation = method.getAnnotation(RecordAction.class);
        if (actionAnnotation == null) {
            return null;
        }

        ApiActionInfo.ApiActionInfoBuilder builder = ApiActionInfo.builder()
                .refresh(actionAnnotation.refresh())
                .confirmMessage(actionAnnotation.confirmMessage())
                .closePopupOnSuccess(actionAnnotation.closePopupOnSuccess())
                .actionName(actionAnnotation.name())
                .actionKey(actionAnnotation.key().isEmpty() ? method.getSimpleName().toString() : actionAnnotation.key());
        builder.uploadFileAction(method.getParameters().stream().anyMatch(param -> isUploadField(param.asType(), processingEnv)));
        if (method.getReturnType() != null) {
            TypeElement returnType = (TypeElement) processingEnv.getTypeUtils().asElement(method.getReturnType());
            builder.downloadFileAction(returnType != null && ElementCompileUtils.isAssignableFrom(DownloadFile.class, returnType, processingEnv));
        }
        return builder.build();
    }

    protected ActionInfo buildApiActionInfo(ExecutableElement method, ProcessingEnvironment processingEnv) {
        Action actionAnnotation = method.getAnnotation(Action.class);
        if (actionAnnotation == null) {
            return null;
        }

        ApiActionInfo.ApiActionInfoBuilder builder = ApiActionInfo.builder()
                .refresh(actionAnnotation.refresh())
                .closePopupOnSuccess(actionAnnotation.closePopupOnSuccess())
                .actionName(actionAnnotation.name())
                .actionKey(actionAnnotation.key().isEmpty() ? method.getSimpleName().toString() : actionAnnotation.key());
        builder.uploadFileAction(method.getParameters().stream().anyMatch(param -> isUploadField(param.asType(), processingEnv)));
        if (method.getReturnType() != null) {
            TypeElement returnType = (TypeElement) processingEnv.getTypeUtils().asElement(method.getReturnType());
            builder.downloadFileAction(returnType != null && ElementCompileUtils.isAssignableFrom(DownloadFile.class, returnType, processingEnv));
        }
        return builder.build();
    }

    protected ActionInfo buildViewActionInfo(ViewAction viewAction, P props) {
        ActionInfo actionInfo;
        switch (viewAction.type()) {
            case Popup:
                Popup popup = viewAction.popup();
                PopupInfo popupInfo = ElementCompileUtils.getPopupInfo(props, popup);
                if (popupInfo == null) {
                    throw new CompilerException("@ViewAction(type=Popup) but @ViewAction.popup.value not config.");
                }
                actionInfo = PopupActionInfo.builder().popupInfo(popupInfo).closePopupOnSuccess(popup.closePopupOnSuccess()).build();
                break;
            case Link:
                throw new CompilerException("@ViewAction(type=Link) not supported yet");
            case Menu:
                throw new CompilerException("@ViewAction(type=Menu) not supported yet");
            case Print:
                Print print = viewAction.print();
                RefComponentInfo printComponent = ElementCompileUtils.getReferencedComponentInfo(props, print.value());
                actionInfo = PrintActionInfo.builder().printComponent(printComponent).build();
                break;
            default:
                throw new CompilerException("@ViewAction(type=" + viewAction.type() + ") not supported yet");
        }
        actionInfo.setActionName(viewAction.name());
        actionInfo.setActionKey(viewAction.key());
        actionInfo.setRefresh(viewAction.refresh());
        actionInfo.setClosePopupOnSuccess(viewAction.closePopupOnSuccess());
        return actionInfo;
    }

    private boolean isUploadField(TypeMirror type, ProcessingEnvironment processingEnv) {
        if (type.getKind() != TypeKind.DECLARED) {
            return false;
        }
        String typeName = ((TypeElement) ((DeclaredType) type).asElement()).getQualifiedName().toString();
        return typeName.equals(InputStream.class.getCanonicalName()) || typeName.equals("org.springframework.web.multipart.MultipartFile");
    }

}
