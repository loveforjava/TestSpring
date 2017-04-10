package com.opinta.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;

public class EnhancedBeanUtilsBean extends BeanUtilsBean {
    
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
