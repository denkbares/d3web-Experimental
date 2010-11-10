/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.records.io;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.protocol.Protocol;
import de.d3web.core.session.protocol.ProtocolEntry;

/**
 * Handels the facts element in an xml case repository
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class ProtocolHandler implements SessionPersistenceHandler {

	private static final String HANDLER_ELEMENT_NAME = "protocol";

	@Override
	public void read(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		List<Element> handlerNodes = XMLUtil.getElementList(sessionElement.getChildNodes());
		Protocol protocol = sessionRecord.getProtocol();
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		for (Element handlerNode : handlerNodes) {
			if (handlerNode.getNodeName().equalsIgnoreCase(HANDLER_ELEMENT_NAME)) {
				List<Element> entryNodes = XMLUtil.getElementList(handlerNode.getChildNodes());
				for (Element entryNode : entryNodes) {
					ProtocolEntry entry = (ProtocolEntry) spm.readFragment(entryNode, null);
					protocol.addEntry(entry);
				}
			}
		}
	}

	@Override
	public void write(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		Document doc = sessionElement.getOwnerDocument();
		Element handlerNode = doc.createElement(HANDLER_ELEMENT_NAME);
		sessionElement.appendChild(handlerNode);

		Protocol protocol = sessionRecord.getProtocol();
		for (ProtocolEntry entry : protocol.getProtocolHistory()) {
			Element entryNode = SessionPersistenceManager.getInstance().writeFragment(entry, doc);
			handlerNode.appendChild(entryNode);
		}
	}

}
