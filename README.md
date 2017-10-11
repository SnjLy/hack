# hack
我的hack历程

 An instance of this class is used to fix tk.mybatis.mapper.mapperhelper.
 MapperInterceptor.mapperHelper.msIdSkip(HashMap<String, boolean> threads not threadSafe)
 through reflect to change msIdSkip.HashMap to msIdSkip.ConcurrentHashMap for threadSafe </p>

  The purpose of this class is designed to fix the thread unsafe problems through the way of hack,
 framework to modify the source code for as little as possible, and at the same time as little as possible
 to modify the project configuration, better use of the open-closed principle, reduce dependence on tk</p>

 if you want to use this function, you need to add an bean instance of this class at your
 spring.xml,{@link <bean class="TkMapperInterceptorHacker" /> }
 and you don't need to dependency tk.mybatis 

This class don't fit for {@code JDKDynamic} proxy class, if class is the agent of JDKDynamic, 
this class won't make effects.

 @author liuyong
 @date   2017-08-28


1、不支持JDK动态代理类是，支持cglib代理类

2、使用时只需要声明bean，可以将tk.mybatis.mapper.mapperhelper.
 MapperInterceptor.mapperHelper.msIdSkip修改成线程安全
 
 
