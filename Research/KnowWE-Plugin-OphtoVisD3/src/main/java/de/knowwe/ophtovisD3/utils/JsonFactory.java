package de.knowwe.ophtovisD3.utils;

import com.google.gson.Gson;


public class JsonFactory {
	
	
	
	public static String toJSON(Object toJSON){
		Gson gson = new Gson();
		return gson.toJson(toJSON);
	}

}
