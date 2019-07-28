package us.framework.web.servlet;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import us.framework.web.servlet.annotation.USReqParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

@Data
public class USHandlerAdapter {

    private USHander usHander;
    private ParamInfo[] paramInfos ;

    public USHandlerAdapter(USHander usHander) {
        this.usHander = usHander;
        getMethodArgumentValues();
    }

    protected void getMethodArgumentValues(){
        Method method = usHander.getMethod();
        paramInfos = new ParamInfo[method.getParameterCount()];
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(USReqParam.class)){
                paramInfos[i] = new ParamInfo(i, parameter.getType(), parameter.getAnnotation(USReqParam.class).value());
            } else {
                paramInfos[i] = new ParamInfo(i, parameter.getType(), parameter.getName());
            }
        }
    }


    USModelAndView handle(HttpServletRequest request, HttpServletResponse response, USHander handler) {
        Object[] args = new Object[paramInfos.length];
        Object val = null;
        for (int i = 0; i < paramInfos.length; i++) {
            ParamInfo paramInfo = paramInfos[i];
            if (paramInfo.getPtype() == HttpServletRequest.class){
                val = request;
            } else if (paramInfo.getPtype() == HttpServletResponse.class){
                val = response;
            } else {
                val = request.getParameter(paramInfo.getName());
            }
            args[paramInfo.getIdx()] = val;
        }

        try {
            Object res = handler.getMethod().invoke(handler.getController(), args);
            boolean isModelAndView = handler.getMethod().getReturnType() == USModelAndView.class;
            if(isModelAndView){
                return (USModelAndView) res;
            }else{
                return null;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        return null;
    }

}
