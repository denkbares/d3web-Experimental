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
package de.d3web.we.kdom.bibtex.verbalizer;

import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.bibsonomy.model.BibTex;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

import bibtex.dom.BibtexAbstractValue;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexPerson;
import bibtex.dom.BibtexPersonList;
import bibtex.dom.BibtexString;
import de.d3web.we.core.semantic.ISemanticCore;
import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager.RenderingFormat;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.parsing.Section;

/**
 * @author Fabian Haupt
 *
 */
public class BibtexSWRCRenderer implements BibTexRenderer {

	/**
	 * To concatenate persons (authors + editors)
	 */
	private static final String AND = " and ";
	private static final ResourceBundle SWRC = ResourceBundle
			.getBundle("SWRCMapping");

	private String getSWRCMapping(String k) {
		try {
			return SWRC.getString(k.trim().toLowerCase());
		} catch (MissingResourceException e) {
			java.util.logging.Logger.getLogger(this.getClass().getName()).log(
					Level.WARNING, "missing key" + k);
			return k;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seede.d3web.we.kdom.bibtex.verbalizer.BibTexRenderer#
	 * getSupportedClassesForVerbalization()
	 */
	@Override
	public Class[] getSupportedClassesForVerbalization() {
		Class[] sup = { BibtexEntry.class };
		return sup;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seede.d3web.we.kdom.bibtex.verbalizer.BibTexRenderer#
	 * getSupportedRenderingTargets()
	 */
	@Override
	public RenderingFormat[] getSupportedRenderingTargets() {
		RenderingFormat[] formats = { RenderingFormat.SWRCOWL };
		return formats;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderer#verbalize(java.lang
	 * .Object,
	 * de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager.RenderingFormat,
	 * java.util.Map)
	 */
	@Override
	public Object render(Object o, RenderingFormat targetFormat,
			Map<String, Object> parameter) {
		if (targetFormat != RenderingFormat.SWRCOWL)
			return null;
		IntermediateOwlObject io = new IntermediateOwlObject();
		ISemanticCore sc = SemanticCoreDelegator.getInstance(KnowWEEnvironment.getInstance());
		UpperOntology uo = sc.getUpper();
		OwlHelper helper = uo.getHelper();
		BibtexEntry bte = (BibtexEntry) o;
		BibTex bt = new BibTex();
		bibtexfromEntry(bt, bte);

		String ns = sc.getNameSpaces().get("swrc");
		try {
			URI localURI = uo.getHelper().createlocalURI("" + bt.hashCode());
			// BNode bnode = uo.getVf().createBNode(bt.hashCode() + "");
			helper.attachTextOrigin(localURI,
					(Section) parameter.get("SOURCE"), io);

			String type = bte.getEntryType();
			io.addStatement(helper.createStatement(localURI, RDF.TYPE, helper
					.createURI(ns, getSWRCMapping(type))));
			if (bt.getTitle() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "title"), helper.createLiteral(bt
						.getTitle())));
			if (bt.getAbstract() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "abstract"), helper.createLiteral(bt
						.getAbstract())));
			if (bt.getYear() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "year"), helper.createLiteral(bt
						.getYear())));
			if (bte.getFieldValue("author") != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "author"), helper.createLiteral(bte
						.getFieldValue("author").toString())));
			if (bt.getBibtexKey() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, getSWRCMapping("key")), helper
						.createLiteral(bt.getBibtexKey())));
			if (bt.getMonth() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "month"), helper.createLiteral(bt
						.getMonth())));
			if (bt.getVolume() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "volume"), helper.createLiteral(bt
						.getVolume())));
			if (bt.getEdition() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "edition"), helper.createLiteral(bt
						.getEdition())));
			if (bt.getAddress() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "address"), helper.createLiteral(bt
						.getAddress())));
			if (bt.getPublisher() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "publisher"), helper.createLiteral(bt
						.getPublisher())));
			if (bt.getNote() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "note"), helper.createLiteral(bt
						.getNote())));
			if (bt.getSeries() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "series"), helper.createLiteral(bt
						.getSeries())));
			if (bt.getNumber() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "number"), helper.createLiteral(bt
						.getNumber())));
			if (bt.getHowpublished() != null)
				io.addStatement(helper.createStatement(localURI, helper
						.createURI(ns, "howpublished"), helper.createLiteral(bt
						.getHowpublished())));
			BibtexString field = null;
			field = (BibtexString) bte.getFieldValue("keywords");
			if (field != null) {
				String con = field.getContent();
				for (String cur : con.split(",")) {
					io.addStatement(helper.createStatement(localURI, helper
							.createURI(ns, "keywords"), helper
							.createLiteral(cur.trim().toLowerCase())));
				}
			}

		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return io;
	}

	private BibTex bibtexfromEntry(BibTex bibtex, BibtexEntry entry) {
		// retrieve entry/bibtex key
		bibtex.setBibtexKey(entry.getEntryKey());
		// retrieve entry type - should not be null or ""
		bibtex.setEntrytype(entry.getEntryType());

		BibtexString field = null;
		field = (BibtexString) entry.getFieldValue("title");
		if (field != null)
			bibtex.setTitle(field.getContent());
		field = (BibtexString) entry.getFieldValue("year");
		if (field != null)
			bibtex.setYear(field.getContent());

		/*
		 * add optional fields
		 */
		field = (BibtexString) entry.getFieldValue("crossref");
		if (field != null)
			bibtex.setCrossref(field.getContent());
		field = (BibtexString) entry.getFieldValue("address");
		if (field != null)
			bibtex.setAddress(field.getContent());
		field = (BibtexString) entry.getFieldValue("annote");
		if (field != null)
			bibtex.setAnnote(field.getContent());
		field = (BibtexString) entry.getFieldValue("booktitle");
		if (field != null)
			bibtex.setBooktitle(field.getContent());
		field = (BibtexString) entry.getFieldValue("chapter");
		if (field != null)
			bibtex.setChapter(field.getContent());
		field = (BibtexString) entry.getFieldValue("day");
		if (field != null)
			bibtex.setDay(field.getContent());
		field = (BibtexString) entry.getFieldValue("edition");
		if (field != null)
			bibtex.setEdition(field.getContent());
		field = (BibtexString) entry.getFieldValue("howpublished");
		if (field != null)
			bibtex.setHowpublished(field.getContent());
		field = (BibtexString) entry.getFieldValue("institution");
		if (field != null)
			bibtex.setInstitution(field.getContent());
		field = (BibtexString) entry.getFieldValue("journal");
		if (field != null)
			bibtex.setJournal(field.getContent());
		field = (BibtexString) entry.getFieldValue("key");
		if (field != null)
			bibtex.setBKey(field.getContent());
		field = (BibtexString) entry.getFieldValue("month");
		if (field != null)
			bibtex.setMonth(field.getContent());
		field = (BibtexString) entry.getFieldValue("note");
		if (field != null)
			bibtex.setNote(field.getContent());
		field = (BibtexString) entry.getFieldValue("number");
		if (field != null)
			bibtex.setNumber(field.getContent());
		field = (BibtexString) entry.getFieldValue("organization");
		if (field != null)
			bibtex.setOrganization(field.getContent());
		field = (BibtexString) entry.getFieldValue("pages");
		if (field != null)
			bibtex.setPages(field.getContent());
		field = (BibtexString) entry.getFieldValue("publisher");
		if (field != null)
			bibtex.setPublisher(field.getContent());
		field = (BibtexString) entry.getFieldValue("school");
		if (field != null)
			bibtex.setSchool(field.getContent());
		field = (BibtexString) entry.getFieldValue("series");
		if (field != null)
			bibtex.setSeries(field.getContent());
		field = (BibtexString) entry.getFieldValue("url");
		if (field != null)
			bibtex.setUrl(field.getContent());
		field = (BibtexString) entry.getFieldValue("volume");
		if (field != null)
			bibtex.setVolume(field.getContent());
		field = (BibtexString) entry.getFieldValue("abstract");
		if (field != null)
			bibtex.setAbstract(field.getContent());
		field = (BibtexString) entry.getFieldValue("type");
		if (field != null)
			bibtex.setType(field.getContent());

		bibtex.setAuthor(createPersonString(entry.getFieldValue("author")));
		bibtex.setEditor(createPersonString(entry.getFieldValue("editor")));
		return bibtex;
	}

	/**
	 * Extracts all persons from the given field value and concatenates their
	 * names with {@value #AND}.
	 *
	 * @param fieldValue
	 * @return The persons names concatenated with " and ".
	 */
	private String createPersonString(final BibtexAbstractValue fieldValue) {
		if (fieldValue != null && fieldValue instanceof BibtexPersonList) {
			/*
			 * cast into a person list and extract the persons
			 */
			final List<BibtexPerson> personList = ((BibtexPersonList) fieldValue)
					.getList();
			/*
			 * result buffer
			 */
			final StringBuffer personBuffer = new StringBuffer();
			/*
			 * build person names
			 */
			for (final BibtexPerson person : personList) {
				/*
				 * build one person
				 *
				 * FIXME: what is done here breaks author names whose last name
				 * consists of several parts, e.g., Vander Wal, Thomas If
				 * written as Thomas Vander Wal, "Vander" is interpreted as
				 * second name and the name is treated in the wrong way at
				 * several occasions. Thus, we must ensure to store all author
				 * names as lastname, firstname and only change the order in the
				 * JSPs.
				 */
				final StringBuffer personName = new StringBuffer();
				/*
				 * first name
				 */
				final String first = person.getFirst();
				if (first != null)
					personName.append(first);
				/*
				 * between first and last name
				 */
				final String preLast = person.getPreLast();
				if (preLast != null)
					personName.append(" " + preLast);
				/*
				 * last name
				 */
				final String last = person.getLast();
				if (last != null)
					personName.append(" " + last);
				/*
				 * "others" has a special meaning in BibTeX (it's converted to
				 * "et al."), so we must not ignore it!
				 */
				if (person.isOthers())
					personName.append("others");
				/*
				 * next name
				 */
				personBuffer.append(personName.toString().trim() + AND);
			}
			/*
			 * remove last " and "
			 */
			if (personBuffer.length() > AND.length()) {
				return personBuffer.substring(0, personBuffer.length()
						- AND.length());
			}
		}
		return null;
	}

}
