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
package de.knowwe.diaflux.coverage;

import de.d3web.core.session.Session;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.CoverageSessionObject;
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.d3web.diaflux.coverage.PSMDiaFluxCoverage;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWERessourceLoader;
import de.knowwe.core.compile.packaging.KnowWEPackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.diaflux.type.FlowchartType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;


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
		MARKUP.addAnnotation(KnowWEPackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		MARKUP.addAnnotation(ANNOTATION_MASTER, false);
		MARKUP.addAnnotation(ANNOTATION_TEST, true);

	}


	public DiaFluxCoverageType() {
		super(MARKUP);
		KnowWERessourceLoader.getInstance().add("diafluxcoverage.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);

		setCustomRenderer(new DiaFluxCoverageRenderer());
	}

	public static String getMaster(Section<DiaFluxCoverageType> section, String topic) {
		String master = DefaultMarkupType.getAnnotation(section, ANNOTATION_MASTER);
		if (master != null) return master;
		else return topic;
	}

	public static CoverageResult getResult(Section<DiaFluxCoverageType> section, UserContext user) {
		String tests = DefaultMarkupType.getAnnotation(section, DiaFluxCoverageType.ANNOTATION_TEST);
		CoverageResult result;
		if (tests == null) {
			String master = DiaFluxCoverageType.getMaster(section, section.getTitle());
			Session session = D3webUtils.getSession(master, user, user.getWeb());
			CoverageSessionObject coverage = PSMDiaFluxCoverage.getCoverage(session);
			result = DefaultCoverageResult.calculateResult(coverage, session.getKnowledgeBase());
		}
		else {
			result = (CoverageResult) section.getSectionStore().getObject(COVERAGE_RESULT);

		}
		return result;

	}
}
