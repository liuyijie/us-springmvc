package us.mvc.demo.controller;

import us.framework.web.servlet.USModelAndView;
import us.framework.web.servlet.annotation.USAutowired;
import us.framework.web.servlet.annotation.USController;
import us.framework.web.servlet.annotation.USReqMapping;
import us.framework.web.servlet.annotation.USReqParam;
import us.mvc.demo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@USController
@USReqMapping("test")
public class UserController {

    private String test;

    @USAutowired
    private UserService userService;

    @USReqMapping("test")
    public USModelAndView test(HttpServletRequest req, HttpServletResponse resp,
                        @USReqParam("a") String name
                        ){
        USModelAndView mode = new USModelAndView("test.pj");
        mode.put("name", userService.getValue(name));
        mode.put("id", 10);
        return mode;
    }
}
