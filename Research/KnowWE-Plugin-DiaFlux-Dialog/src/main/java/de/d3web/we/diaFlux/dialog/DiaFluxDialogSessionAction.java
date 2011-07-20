/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaFlux.dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.wikiConnector.ConnectorAttachment;

/**
 * 
 * @author Florian Ziegler
 * @created 18.07.2011
 */
public class DiaFluxDialogSessionAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String type = context.getParameter("type");
		String topic = context.getTopic();

		String fileName = "DIAFLUXDIALOGSESSIONS_" + topic + ".xml";

		KnowWEEnvironment environment = KnowWEEnvironment.getInstance();

		List<String> fileNames = environment.getWikiConnector().getAttachmentFilenamesForPage(
				topic);

		boolean fileFound = fileNames.contains(fileName);

		Collection<ConnectorAttachment> attachments = environment.getWikiConnector().getAttachments();
		if (type.equals("load")) {
			if (fileFound) {
				ConnectorAttachment ca = DiaFluxDialogUtils.findConnectorAttachmentWithName(
						attachments, fileName);
				List<DiaFluxDialogSession> sessions = convertXMLtoQFs(ca.getInputStream());
				StringBuilder hiddenSessions = new StringBuilder();
				for (DiaFluxDialogSession session : sessions) {
					hiddenSessions.append("[SESSION]");
					hiddenSessions.append(DiaFluxDialogUtils.createHiddenSessionContent(
									session, true));
				}
				context.getWriter().write(hiddenSessions.toString());
			}
			else {
				context.getWriter().write("No saved sessions");
			}
		}
		else if (type.equals("save")) {
			DiaFluxDialogSession session = DiaFluxDialogManager.getInstance().getSession();
			List<DiaFluxDialogSession> sessions = null;

			if (!fileFound) {
				sessions = new LinkedList<DiaFluxDialogSession>();
				sessions.add(session);

			}
			else {
				ConnectorAttachment ca = DiaFluxDialogUtils.findConnectorAttachmentWithName(
						attachments, fileName);
				sessions = convertXMLtoQFs(ca.getInputStream());
				sessions.add(session);
			}

			Document doc = convertSessionsToXML(sessions);
			File f = new File(fileName);
			f.createNewFile();
			FileOutputStream stream = new FileOutputStream(f);
			stream.flush();
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(doc, stream);
			stream.close();
			environment.getWikiConnector().storeAttachment(topic,
					f);

		}
	}

	public static Document convertSessionsToXML(List<DiaFluxDialogSession> sessions) {
		Element root = new Element("Sessions");
		Document doc = new Document(root);

		for (DiaFluxDialogSession session : sessions) {
			root.addContent(convertSessionToXML(session));
		}

		return doc;
	}

	public static Element convertSessionToXML(DiaFluxDialogSession session) {
		List<DiaFluxDialogQuestionFindingPair> forwardKnowledge = session.getForwardKnowledge();
		List<DiaFluxDialogQuestionFindingPair> path = session.getPath();

		Element root = new Element("Session");

		Element knowledge = new Element("ForwardKnowledge");
		root.addContent(knowledge);
		for (DiaFluxDialogQuestionFindingPair pair : forwardKnowledge) {
			Element p = new Element("Pair");
			Element question = new Element("Question");
			question.setText(pair.getQuestion());
			Element findings = new Element("Findings");
			for (String s : pair.getFinding()) {
				Element finding = new Element("Finding");
				finding.setText(s);
				findings.addContent(finding);
			}
			p.addContent(question);
			p.addContent(findings);
			knowledge.addContent(p);
		}

		Element exactPath = new Element("Path");
		root.addContent(exactPath);
		for (DiaFluxDialogQuestionFindingPair pair : path) {
			Element p = new Element("Pair");
			Element question = new Element("Question");
			question.addContent(pair.getQuestion());
			Element findings = new Element("Findings");
			for (String s : pair.getFinding()) {
				Element finding = new Element("Finding");
				finding.setText(s);
				findings.addContent(finding);
			}
			p.addContent(question);
			p.addContent(findings);
			exactPath.addContent(p);
		}
		return root;
	}

	@SuppressWarnings("unchecked")
	public static List<DiaFluxDialogSession> convertXMLtoQFs(InputStream is) {
		List<DiaFluxDialogSession> list = new LinkedList<DiaFluxDialogSession>();
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(is);
			Element root = doc.getRootElement();
			List<Element> sessions = root.getChildren("Session");
			for (Element session : sessions) {

				Element knowledge = session.getChild("ForwardKnowledge");
				Element path = session.getChild("Path");
				List<Element> knowledges = knowledge.getChildren("Pair");
				List<Element> pairs = path.getChildren("Pair");

				List<DiaFluxDialogQuestionFindingPair> forwardKnowledge = new ArrayList<DiaFluxDialogQuestionFindingPair>();
				List<DiaFluxDialogQuestionFindingPair> qfPair = new ArrayList<DiaFluxDialogQuestionFindingPair>();

				for (Element e : knowledges) {
					Element question = e.getChild("Question");
					String q = question.getText();
					Element findings = e.getChild("Findings");
					List<String> f = new ArrayList<String>();

					List<Element> findingsChildren = findings.getChildren("Finding");

					for (Element finding : findingsChildren) {
						f.add(finding.getText());
					}
					forwardKnowledge.add(new DiaFluxDialogQuestionFindingPair(q, f));
				}

				for (Element e : pairs) {
					Element question = e.getChild("Question");
					String q = question.getText();
					Element findings = e.getChild("Findings");
					List<String> f = new ArrayList<String>();

					List<Element> findingsChildren = findings.getChildren("Finding");

					for (Element finding : findingsChildren) {
						f.add(finding.getText());
					}
					qfPair.add(new DiaFluxDialogQuestionFindingPair(q, f));
				}
				list.add(new DiaFluxDialogSession(forwardKnowledge, qfPair));
			}

		}
		catch (JDOMException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}
