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
package de.d3web.knowwe.type;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;


/**
 * 
 * @author Johannes Dienst
 * @created 09.06.2011
 */
public class SimpleAnnotation extends AbstractType {

	@Override
	public void init() {
		this.sectionFinder = new AllTextFinderTrimmed();
		//		this.addSubtreeHandler(new SimpleAnnotationSubTreeHandler());
	}

	//	private class SimpleAnnotationSubTreeHandler extends
	//	OwlSubtreeHandler<Type> {
	//
	//		@Override
	//		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section s) {
	//			IntermediateOwlObject io = new IntermediateOwlObject();
	//			UpperOntology uo = UpperOntology.getInstance();
	//
	//			String annos = "";
	//			try {
	//				annos = URLEncoder.encode(s.getOriginalText().trim(), "UTF-8");
	//			}
	//			catch (UnsupportedEncodingException e1) {
	//				// TODO Auto-generated catch block
	//				e1.printStackTrace();
	//			}
	//			URI anno = null;
	//			if (annos.contains(":")) {
	//				String[] list = annos.split(":");
	//				String ns = list[0];
	//				String ens = SemanticCoreDelegator.getInstance().expandNamespace(ns);
	//				if (ns.equals(ens)) {
	//					io.setValidPropFlag(false);
	//					io.setBadAttribute(ns + " is no valid namespace");
	//				}
	//				try {
	//					anno = uo.getHelper().createURI(ens, list[1]);
	//				}
	//				catch (IllegalArgumentException e) {
	//					io.setValidPropFlag(false);
	//					io.setBadAttribute(ns);
	//				}
	//			}
	//			else {
	//				anno = uo.getHelper().createlocalURI(annos);
	//			}
	//			if (anno != null) {
	//				io.addLiteral(anno);
	//			}
	//			KnowWEUtils.storeObject(article, s, OwlHelper.IOO, io);
	//			return null;
	//		}
	//
	//	}

}
