package us.framework.web.servlet;

import java.lang.reflect.Method;

public class USHander {
    private Object controller;
    private String url;

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    private Method method;
    public USHander(String url, Method method, Object controller){
        this.url = url;
        this.method = method;
        this.controller = controller;
    }

}
