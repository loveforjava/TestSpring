package com.opinta.util;

import java.util.UUID;

import static java.lang.String.format;

public class LogMessageUtil {
    public static String getAllLogEndpoint(Class clazz) {
        return format("Getting all %s", clazz.getSimpleName());
    }

    public static String getAllByFieldLogEndpoint(Class clazz, Class clazzField, Object value) {
        return format("Getting all %s by %s %s", clazz.getSimpleName(), clazzField.getSimpleName(), value);
    }

    public static String getByIdLogEndpoint(Class clazz, Object value) {
        return format("Getting %s %s", clazz.getSimpleName(), value);
    }

    public static String saveLogEndpoint(Class clazz, Object value) {
        return format("Saving %s %s", clazz.getSimpleName(), value);
    }

    public static String updateLogEndpoint(Class clazz, Object value) {
        return format("Updating %s %s", clazz.getSimpleName(), value);
    }

    public static String deleteLogEndpoint(Class clazz, Object value) {
        return format("Deleting %s %s", clazz.getSimpleName(), value);
    }

    public static String getAllOnErrorLogEndpoint(Class clazz) {
        return format("List of %s has not been got!", clazz.getSimpleName());
    }

    public static String getAllOnErrorLogEndpoint(Class clazz, Exception e) {
        return format("List of %s has not been got! %s", clazz.getSimpleName(), e.getMessage());
    }

    public static String getAllByFieldOnErrorLogEndpoint(Class clazz, Class clazzField, Object value, Exception e) {
        return format("%s by %s %s has not been got! %s", clazz.getSimpleName(), clazzField.getSimpleName(), value, e.getMessage());
    }

    public static String getByIdOnErrorLogEndpoint(Class clazz, Object value) {
        return format("%s %s not found!", clazz.getSimpleName(), value);
    }

    public static String getByIdOnErrorLogEndpoint(Class clazz, Object value, Exception e) {
        return format("%s %s not found! %s", clazz.getSimpleName(), value, e.getMessage());
    }

    public static String saveOnErrorLogEndpoint(Class clazz, Object value) {
        return format("%s %s has not been saved!", clazz.getSimpleName(), value);
    }

    public static String saveOnErrorLogEndpoint(Class clazz, Object value, Exception e) {
        return format("%s %s has not been saved! %s", clazz.getSimpleName(), value, e.getMessage());
    }

    public static String updateOnErrorLogEndpoint(Class clazz, Object value) {
        return format("%s %s has not been updated!", clazz.getSimpleName(), value);
    }

    public static String updateOnErrorLogEndpoint(Class clazz, Object value, Exception e) {
        return format("%s %s has not been updated! %s", clazz.getSimpleName(), value, e.getMessage());
    }

    public static String deleteOnErrorLogEndpoint(Class clazz, Object value) {
        return format("%s %s has not been deleted!", clazz.getSimpleName(), value);
    }

    public static String deleteOnErrorLogEndpoint(Class clazz, Object value, Exception e) {
        return format("%s %s has not been deleted! %s!", clazz.getSimpleName(), value, e);
    }

    public static String copyPropertiesOnErrorLogEndpoint(Class clazz, Object from, Object to, Exception e) {
        return format("Can't copy properties from %s to %s for %s! %s", from, to, clazz.getSimpleName(), e.getMessage());
    }

    public static String authenticationOnErrorLogEndpoint(UUID token) {
        return format("Can't authenticate user with token %s!", token);
    }

    public static String authenticationOnErrorLogEndpoint(UUID token, Exception e) {
        return format("Can't authenticate user with token %s! %s", token, e.getMessage());
    }

    public static String authorizationOnErrorLogEndpoint(UUID token, Object value) {
        return format("You are not authorized to perform this action (token: %s)! Object: %s", token, value);
    }

    public static String generatePdfFormOnErrorLogEndpoint(Class clazz, Object value, Exception e) {
        return format("Can't generate PDF form for %s %s! %s!", clazz.getSimpleName(), value, e);
    }
}
