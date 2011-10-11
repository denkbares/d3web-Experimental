/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.we.biolog;

import java.io.File;

import javax.servlet.ServletContext;

import de.d3web.we.core.semantic.ISemanticCore;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.KnowWERessourceLoader;
import de.knowwe.plugin.Instantiation;

/**
 * Biolog plugin Contains knowledge formalization components for EML,
 * FreeMap-taxonomies and bibtex. Further contains some searchProviders for the
 * KnowWE MultiSearchEngine
 * 
 * 
 * @author Jochen
 */
public class BiologPlugin implements Instantiation {

	/**
	 * Singleton instance
	 */
	private static BiologPlugin instance;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.we.module.AbstractDefaultKnowWEModule#initModule(javax.servlet
	 * .ServletContext)
	 */
	@Override
	public void init(ServletContext context) {
		ISemanticCore sc = SemanticCoreDelegator.getInstance(KnowWEEnvironment.getInstance());
		sc.addNamespace("swrc", "http://swrc.ontoware.org/ontology#");
		String ontfile = KnowWEEnvironment.getInstance()
				.getKnowWEExtensionPath()
				+ File.separatorChar + "swrc_updated_v0.7.1.owl";
		sc.getUpper().loadOwlFile(new File(ontfile));

		KnowWERessourceLoader.getInstance().add("biolog-style.css",
				KnowWERessourceLoader.RESOURCE_STYLESHEET);
		KnowWERessourceLoader.getInstance().add("Biolog.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);

		// MultiSearchEngine.getInstance().addProvider(TaggingMangler.getInstance());
		// MultiSearchEngine.getInstance().addProvider(new
		// BibtexSearchProvider());
		// MultiSearchEngine.getInstance().addProvider(new EMLSearchProvider());
		// MultiSearchEngine.getInstance().addProvider(new
		// AnnotationsProvider());

	}

	/**
	 * Singleton is necessary for ALL KnowWE-Modules, because on wiki startup
	 * getInstance() is called by reflections for instanciation of the module.
	 * 
	 * @return
	 */
	public static BiologPlugin getInstance() {
		if (instance == null)
			instance = new BiologPlugin();

		return instance;
	}

}
