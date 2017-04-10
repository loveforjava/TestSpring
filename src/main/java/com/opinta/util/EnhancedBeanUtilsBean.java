package com.opinta.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;

public class EnhancedBeanUtilsBean extends BeanUtilsBean {
    
    private static final EnhancedBeanUtilsBean ENHANCED_BEAN_UTILS_BEAN = new EnhancedBeanUtilsBean();
    
    public static void copyNotNullProperties(Object target, Object source)
            throws IllegalAccessException, InvocationTargetException {
        ENHANCED_BEAN_UTILS_BEAN.copyProperties(target, source);
    }
    
    @Override
    public void copyProperty(final Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if (value == null) {
            return;
        } else {
            super.copyProperty(bean, name, value);
        }
    }
}
