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

package de.d3web.we.biolog.freemap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.xml.AbstractXMLType;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * 
 * A type for the (recursive) KDOM-parsing of the xml-structure of FreeMap
 * xml-dumps.
 * 
 * @author Jochen
 * @created 16.09.2010
 */
public class FreeMapNode extends AbstractXMLType {

	private static FreeMapNode instance;

	public static FreeMapNode getInstance() {
		if (instance == null) {
			instance = new FreeMapNode();

		}

		return instance;
	}

	public static String getText(Section<FreeMapNode> s) {
		Map<String, String> map = AbstractXMLType.getAttributeMapFor(s);

		String text = map.get("TEXT");
		if (text == null) {
			text = "no text found";
		}
		if (text.startsWith("=")) {
			text = text.substring(1).trim();
		}
		return text;
	}

	private FreeMapNode() {
		super("node");
	}

	@Override
	protected void init() {
		this.childrenTypes.add(this);
		this.setCustomRenderer(new FreeMapNodeRenderer());
		this.addSubtreeHandler(new FreeMapNodeOWLSubTreeHandler());
	}

	/**
	 * 
	 * This handler creates the 'subMethodOf'-relations between the Nodes
	 * representing the BIOLOG-Methods. Thus, the term hierarchy is inserted
	 * into the rdf-triple-store.
	 * 
	 * @author Jochen
	 * @created 16.09.2010
	 */
	private class FreeMapNodeOWLSubTreeHandler extends OwlSubtreeHandler<FreeMapNode> {

		@SuppressWarnings("unchecked")
		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<FreeMapNode> s) {
			Section<FreeMapNode> element = s; // warning
			IntermediateOwlObject io = new IntermediateOwlObject();
			if (s.get().isAssignableFromType(FreeMapNode.class)) {
				Section<? extends AbstractXMLType> father = AbstractXMLType
						.getXMLFatherElement(element);
				if (father != null
						&& father.get() instanceof FreeMapNode) {

					createSubClassRelation(getText(element),
							getText((Section<FreeMapNode>) father), io);
				}
			}
			List<Section<FreeMapNode>> children = new ArrayList<Section<FreeMapNode>>();
			Sections.findSuccessorsOfType(s, FreeMapNode.class, children);
			for (Section<FreeMapNode> child : children) {
				if (child.get() instanceof FreeMapNode)
					io.merge((IntermediateOwlObject) KnowWEUtils
							.getStoredObject(child, OwlHelper.IOO));
			}
			SemanticCoreDelegator.getInstance(KnowWEEnvironment.getInstance()).addStatements(io, s);
			KnowWEUtils.storeObject(article, s, OwlHelper.IOO, io);
			return new ArrayList<KDOMReportMessage>();
		}

	}

	private void createSubClassRelation(String child, String fatherElement,
			IntermediateOwlObject io) {
		UpperOntology uo = UpperOntology.getInstance();
		URI localURI = uo.getHelper().createlocalURI(child);
		URI fatherURI = uo.getHelper().createlocalURI(fatherElement);
		try {
			io.addStatement(uo.getHelper().createStatement(localURI,
					uo.getHelper().createlocalURI("submethodOf"), fatherURI));
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * renders a section representing one FreeMap-Node
	 * 
	 * @author Jochen
	 * @created 16.09.2010
	 */
	@SuppressWarnings("unchecked")
	class FreeMapNodeRenderer extends KnowWEDomRenderer {

		@Override
		public void render(KnowWEArticle article, Section sec,
				UserContext user, StringBuilder string) {
			int depth = AbstractXMLType.getXMLDepth(sec);

			string.append(createDashes(depth - 1) + "  " + getText(sec) + "\n");

			DelegateRenderer.getInstance().render(article, sec, user, string);

		}

		private String createDashes(int dashes) {
			String s = "";
			for (int i = 0; i < dashes; i++) {
				s += "-";
			}

			return s;
		}

	}


}
