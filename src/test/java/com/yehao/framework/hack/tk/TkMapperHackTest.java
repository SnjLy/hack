package com.yehao.framework.hack.tk;

import org.apache.ibatis.plugin.Interceptor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.mapperhelper.MapperInterceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.doThrow;

/**
 * <P> test class methods keys
 * every testCase need follow next rules:
 * 1、begin check ：self check
 * 2、 post   : do test method
 * 3、check again : check result
 * </P>
 * <p>
 * <p>
 * 1、TestCase don't need other complex logic
 * 2、Declare the methods' test scope
 * 3、The code annotation style is unified
 * </p>
 * Created by LiuYong on 2017/8/25.
 */
public class TkMapperHackTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TkMapperHackTest.class);

    /**
     * TestCase1: not the bean we won't to deal, it should return the bean no change
     */
    @Test
    public void testBeanPostProcessorNotTheBean() {
        Object bean = new Object();
        try {
            bean.getClass().getDeclaredField("mapperHelper");

            //Doomed to failure should judge fail, like next: Assert.fail()
            Assert.fail("bean self check ok");
        } catch (Exception e) {
            Assert.assertEquals(NoSuchFieldException.class, e.getClass());
        }
        try {
            //call the method to deal this bean
            new TkMapperInterceptorHacker().postProcessAfterInitialization(bean, "mapperInterceptor");

            bean.getClass().getDeclaredField("mapperHelper");
            Assert.fail("bean check after call ok");
        } catch (Exception e) {
            Assert.assertEquals(NoSuchFieldException.class, e.getClass());
        }

    }


    /**
     * TestCase2: test the bean which we want to deal by hacker
     */
    @Test
    public void testBeanPostProcessor() {
        MapperInterceptor bean = new MapperInterceptor();
        try {
            Field mapperHelperFiled = bean.getClass().getDeclaredField("mapperHelper");
            mapperHelperFiled.setAccessible(true);
            Field msIdSkipField = mapperHelperFiled.get(bean).getClass().getDeclaredField("msIdSkip");
            msIdSkipField.setAccessible(true);
            Assert.assertEquals(HashMap.class, msIdSkipField.get(mapperHelperFiled.get(bean)).getClass());
            LOGGER.info("bean self ckeck ok, fields class is we want");
        } catch (Exception e) {
            Assert.fail("bean self check error" + e);
        }

        new TkMapperInterceptorHacker().postProcessAfterInitialization(bean, "mapperInterceptor");
        try {
            Field mapperHelperFiled = bean.getClass().getDeclaredField("mapperHelper");
            mapperHelperFiled.setAccessible(true);
            Field msIdSkipField = mapperHelperFiled.get(bean).getClass().getDeclaredField("msIdSkip");
            msIdSkipField.setAccessible(true);
            Assert.assertEquals(ConcurrentHashMap.class, msIdSkipField.get(mapperHelperFiled.get(bean)).getClass());
            LOGGER.info("bean ckeck again ok, fields class is we want after post deal");
        } catch (Exception e) {
            Assert.fail("bean self check error" + e);
        }

    }


    /**
     * TestCase3: test proxy class
     */
    @Test
    public void testOtherDealBeanPost() {
        //JDKDynamic proxy
        Interceptor interceptor = new MapperInterceptor();
        DynamicProxyInterceptor handler = new DynamicProxyInterceptor(interceptor);
        Interceptor jdkProxytor = (Interceptor) Proxy.newProxyInstance(interceptor.getClass().getClassLoader(),
                interceptor.getClass().getInterfaces(), handler);
        //cglib proxy
        MapperInterceptor mapper = new MapperInterceptor();
        CglibProxyInterceptor cglibProxyInterceptor = new CglibProxyInterceptor();
        MapperInterceptor cglibProxytor = (MapperInterceptor) cglibProxyInterceptor.createProxy(mapper);


        //cglib proxy check first
        try {
            exceClassBefore(cglibProxytor, cglibProxytor.getClass());
        } catch (Exception e) {
            Assert.fail("bean self check error" + e);
        }

        //cglib proxy check again
        new TkMapperInterceptorHacker().postProcessAfterInitialization(cglibProxytor, "mapperInterceptor");
        try {
            exceClassAfter(cglibProxytor, cglibProxytor.getClass());
            System.out.println(cglibProxytor);
        } catch (Exception e) {
            Assert.fail("bean self check error" + e);
        }

        TkMapperInterceptorHacker hacker = Mockito.spy(TkMapperInterceptorHacker.class);
        doThrow(new RuntimeException("error")).when(hacker).postProcessAfterInitialization(cglibProxytor, "");
        try {
            hacker.postProcessAfterInitialization(cglibProxytor, "");
            //Doomed to failure should judge fail, like next: Assert.fail()
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("error", e.getMessage());
            LOGGER.info("bean check again throw exception ok " + e.getClass());
        }

    }

    private void exceClassBefore(Object bean, Class clazz) throws Exception {
        if (null == clazz || clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            if ("mapperHelper".equals(field.getName())) {
                field.setAccessible(true);
                Field msIdSkipField = field.get(bean).getClass().getDeclaredField("msIdSkip");
                msIdSkipField.setAccessible(true);
                Assert.assertEquals(HashMap.class.getSimpleName(), msIdSkipField.get(field.get(bean)).getClass().getSimpleName());
                break;
            }
        }
        exceClassBefore(bean, clazz.getSuperclass());
    }

    private void exceClassAfter(Object bean, Class clazz) throws Exception {
        if (null == clazz || clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            if ("mapperHelper".equals(field.getName())) {
                field.setAccessible(true);
                Field msIdSkipField = field.get(bean).getClass().getDeclaredField("msIdSkip");
                msIdSkipField.setAccessible(true);
                Assert.assertEquals(ConcurrentHashMap.class.getSimpleName(), msIdSkipField.get(field.get(bean)).getClass().getSimpleName());
                break;
            }
        }
        exceClassAfter(bean, clazz.getSuperclass());
    }


}
