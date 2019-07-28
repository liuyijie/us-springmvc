package us.mvc.demo.service;

import us.framework.web.servlet.annotation.USController;
import us.framework.web.servlet.annotation.USService;

@USService
public class UserService {

    public int getId(){
        return 1;
    }
    public String getValue(String v){
        return v;
    }
}
