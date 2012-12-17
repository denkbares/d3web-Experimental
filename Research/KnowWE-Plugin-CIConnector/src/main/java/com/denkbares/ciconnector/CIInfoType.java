/*
 * Copyright (C) 2012 denkbares GmbH, Germany
 */
package com.denkbares.ciconnector;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author Sebastian Furth
 * @created 17.12.2012
 */
public class CIInfoType extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	public static final String MARKUP_NAME = "CIInfo";

	static {
		MARKUP = new DefaultMarkup(MARKUP_NAME);
	}

	public CIInfoType() {
		super(MARKUP);
		this.setIgnorePackageCompile(true);
		this.addSubtreeHandler(new CIInfoHandler());
	}

}
