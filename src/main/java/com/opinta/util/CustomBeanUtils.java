package com.opinta.util;

import java.lang.reflect.InvocationTargetException;

public class CustomBeanUtils {
    
    private static final EnhancedBeanUtilsBean ENHANCED_BEAN_UTILS_BEAN = new EnhancedBeanUtilsBean();
    
    public static void copyNonNullProperties(Object target, Object source)
            throws IllegalAccessException, InvocationTargetException {
        ENHANCED_BEAN_UTILS_BEAN.copyProperties(target, source);
    }
}
