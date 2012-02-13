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

package de.knowwe.compile.object;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.plugin.Plugins;

public class AbstractKnowledgeUnitType extends AbstractType
		implements KnowledgeUnit {

	private KnowledgeUnitCompileScript compileScript = null;

	public AbstractKnowledgeUnitType() {
		Extension[] exts = PluginManager.getInstance().getExtensions(
				Plugins.EXTENDED_PLUGIN_ID,
				Plugins.EXTENDED_POINT_COMPILESCRIPT);
		for (Extension extension : exts) {
			String parameter = extension.getParameter("scope");
			String thisClassName = this.getClass().getCanonicalName();
			if (parameter.equals(thisClassName)) {
				Object o = extension.getSingleton();
				if (o instanceof KnowledgeUnitCompileScript) {
					compileScript = (((KnowledgeUnitCompileScript) o));
				}
			}
		}
	}

	@Override
	public KnowledgeUnitCompileScript getCompileScript() {
		return compileScript;
	}

}
