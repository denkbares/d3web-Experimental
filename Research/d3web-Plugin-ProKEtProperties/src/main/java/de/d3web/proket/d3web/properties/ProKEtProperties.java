package de.d3web.proket.d3web.properties;

import de.d3web.core.knowledge.terminology.info.Property;

public class ProKEtProperties {

	public static final Property<String> DATE_FORMAT = Property.getProperty("date_format",
			String.class);

	public static final Property<String> TEXT_FORMAT = Property.getProperty("text_format",
			String.class);

	public static final Property<String> POPUP = Property.getProperty("popup", String.class);

	public static final Property<String> GRID = Property.getProperty("grid", String.class);

	public static final Property<Boolean> SHOW_IN_HEADER = Property.getProperty("show_in_header",
			Boolean.class);

	public static final Property<Integer> POSITION_IN_HEADER = Property.getProperty(
			"position_in_header", Integer.class);

	public static final Property<String> HEADER_TEXT = Property.getProperty(
			"header_text", String.class);

	public static final Property<String> IMAGE = Property.getProperty("image", String.class);

	public static final Property<String> IMAGEWIDTH = Property.getProperty("imagewidth",
			String.class);

	public static final Property<String> IMAGEHEIGHT = Property.getProperty("imageheight",
			String.class);

	public static final Property<String> IMAGEMAP = Property.getProperty("imagemap", String.class);

	public static final Property<String> DROPDOWN_MENU_OPTIONS = Property.getProperty(
			"dropdown_menu_options", String.class);

	public static final Property<String> DROPDOWN_MENU_DEFAULT = Property.getProperty(
			"dropdown_menu_default", String.class);

	public static final Property<Boolean> USE_AS_FILENAME = Property.getProperty(
			"use_as_filename", Boolean.class);
}
