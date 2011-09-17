/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.diaflux.coverage;

import de.d3web.core.session.Session;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.PSMDiaFluxCoverage;
import de.d3web.we.core.packaging.KnowWEPackageManager;
import de.d3web.we.flow.type.FlowchartType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;


/**
 * 
 * @author Reinhard Hatko
 * @created 05.08.2011
 */
public class DiaFluxCoverageType extends DefaultMarkupType {

	public static final String COVERAGE_RESULT = "coverageResult";
	public static final String ANNOTATION_TEST = "test";
	private static final String ANNOTATION_MASTER = "master";
	private static final DefaultMarkup MARKUP;

	static {

		MARKUP = new DefaultMarkup("DiaFluxCoverage");
		MARKUP.addContentType(new FlowchartType());
		MARKUP.addAnnotation(KnowWEPackageManager.ATTRIBUTE_NAME, false);
		MARKUP.addAnnotation(ANNOTATION_MASTER, false);
		MARKUP.addAnnotation(ANNOTATION_TEST, true);

	}


	public DiaFluxCoverageType() {
		super(MARKUP);
		setCustomRenderer(new DiaFluxCoverageRenderer());
	}

	public static String getMaster(Section<DiaFluxCoverageType> section, String topic) {
		String master = DefaultMarkupType.getAnnotation(section, ANNOTATION_MASTER);
		if (master != null) return master;
		else return topic;
	}

	public static CoverageResult getResult(Section<DiaFluxCoverageType> section, Session session) {
		String tests = DefaultMarkupType.getAnnotation(section, ANNOTATION_TEST);
		if (tests == null) {
			return CoverageResult.calculateResult(PSMDiaFluxCoverage.getCoverage(session),
					session.getKnowledgeBase());
		}
		else {
			return (CoverageResult) section.getSectionStore().getObject(COVERAGE_RESULT);
		}

	}
}
