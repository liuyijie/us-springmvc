package us.mvc.demo;


import com.alibaba.fastjson.JSON;
import us.framework.web.servlet.ParamInfo;
import us.framework.web.servlet.annotation.USAutowired;
import us.framework.web.servlet.annotation.USReqParam;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class TestMain {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        final Class<?> aclazz = Class.forName("us.mvc.demo.controller.UserController");
        final Object a0 = aclazz.newInstance();
        final Class<?> bclazz = Class.forName("us.mvc.demo.service.UserService");
        final Object us = bclazz.newInstance();
        for (Field field : a0.getClass().getDeclaredFields()) {
            System.out.println(field.isAnnotationPresent(USAutowired.class));
            if (field.isAnnotationPresent(USAutowired.class)){
                final Field tet01 = a0.getClass().getDeclaredField(field.getName());
                tet01.setAccessible(true);
                tet01.set(a0,us);
//
//                final String name = field.getType().getName();
//                field.setAccessible(true);
//                System.out.println(name);
//                System.out.println(field.getName());
//                field.set(field.getName(), us);
//                System.out.println(aclazz);
            }
        }


        final Method[] methods = a0.getClass().getMethods();
        for (Method method : methods) {
            ParamInfo[] paramInfos = new ParamInfo[method.getParameterCount()];
            System.out.println(method +", " +method.getParameterCount());
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < method.getParameterCount(); i++) {
                Parameter parameter = parameters[i];
                if (parameter.isAnnotationPresent(USReqParam.class)){
                    paramInfos[i] = new ParamInfo(i, parameter.getType(), parameter.getAnnotation(USReqParam.class).value());
                } else {
                    paramInfos[i] = new ParamInfo(i, parameter.getType(), parameter.getName());
                }
            }
            for (ParamInfo paramInfo : paramInfos) {

                System.out.println(JSON.toJSONString(paramInfo));
            }
        }

//        final Field testField = a0.getClass().getDeclaredField("test");
//        final Field tet01 = a0.getClass().getDeclaredField("userService");
//        testField.setAccessible(true);
//        tet01.setAccessible(true);
//        tet01.set(a0,us);
//        testField.set(a0,"xxxx");
//        final Method method = a0.getClass().getDeclaredMethod("test", null);
//        System.out.println(method);
//        System.out.println(method.invoke(a0, null));


    }
}
