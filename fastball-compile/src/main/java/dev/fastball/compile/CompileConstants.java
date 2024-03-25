package dev.fastball.compile;

public interface CompileConstants {
    String SELF_PACKAGE = "@";
    String COMPONENT_IMPORT_PREFIX = "_f_b_Component___";

    String SIMPLE_FORM_LIST_VALUE_FIELD = "value";

    String FRONTEND_MAIN_CLASS_PACKAGE = "dev.fastball.frontend";
    String FRONTEND_MAIN_CLASS_NAME = "MainClass";
    String FRONTEND_MAIN_CLASS_FULL_NAME = FRONTEND_MAIN_CLASS_PACKAGE + "." + FRONTEND_MAIN_CLASS_NAME;
    String FRONTEND_MAIN_METHOD_NAME = "main";
    String FRONTEND_MAIN_METHOD_ARG_NAME = "args";
    String FRONTEND_REGISTER_METHOD_NAME = "registerFunction";
    String FRONTEND_REGISTER_FUNCTION_BODY = "main[_methodName] = _method;";
    String FRONTEND_REGISTER_PARAM_NAME_1 = "_method";
    String FRONTEND_REGISTER_PARAM_NAME_2 = "_methodName";
}
