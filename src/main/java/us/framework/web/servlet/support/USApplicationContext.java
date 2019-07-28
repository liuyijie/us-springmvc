package us.framework.web.servlet.support;

import us.framework.web.servlet.annotation.USAutowired;
import us.framework.web.servlet.annotation.USController;
import us.framework.web.servlet.annotation.USService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class USApplicationContext {
    private Properties config = new Properties();

    public Properties getConfig() {
        return config;
    }

    public List<String> getClassCache() {
        return classCache;
    }

    public Map<String, Object> getSingetonObjects() {
        return singetonObjects;
    }

    private List<String> classCache = new ArrayList<String>();
    private Map<String, Object> singetonObjects = new ConcurrentHashMap<>();

    public USApplicationContext(String location){
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream(location);
            config.load(is);
            String scanPackage = config.getProperty("scanPackage");
            doRegister(scanPackage);

            doCreateBean();

            populate();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //把符合条件所有的class全部找出来，注册到缓存里面去
    private void doRegister(String packageName){
        String path = "/" + packageName.replaceAll("\\.", "/");
        URL url = this.getClass().getClassLoader().getResource(path);
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            //如果是一个文件夹，继续递归
            if(file.isDirectory()){
                doRegister(packageName + "." + file.getName());
            }else{
                classCache.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    private void doCreateBean(){
        if (classCache.isEmpty()){
            return;
        }
        classCache.forEach(p -> {
            try {
                Class<?> clazz = Class.forName(p);
                if (Arrays.stream(clazz.getAnnotations()).map(Annotation::annotationType)
                        .anyMatch(a -> a == USController.class || a == USAutowired.class || a == USService.class)){
                    singetonObjects.put(toBeanName(clazz), clazz.newInstance());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 依赖注入
     */
    private void populate(){
        for (Map.Entry<String, Object> entry : singetonObjects.entrySet()) {
            //把所有的属性全部取出来，包括私有属性
            Field []  fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if(!field.isAnnotationPresent(USAutowired.class)){ continue; }
                if (!singetonObjects.containsKey(field.getName())){ continue; }
                final Field tet01;
                try {
                    tet01 = entry.getValue().getClass().getDeclaredField(field.getName());
                    tet01.setAccessible(true);
                    tet01.set(entry.getValue(), singetonObjects.get(field.getName()));
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    continue;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private String toBeanName(Class clazz){
        return toBeanName(clazz.getSimpleName());
    }

    private String toBeanName(String name){
        return name.substring(0, 1).toLowerCase()
                + name.substring(1,name.length() );
    }
}
