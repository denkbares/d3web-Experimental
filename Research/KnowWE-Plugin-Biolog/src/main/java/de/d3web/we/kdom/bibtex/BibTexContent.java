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
package de.d3web.we.kdom.bibtex;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.parser.BibtexParser;
import bibtex.parser.ParseException;
import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager;
import de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager.RenderingFormat;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.xml.XMLContent;

/**
 * A type that translates the bibtex data into the triple-store using the SWRC
 * ontology. (http://ontoware.org/swrc/swrc/SWRCOWL/swrc_updated_v0.7.1.owl)
 * 
 * @author Jochen
 * @created 16.09.2010
 */
public class BibTexContent extends XMLContent{

	public static final String PARSEEXCEPTION = "PARSEEXCEPTION";
	public static final String IOEXCEPTION = "IOEXCEPTION";
	public static final String BIBTEXs = "BIBTEXs";

	@Override
	protected void init() {
		this.setCustomRenderer(new BibTexContentRenderer());
		this.addSubtreeHandler(new BibTexContentSubTreeHandler());
	}

	private class BibTexContentSubTreeHandler extends OwlSubtreeHandler<BibTexContent> {

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<BibTexContent> s) {
			Message msg=null;
			IntermediateOwlObject io = new IntermediateOwlObject();
			String text = s.getText();
			BibtexFile bibtexFile = new BibtexFile();
			BibtexParser parser = new BibtexParser(false);

			try {
				StringReader reader = new StringReader(text);
				parser.parse(bibtexFile, reader);
			} catch (ParseException e) {
				KnowWEUtils.storeObject(article, s, PARSEEXCEPTION, e.toString());
			} catch (IOException e) {
				KnowWEUtils.storeObject(article, s, IOEXCEPTION, e.toString());
			}

			if (bibtexFile.getEntries().size() != 0) {
				KnowWEUtils.storeObject(article, s, BIBTEXs, bibtexFile);
			}
			HashMap<String, Object> params=new HashMap<String, Object>();
			params.put("SOURCE",s);
			if (bibtexFile.getEntries().size() != 0) {
				for (Object cur : bibtexFile.getEntries()) {
					if (cur instanceof BibtexEntry)
					io.merge((IntermediateOwlObject) BibTexRenderManager
							.getInstance().render(cur,
									RenderingFormat.SWRCOWL,params));
				}
			}
			SemanticCoreDelegator.getInstance(KnowWEEnvironment.getInstance()).addStatements(io, s);

			return Collections.singleton(msg);
		}

	}

}
