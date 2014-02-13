package hzg.wpn.util.beanutils;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 03.02.14
 */
public class BeanUtilsHelper {
    private BeanUtilsHelper(){}

    /**
     * Wraps commons-beanutils.BeanUtils#getProperty
     *
     * @param bean bean's instance
     * @param name name of the property
     * @param toType type of the property
     * @param <T>
     * @param <V>
     * @return property's value casted to the type
     * @throws RuntimeException
     */
    public static <T, V extends DynaBean> T getProperty(V bean, String name, Class<T> toType){
        try {
            return toType.cast(PropertyUtils.getProperty(bean, name));
        } catch (IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
