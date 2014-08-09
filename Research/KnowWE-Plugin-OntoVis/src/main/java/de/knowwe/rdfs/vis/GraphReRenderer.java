/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

package de.knowwe.rdfs.vis;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.strings.Strings;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.ontology.compile.OntologyCompilerFinishedEvent;
import de.knowwe.rdfs.vis.markup.OntoVisType;
import de.knowwe.rdfs.vis.markup.OntoVisTypeRenderer;
import de.knowwe.rdfs.vis.markup.sparql.SparqlVisContentType;
import de.knowwe.rdfs.vis.markup.sparql.SparqlVisTypeRenderer;

/**
 * @author Johanna Latt
 * @created 02.08.2014
 */
public class GraphReRenderer implements EventListener {

	private static GraphReRenderer gr;
	private static ArticleManager am;
	private static String fileDirPath;

	private GraphReRenderer() {}

	public static GraphReRenderer getInstance(ArticleManager am, String fileDirPath) {
		if (gr == null) {
			gr = new GraphReRenderer();
			GraphReRenderer.am = am;
			GraphReRenderer.fileDirPath = fileDirPath;
		}
		return gr;
	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		List<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>();
		events.add(OntologyCompilerFinishedEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		OntologyCompilerFinishedEvent e = (OntologyCompilerFinishedEvent) event;
		OntologyCompiler oc = e.getCompiler();
		String hash = String.valueOf(oc.getCompileSection().getTitle().hashCode());

		// delete all graph-files that are based on this compiler hash
		List<File> files = findAllFilesForCompiler(fileDirPath, hash);
		for (File f : files) {
			f.delete();
			// System.out.println(f.getName() + " - Deleted? " + f.delete());
		}

		// re-render all OntoVisType-sections
		List<Section<? extends Type>> sections = Sections.findSectionsOfTypeGlobal(OntoVisType.class, am);
		for (Section<? extends Type> s : sections) {
//			Section<OntoVisType> section = Sections.cast(s, OntoVisType.class);
//			section.get().getRenderer().render(section, null, null);
			new OntoVisTypeRenderer().renderContents(s, null, null);
		}

		// re-render all SparqlVisType-sections
		sections = Sections.findSectionsOfTypeGlobal(SparqlVisContentType.class, am);
		for (Section<? extends Type> s : sections) {
//			Section<SparqlVisType> section = Sections.cast(s, SparqlVisType.class);
//			section.get().getRenderer().render(section, null, null);
			new SparqlVisTypeRenderer().render(s, null, null);
		}
	}

	private static List<File> findAllFilesForCompiler(String fileDirPath, String hash) {
		List<File> result = new LinkedList<>();
		if (Strings.isBlank(hash)) {
			return result;
		}

		File fileDir = new File(fileDirPath);
		File[] files = fileDir.listFiles();

		for (File f : files) {
			String name = f.getName();
			String nameWithoutExtension = name.split("\\.")[0];
			if (nameWithoutExtension.endsWith(hash)) {
				result.add(f);
			}
		}
		return result;
	}
}