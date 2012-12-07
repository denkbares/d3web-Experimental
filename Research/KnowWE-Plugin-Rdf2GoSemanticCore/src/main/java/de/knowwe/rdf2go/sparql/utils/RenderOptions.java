package de.knowwe.rdf2go.sparql.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class RenderOptions {

	boolean zebraMode;
	boolean rawOutput;
	boolean sorting;
	boolean navigation;
	boolean border;
	Map sortingOrder;
	String id;

	public RenderOptions(String id) {
		super();
		this.zebraMode = false;
		this.rawOutput = false;
		this.sorting = false;
		this.navigation = false;
		this.border = false;
		sortingOrder = new LinkedHashMap<String, String>();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Map<String, String> getSortingMap() {
		return sortingOrder;
	}

	public void setSortingMap(Map<String, String> json) {
		this.sortingOrder = json;
	}

	public boolean isNavigation() {
		return navigation;
	}

	public void setNavigation(boolean navigation) {
		this.navigation = navigation;
	}

	public boolean isBorder() {
		return border;
	}

	public void setBorder(boolean border) {
		this.border = border;
	}

	public boolean isZebraMode() {
		return zebraMode;
	}

	public void setZebraMode(boolean zebraMode) {
		this.zebraMode = zebraMode;
	}

	public boolean isRawOutput() {
		return rawOutput;
	}

	public void setRawOutput(boolean rawOutput) {
		this.rawOutput = rawOutput;
	}

	public boolean isSorting() {
		return sorting;
	}

	public void setSorting(boolean sorting) {
		this.sorting = sorting;
	}

}
