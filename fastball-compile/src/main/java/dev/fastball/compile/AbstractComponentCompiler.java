package dev.fastball.compile;

import dev.fastball.compile.exception.CompilerException;
import dev.fastball.compile.utils.ElementCompileUtils;
import dev.fastball.core.annotation.*;
import dev.fastball.core.component.Component;
import dev.fastball.core.component.DownloadFile;
import dev.fastball.meta.action.ActionInfo;
import dev.fastball.meta.action.ApiActionInfo;
import dev.fastball.meta.action.PopupActionInfo;
import dev.fastball.meta.action.PrintActionInfo;
import dev.fastball.meta.basic.PopupInfo;
import dev.fastball.meta.basic.RefComponentInfo;
import dev.fastball.meta.component.ComponentInfo;
import dev.fastball.meta.component.ComponentInfo_AutoValue;
import dev.fastball.meta.component.ComponentProps;
import org.springframework.util.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
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
        componentInfo.componentTitle(ElementCompileUtils.getComponentTitle(compileContext.getComponentElement()));
        componentInfo.componentName(getComponentName());
        componentInfo.propsClassName(props.getClass().getCanonicalName());
        return componentInfo;
    }

    protected List<? extends TypeMirror> getGenericTypes(CompileContext compileContext) {
        DeclaredType declaredType = ElementCompileUtils.getDeclaredInterface(basicComponentClass, compileContext.getComponentElement(), compileContext.getProcessingEnv());
        if (declaredType == null) {
            return Collections.emptyList();
        }
        return declaredType.getTypeArguments();
    }

    protected List<TypeElement> getGenericTypeElements(CompileContext compileContext) {
        return getGenericTypes(compileContext).stream().map(type -> (TypeElement) compileContext.getProcessingEnv().getTypeUtils().asElement(type)).collect(Collectors.toList());
    }

    @Override
    public boolean support(CompileContext componentContext) {
        return ElementCompileUtils.isAssignableFrom(basicComponentClass, componentContext);
    }

    protected Class<T> getBasicComponentClass() {
        return getGenericClass(this.getClass(), ComponentCompiler.class, 0);
    }

    @Override
    public Class<P> getComponentPropsClass() {
        return getGenericClass(this.getClass(), ComponentCompiler.class, 1);
    }

    private <C> Class<C> getGenericClass(Class<?> clazz, Class<?> targetClass, int index) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        while (genericSuperclass != null) {
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                if (parameterizedType.getRawType() instanceof Class && targetClass.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                    if(index < parameterizedType.getActualTypeArguments().length) {
                        Type componentType = parameterizedType.getActualTypeArguments()[index];
                        if (componentType instanceof Class) {
                            return (Class<C>) componentType;
                        } else if (componentType instanceof ParameterizedType && ((ParameterizedType) componentType).getRawType() instanceof Class) {
                            return (Class<C>) ((ParameterizedType) componentType).getRawType();
                        }
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
        List<ActionInfo> actionInfoList = compileContext.getMethodMap().values().stream().map(method -> buildApiActionInfo(props.componentKey(), method, compileContext.getProcessingEnv())).filter(Objects::nonNull).collect(Collectors.toList());
        props.actions(actionInfoList);
        compileExtensionViewActions(props, compileContext.getComponentElement(), compileContext.getProcessingEnv(), actionInfoList, false);
        actionInfoList.sort(ActionInfo::compareTo);
    }

    protected void compileRecordActions(P props, CompileContext compileContext) {
        List<ActionInfo> actionInfoList = compileContext.getMethodMap().values().stream().map(method -> buildApiRecordActionInfo(props.componentKey(), method, compileContext.getProcessingEnv())).filter(Objects::nonNull).collect(Collectors.toList());
        props.recordActions(actionInfoList);
        compileExtensionViewActions(props, compileContext.getComponentElement(), compileContext.getProcessingEnv(), actionInfoList, true);
        actionInfoList.sort(ActionInfo::compareTo);
    }

    protected void compileExtensionViewActions(P props, TypeElement element, ProcessingEnvironment processingEnv, List<ActionInfo> actionInfoList, boolean recordAction) {
        ViewActions viewActions = element.getAnnotation(ViewActions.class);
        if (viewActions != null && !viewActions.override()) {
            TypeMirror superclass = element.getSuperclass();
            if (superclass != null && superclass.getKind() == TypeKind.DECLARED) {
                Element superElement = processingEnv.getTypeUtils().asElement(superclass);
                if (superElement.getKind() == ElementKind.CLASS) {
                    compileExtensionViewActions(props, (TypeElement) superElement, processingEnv, actionInfoList, recordAction);
                }
            }
        }
        ViewAction[] actions = null;
        if (recordAction) {
            if (viewActions != null && viewActions.recordActions().length > 0) {
                actions = viewActions.recordActions();
                // FIXME 兼容老 API
            } else if (element.getAnnotation(RecordViewActions.class) != null) {
                actions = element.getAnnotation(RecordViewActions.class).value();
            }
        } else if (viewActions != null) {
            if (viewActions.actions().length > 0) {
                actions = viewActions.actions();
            } else {
                // FIXME 兼容老 API
                actions = viewActions.value();
            }
        }
        if (actions == null) {
            return;
        }
        for (ViewAction action : actions) {
            ActionInfo actionInfo = buildViewActionInfo(action, props);
            actionInfoList.add(actionInfo);
        }
    }

    protected ActionInfo buildApiRecordActionInfo(String componentKey, ExecutableElement method, ProcessingEnvironment processingEnv) {
        RecordAction actionAnnotation = method.getAnnotation(RecordAction.class);
        if (actionAnnotation == null) {
            return null;
        }

        ApiActionInfo.ApiActionInfoBuilder builder = ApiActionInfo.builder()
                .componentKey(componentKey)
                .order(actionAnnotation.order())
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

    protected ActionInfo buildApiActionInfo(String componentKey, ExecutableElement method, ProcessingEnvironment processingEnv) {
        Action actionAnnotation = method.getAnnotation(Action.class);
        if (actionAnnotation == null) {
            return null;
        }

        ApiActionInfo.ApiActionInfoBuilder builder = ApiActionInfo.builder()
                .componentKey(componentKey)
                .order(actionAnnotation.order())
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

    protected ActionInfo buildViewActionInfo(ViewAction viewAction, P props) {
        ActionInfo actionInfo;
        switch (viewAction.type()) {
            case Popup:
                Popup popup = viewAction.popup();
                PopupInfo popupInfo = ElementCompileUtils.getPopupInfo(props, popup);
                if (popupInfo == null) {
                    throw new CompilerException("@ViewAction(type=Popup) but @ViewAction.popup.value not config.");
                }
                if (!StringUtils.hasLength(popupInfo.getTitle())) {
                    popupInfo.setTitle(viewAction.name());
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
        actionInfo.setOrder(viewAction.order());
        actionInfo.setRefresh(viewAction.refresh());
        actionInfo.setClosePopupOnSuccess(viewAction.closePopupOnSuccess());
        return actionInfo;
    }

    protected boolean isUploadField(TypeMirror type, ProcessingEnvironment processingEnv) {
        if (type.getKind() != TypeKind.DECLARED) {
            return false;
        }
        String typeName = ((TypeElement) ((DeclaredType) type).asElement()).getQualifiedName().toString();
        return typeName.equals(InputStream.class.getCanonicalName()) || typeName.equals("org.springframework.web.multipart.MultipartFile");
    }

}
