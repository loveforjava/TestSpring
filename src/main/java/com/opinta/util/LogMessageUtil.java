package com.opinta.util;

import static java.lang.String.format;

public class LogMessageUtil {
    public static String getAllLogEndpoint(Class clazz) {
        return format("Getting all %s", clazz.getName());
    }

    public static String getAllByIdLogEndpoint(Class clazz, Object object) {
        return format("Getting %s by id %s", clazz.getName(), object);
    }
}
