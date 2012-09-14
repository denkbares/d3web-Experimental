/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.diaflux.coverage.ci;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.TestParameter;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.diaflux.coverage.DiaFluxCoverageType;

/**
 * 
 * @author Reinhard Hatko
 * @created 26.03.2012
 */
public class FlowCoverage extends de.d3web.testing.AbstractTest<KnowledgeBase> {

	
	
	public FlowCoverage() {
		this.addParameter("param1", TestParameter.Type.String, TestParameter.Mode.Mandatory, "description param1");
		this.addParameter("param2", TestParameter.Type.String, TestParameter.Mode.Mandatory, "description param2");
	}
	
	@Override
	public Message execute(KnowledgeBase kb, String[] args) {

		String packageName = args[1];

		Environment env = Environment.getInstance();
		PackageManager packageManager = env.getPackageManager(Environment.DEFAULT_WEB);

		List<Section<?>> sectionsOfPackage = packageManager.getSectionsOfPackage(packageName);

		CoverageResult result = null;
		for (Section<?> section : sectionsOfPackage) {
			if (section.get() instanceof DiaFluxCoverageType) {
				result = DiaFluxCoverageType.getResult(section);
				break;

			}
		}

		if (result == null) {
			return new Message(Type.ERROR);
		}

		FlowSet flowSet = DiaFluxUtils.getFlowSet(kb);
		StringBuilder bob = new StringBuilder();
		for (Flow flow : flowSet) {
			double coverage = result.getFlowCoverage(flow);
			if (coverage < 1) {
				bob.append("Flow '" + flow.getName()
						+ "' is not covered by any testcase.\r");
			}
		}

		if (bob.length() == 0) {

			return new Message(Type.SUCCESS, null);
		}
		else {
			return new Message(Type.FAILURE, bob.toString());

		}
	}


	@Override
	public Class<KnowledgeBase> getTestObjectClass() {
		return KnowledgeBase.class;
	}

	@Override
	public String getDescription() {
		return "No description available";
	}

}
