/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.we.kdom.dashTree.propertyDefinition;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.StyleRenderer;

/**
 * @author Jochen
 * 
 *         Type for defining domain and range of an object-property syntax:
 *         DOMAINDEF --> RANGEDEF (@see DomainRangeOperatorType for Operator)
 * 
 * 
 */
public class PropertyDetails extends AbstractType {

	public PropertyDetails() {
		this.sectionFinder = new PropertyDetailsSectionFinder();
		this.childrenTypes.add(new DomainDefinition());
		this.childrenTypes.add(new DomainRangeOperatorType());
		this.childrenTypes.add(new RangeDefinition());

	}

}

class PropertyDetailsSectionFinder implements SectionFinder {

	private final AllTextFinderTrimmed textFinder = new AllTextFinderTrimmed();

	@Override
	public List<SectionFinderResult> lookForSections(String text,
			Section<?> father, Type type) {
		if (text.contains("-->")) {
			return textFinder.lookForSections(text, father, type);
		}
		return null;
	}

}

class DomainFinder implements SectionFinder {

	@Override
	public List<SectionFinderResult> lookForSections(String text,
			Section<?> father, Type type) {
		if (text.contains(DomainRangeOperatorType.DOMAIN_RANGE_OPERATOR)) {

			List<SectionFinderResult> result = new ArrayList<SectionFinderResult>();
			result.add(new SectionFinderResult(0,
					text.indexOf(DomainRangeOperatorType.DOMAIN_RANGE_OPERATOR)));
			return result;
		}
		return null;
	}

}

class RangeDefinition extends AbstractType {

	private static RangeDefinition defaultInstance = null;

	public static RangeDefinition getDefaultInstance() {
		if (defaultInstance == null) {
			defaultInstance = new RangeDefinition();

		}

		return defaultInstance;
	}

	protected RangeDefinition() {
		this.sectionFinder = new AllTextFinderTrimmed();
		this.setRenderer(new RangeRenderer());
	}
}

class RangeRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user,
			StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<span title=\"Range restriction\">"));
		new StyleRenderer("color:rgb(128, 128, 0)").render(sec, user, string);
		string.append(KnowWEUtils.maskHTML("</span>"));

	}

}

class DomainDefinition extends AbstractType {

	private static DomainDefinition defaultInstance = null;

	public static DomainDefinition getDefaultInstance() {
		if (defaultInstance == null) {
			defaultInstance = new DomainDefinition();

		}

		return defaultInstance;
	}

	protected DomainDefinition() {
		this.sectionFinder = new DomainFinder();
		this.setRenderer(new DomainRenderer());
	}
}

class DomainRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user,
			StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<span title=\"Domain restriction\">"));
		new StyleRenderer("color:rgb(0, 128, 0)").render(sec, user, string);
		string.append(KnowWEUtils.maskHTML("</span>"));

	}

}
