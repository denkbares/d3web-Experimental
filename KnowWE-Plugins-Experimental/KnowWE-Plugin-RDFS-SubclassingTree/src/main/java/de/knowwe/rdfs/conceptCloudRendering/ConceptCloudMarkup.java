/*
 * Copyright (C) 2012 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.rdfs.conceptCloudRendering;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 21.06.2012
 */
public class ConceptCloudMarkup extends DefaultMarkupType {

	public static final String RELATION_KEY = "relation";
	public static final String CONCEPT_KEY = "concept";

	public ConceptCloudMarkup(DefaultMarkup markup) {
		super(markup);
	}

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("conceptcloud");
		m.addAnnotation(RELATION_KEY, false);
		m.addAnnotation(CONCEPT_KEY, false);
	}

	public ConceptCloudMarkup() {
		super(m);
		this.setRenderer(new CloudRenderer());
	}

}
