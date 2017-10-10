package com.yehao.framework.hack.tk;


import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

import java.lang.reflect.Method;

/**
 * <p>cglib proxy</p>
 * Created by admin on 2017/9/8.
 */
public class CglibProxyInterceptor implements MethodInterceptor {

    private Object object;

    public Object createProxy(Object target){
        this.object = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.object.getClass());
        enhancer.setCallback(this);
        enhancer.setClassLoader(target.getClass().getClassLoader());
        return enhancer.create();
    }

    @Override
    public Object intercept(final Object o, final Method method, final Object[] objects, final MethodProxy methodProxy) throws Throwable {
        Object result;
        //do before
        doBefore();
        //do original method
        result = methodProxy.invokeSuper(o, objects);
        //do after
        doAfter();
        return result;
    }

    private void doBefore() {
    }

    private void doAfter() {
    }

    public static void main(String[] args) {
        MapperHelper mapperHelper = new MapperHelper();
        CglibProxyInterceptor cglibProxyInterceptor = new CglibProxyInterceptor();
        MapperHelper mhelper = (MapperHelper) cglibProxyInterceptor.createProxy(mapperHelper);
        mhelper.initMapper();
        System.out.println(mhelper.getClass().getName());
    }
}
