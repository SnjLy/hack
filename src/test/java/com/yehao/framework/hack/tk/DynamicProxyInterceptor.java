package com.yehao.framework.hack.tk;


import org.apache.ibatis.plugin.Interceptor;
import tk.mybatis.mapper.mapperhelper.MapperInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p>JDK动态代理</p>
 * Created by admin on 2017/9/8.
 */
public class DynamicProxyInterceptor implements InvocationHandler {

    private Object object;

    public DynamicProxyInterceptor(final Object mapperInterceptor) {
        super();
        this.object = mapperInterceptor;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Object result = null;
        //调用之前
        doBefore();
        //调用原始对象的方法
        result = method.invoke(object, args);
        //调用之后
        doAfter();
        return result;
    }


    private void doBefore() {
    }

    private void doAfter() {
    }


    public static void main(String[] args) {
        Interceptor interceptor = new MapperInterceptor();
        DynamicProxyInterceptor handler = new DynamicProxyInterceptor(interceptor);
        //创建动态代理对象
        Interceptor mapperInterceptor = (Interceptor) Proxy.newProxyInstance(interceptor.getClass().getClassLoader(),
                interceptor.getClass().getInterfaces(), handler);

        mapperInterceptor.getClass();
    }
}
