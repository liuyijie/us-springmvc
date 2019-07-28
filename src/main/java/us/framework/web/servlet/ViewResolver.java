package us.framework.web.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewResolver{
    private static Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}",Pattern.CASE_INSENSITIVE);

    private String viewName;
    private File file;

    protected ViewResolver(String viewName,File file){
        this.viewName = viewName;
        this.file = file;
    }

    protected String parse(USModelAndView mv) throws Exception{

        StringBuffer sb = new StringBuffer();

        RandomAccessFile ra = new RandomAccessFile(this.file, "r");

        try{
            //模板框架的语法是非常复杂，但是，原理是一样的
            //无非都是用正则表达式来处理字符串而已
            //就这么简单，不要认为这个模板框架的语法是有多么的高大上
            //来我现在来做一个最接地气的模板，也就是咕泡学院独创的模板语法
            String line = null;
            while(null != (line = ra.readLine())){
                Matcher m = matcher(line);
                while (m.find()) {
                    for (int i = 1; i <= m.groupCount(); i ++) {
                        String paramName = m.group(i);
                        Object paramValue = mv.getModel().get(paramName);
                        if(null == paramValue){ continue; }
                        line = line.replaceAll("\\$\\{" + paramName + "\\}", paramValue.toString());
                    }
                }

                sb.append(line);
            }
        }finally{
            ra.close();
        }
        return sb.toString();
    }

    private Matcher matcher(String str){
        Matcher m = pattern.matcher(str);
        return m;
    }


    public String getViewName() {
        return viewName;
    }

}