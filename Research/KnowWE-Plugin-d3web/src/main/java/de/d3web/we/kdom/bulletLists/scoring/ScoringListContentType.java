/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.kdom.bulletLists.scoring;

import de.d3web.we.kdom.bulletLists.BulletContentType;
import de.d3web.we.kdom.bulletLists.BulletListItemLine;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.utils.KnowWETypeSet;
import de.knowwe.core.utils.KnowWETypeUtils;
import de.knowwe.kdom.xml.XMLContent;

public class ScoringListContentType extends XMLContent {

	@Override
	protected void init() {
		this.childrenTypes.add(new BulletListItemLine());

		KnowWETypeSet set = new KnowWETypeSet();
		KnowWETypeUtils.getAllChildrenTypesRecursive(this, set);
		Type contentType = set.getInstanceOf(BulletContentType.class);

		if (contentType instanceof AbstractType) { // damn, not
																// nice. maybe
																// we need some
																// interface
																// changes one
																// day
			((AbstractType) contentType).addSubtreeHandler(new CreateScoresHandler());
			((AbstractType) contentType).setCustomRenderer(new ValueRenderer());
		}

	}

}
