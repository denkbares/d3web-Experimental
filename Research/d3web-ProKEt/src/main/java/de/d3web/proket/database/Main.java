package de.d3web.proket.database;

import java.util.ResourceBundle;

public class Main {

	static ResourceBundle getBundle() {
		
		ResourceBundle b = ResourceBundle.getBundle("generic");
		String bname = b.getString("props");
		
		ResourceBundle res = ResourceBundle.getBundle(bname);
		return res;
		
	}
	
}
