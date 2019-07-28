package us.framework.web.servlet;

import lombok.Data;

@Data
public class ParamInfo {
    private int idx;
    private Object ptype;
    private String name;

    public ParamInfo(int idx, Object ptype, String name){
        this.idx = idx;
        this.ptype = ptype;
        this.name = name;
    }
}
