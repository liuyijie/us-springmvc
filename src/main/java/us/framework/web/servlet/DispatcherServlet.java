package us.framework.web.servlet;

import us.framework.web.servlet.annotation.USController;
import us.framework.web.servlet.annotation.USReqMapping;
import us.framework.web.servlet.support.USApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DispatcherServlet extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";

    private List<USHander> usHanderMapping = new ArrayList<>();
    private List<USHandlerAdapter> usHandlerAdapter = new ArrayList<>();
    private List<ViewResolver> viewResolvers = new ArrayList<>();

    @Override
    public void init(ServletConfig config) {
        final String location = config.getInitParameter(LOCATION);
        System.out.println("DispatcherServlet init !!!!"  + location);
        USApplicationContext context = new USApplicationContext(location);

        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);
        initHandlerMappings(context);
        initHandlerAdapters(context);
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);
        initViewResolvers(context);
        initFlashMapManager(context);
    }

    private void initFlashMapManager(USApplicationContext context) {

    }

    private void initViewResolvers(USApplicationContext context) {

        final String tempateRoot = context.getConfig().getProperty("templateRoot");

        //归根到底就是一个文件，普通文件
        String rootPath = this.getClass().getClassLoader().getResource(tempateRoot).getFile();

        File rootDir = new File(rootPath);
        for (File template : rootDir.listFiles()) {
            viewResolvers.add(new ViewResolver(template.getName(),template));
        }

    }

    private void initRequestToViewNameTranslator(USApplicationContext context) {

    }

    private void initHandlerExceptionResolvers(USApplicationContext context) {

    }

    private void initHandlerAdapters(USApplicationContext context) {
        if (this.usHanderMapping.isEmpty()) {
            return;
        }
        this.usHandlerAdapter = this.usHanderMapping.stream()
                .map(i ->new USHandlerAdapter(i)).collect(Collectors.toList());
    }

    /**
     * 初始化handlerMapping
     * @param context
     */
    private void initHandlerMappings(USApplicationContext context) {
        this.usHanderMapping = context.getSingetonObjects().values()
                .stream()
                .filter(i -> i.getClass().isAnnotationPresent(USController.class))
                .flatMap(i -> {
                    String urlBase = "";
                    if (i.getClass().isAnnotationPresent(USReqMapping.class)){
                        urlBase = i.getClass().getAnnotation(USReqMapping.class).value();
                    }
                    Method[] methods = i.getClass().getDeclaredMethods();
                    String finalUrlBase = urlBase;
                    return Arrays.stream(methods).filter(k ->
                            k.isAnnotationPresent(USReqMapping.class)
                    ).map(k -> new USHander(
                            (finalUrlBase + "/" + k.getAnnotation(USReqMapping.class).value()).replaceAll("/+", "/"),
                            k, i )
                    );
                })
                .peek(i -> {
                    System.out.println(i.getUrl() +", " + i.getController() + "." + i.getMethod());
                })
                .collect(Collectors.toList());

    }

    private void initThemeResolver(USApplicationContext context) {

    }

    private void initLocaleResolver(USApplicationContext context) {
    }

    private void initMultipartResolver(USApplicationContext context) {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        doDispatch(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        doDispatch(req, resp);
    }

    private USHander getHandler(HttpServletRequest req){
        return this.usHanderMapping.stream()
                .filter(i -> req.getRequestURI().contains(i.getUrl()))
                .findFirst().orElse(null);
    }

    private USHandlerAdapter getHandlerAdapter(USHander usHander){
        return this.usHandlerAdapter.stream().filter(i -> i.getUsHander().equals(usHander)).findFirst().orElse(null);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("--------"+req.getRequestURL()+"-------");
        // Determine handler for the current request.
        USHander usHander = getHandler(req);
        if (usHander == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Determine handler adapter for the current request.
        USHandlerAdapter ha = getHandlerAdapter(usHander);

        if (ha == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Actually invoke the handler.
        USModelAndView mv = ha.handle(req, resp, usHander);
        render(mv, req, resp);
    }

    private void render(USModelAndView mv, HttpServletRequest req, HttpServletResponse resp) {
        if (mv == null){
            try {
                resp.sendError(500, "page not found" );
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        if (mv.getView().getClass() == String.class) {
            final String view = mv.getView().toString();
            final ViewResolver viewResolver = this.viewResolvers.stream().filter(i -> i.getViewName().equals(view)).findFirst().orElse(null);
            if (viewResolver == null) {
                try {
                    resp.sendError(500, "page not found" );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                resp.getWriter().println(viewResolver.parse(mv));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
