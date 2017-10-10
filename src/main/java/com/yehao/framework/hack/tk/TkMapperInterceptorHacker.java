package com.yehao.framework.hack.tk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>An instance of this class is used to fix tk.mybatis.mapper.mapperhelper.
 * MapperInterceptor.mapperHelper.msIdSkip(HashMap<String, boolean> threads not threadSafe)
 * through reflect to change msIdSkip.HashMap to msIdSkip.ConcurrentHashMap for threadSafe </p>
 * <p>
 * <p>The purpose of this class is designed to fix the thread unsafe problems through the way of hack,
 * framework to modify the source code for as little as possible, and at the same time as little as possible
 * to modify the project configuration, better use of the open-closed principle, reduce dependence on tk</p>
 * <p>
 * <p>if you want to use this function, you need to add an bean instance of this class at your
 * spring.xml,{@link <bean class="TkMapperInterceptorHacker" /> }
 * and you don't need to dependency tk.mybatis </p>
 * <p>
 * <p>This class don't fit for {@code JDKDynamic} proxy class, if class is the agent of JDKDynamic, this class won't make effects</p>
 *
 * @author liuyong
 *         2017-08-28
 */
class TkMapperInterceptorHacker extends InstantiationAwareBeanPostProcessorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TkMapperInterceptorHacker.class);


    /**
     * interface method is invoked after the bean is instantiated
     *
     * @param bean
     * @param beanName
     * @return
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        String beanClassName = bean.getClass().getName();
        if (!StringUtils.isEmpty(beanClassName) && beanClassName.contains("tk.mybatis.mapper.mapperhelper.MapperInterceptor")) {
            try {
                LOGGER.info("TkMapperInterceptorHacker invoke postProcessAfterInitialization method reload bean:" + beanName);


                exceClass(bean, bean.getClass());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return super.postProcessAfterInitialization(bean, beanName);
    }


    private void exceClass(Object bean, Class clazz) throws Exception {
        if (null == clazz || clazz == Object.class) {
            return;
        }
        boolean isMatch = false;
        Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            if ("mapperHelper".equals(field.getName())) {
                field.setAccessible(true);
                Field msIdSkipField = field.get(bean).getClass().getDeclaredField("msIdSkip");
                msIdSkipField.setAccessible(true);
                msIdSkipField.set(field.get(bean), new ConcurrentHashMap<>());
                isMatch = true;
                break;
            }
        }
        if (!isMatch) {
            exceClass(bean, clazz.getSuperclass());
        }
    }
}