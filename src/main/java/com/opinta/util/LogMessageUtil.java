package com.opinta.util;

import static java.lang.String.format;

public class LogMessageUtil {
    public static String getAllLogEndpoint(Class clazz) {
        return format("Getting all %s", clazz.getName());
    }

    public static String getAllByIdLogEndpoint(Class clazz, Object value) {
        return format("Getting %s %s", clazz.getName(), value);
    }

    public static String saveLogEndpoint(Class clazz, Object value) {
        return format("Saving %s %s", clazz.getName(), value);
    }

    public static String updateLogEndpoint(Class clazz, Object value) {
        return format("Updating %s %s", clazz.getName(), value);
    }

    public static String deleteLogEndpoint(Class clazz, Object value) {
        return format("Deleting %s %s", clazz.getName(), value);
    }

    public static String getOnErrorLogEndpoint(Class clazz, Object value) {
        return format("%s %s not found!", clazz.getName(), value);
    }

    public static String saveOnErrorLogEndpoint(Class clazz, Object value) {
        return format("%s %s has not been saved!", clazz.getName(), value);
    }

    public static String updateOnErrorLogEndpoint(Class clazz, Object value) {
        return format("%s %s has not been updated!", clazz.getName(), value);
    }

    public static String deleteOnErrorLogEndpoint(Class clazz, Object value) {
        return format("%s %s has not been deleted!", clazz.getName(), value);
    }
}
