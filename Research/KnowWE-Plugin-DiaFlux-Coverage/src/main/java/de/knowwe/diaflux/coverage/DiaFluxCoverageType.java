/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.diaflux.coverage;

import de.d3web.diaflux.coverage.CoverageResult;
import de.knowwe.core.RessourceLoader;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.diaflux.type.FlowchartType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * 
 * @author Reinhard Hatko
 * @created 05.08.2011
 */
public class DiaFluxCoverageType extends DefaultMarkupType {

	public static final String COVERAGE_RESULT = "coverageResult";
	private static final DefaultMarkup MARKUP;

	static {

		MARKUP = new DefaultMarkup("DiaFluxCoverage");
		MARKUP.addContentType(new FlowchartType());
		MARKUP.addAnnotation(PackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		MARKUP.addAnnotationRenderer(PackageManager.PACKAGE_ATTRIBUTE_NAME,
				StyleRenderer.ANNOTATION);

	}

	public DiaFluxCoverageType() {
		super(MARKUP);
		RessourceLoader.getInstance().add("diafluxcoverage.js",
				RessourceLoader.RESOURCE_SCRIPT);

		setRenderer(new DiaFluxCoverageRenderer());
	}

	public static CoverageResult getResult(Section<?> coverageSection) {
		CoverageResult result = (CoverageResult) coverageSection.getSectionStore().getObject(
				COVERAGE_RESULT);

//		if (result == null) {
//			CalculateCoverageAction.calculateCoverage((Section<DiaFluxCoverageType>) coverageSection);
//		}

		return result;

	}
}
