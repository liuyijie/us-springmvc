package us.framework.web.servlet;

import lombok.Data;

import java.util.LinkedHashMap;

@Data
public class USModelAndView {

	/**
	 * View instance or view name String.
	 */
	private Object view;

	/**
	 * Model Map.
	 */
	private LinkedHashMap<String, Object> model = new LinkedHashMap<>();

	/**
	 * Optional HTTP status for the response.
	 */
	private int status = 200;

	public USModelAndView(){
	}

	public void put(String key, Object val){
		model.put(key, val);
	}

	public USModelAndView(Object view){
		this.view = view;
	}

	public USModelAndView(Object view, LinkedHashMap<String, Object> model){
		this.view = view;
		this.model = model;
	}

	public USModelAndView(Object view, LinkedHashMap<String, Object> model, int status){
		this.view = view;
		this.model = model;
		this.status = status;
	}
}