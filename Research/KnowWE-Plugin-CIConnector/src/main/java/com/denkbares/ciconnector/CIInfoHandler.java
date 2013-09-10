/*
 * Copyright (C) 2012 denkbares GmbH, Germany
 */
package com.denkbares.ciconnector;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.rdf2go.RDF2GoSubtreeHandler;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * 
 * @author Sebastian Furth
 * @created 17.12.2012
 */
public class CIInfoHandler extends RDF2GoSubtreeHandler<CIInfoType> {

	private static final String EL_PLUGIN = "plugin";
	private static final String EL_DESCRIPTION = "description";
	private static final String EL_STATS = "stats";
	private static final String EL_STAT = "stat";
	private static final String EL_CHANGES = "changes";
	private static final String EL_CHANGE = "change";

	private static final String ATT_ID = "id";
	private static final String ATT_VALUE = "value";
	private static final String ATT_REVISION = "revision";
	private static final String ATT_TIMESTAMP = "timestamp";
	private static final String ATT_AUTHOR = "author";

	@Override
	public Collection<Message> create(Article article, Section<CIInfoType> section) {

		try {
			Collection<CIInfo> infos = getCIInfos(article);
			createTriples(article, infos);
		}
		catch (IOException e) {
			return Messages.asList(Messages.warning("Unable to load CI-Info"));
		}
		catch (JDOMException e) {
			return Messages.asList(Messages.warning("Unable to load CI-Info"));
		}

		return Messages.noMessage();

	}

	private void createTriples(Article article, Collection<CIInfo> infos) {

		Rdf2GoCore core = Rdf2GoCore.getInstance();

		URI changeTypeURI = core.createlocalURI("Change");
		URI hasTimeStampURI = core.createlocalURI("hasTimeStamp");
		URI hasRevisionURI = core.createlocalURI("hasRevision");
		URI hasAuthorURI = core.createlocalURI("hasAuthor");
		URI hasCommitTextURI = core.createlocalURI("hasCommitText");
		URI hasChangeURI = core.createlocalURI("hasChange");
		URI pluginTypeURI = core.createlocalURI("Plugin");
		URI hasIDURI = core.createlocalURI("hasID");
		URI hasDescriptionURI = core.createlocalURI("hasDescription");
		URI hasStatURI = core.createlocalURI("hasStat");
		URI hasValueURI = core.createlocalURI("hasValue");
		URI statTypeURI = core.createlocalURI("Stat");
		URI hasKeyURI = core.createlocalURI("hasKey");

		for (CIInfo info : infos) {

			// lns:Plugin-X rdf:Type lns:Plugin
			URI pluginURI = core.createlocalURI(info.getPlugin());
			core.addStatements(article, core.createStatement(pluginURI, RDF.type, pluginTypeURI));
			core.addStatements(article, core.createStatement(pluginURI, hasIDURI,
					core.createLiteral(info.getPlugin())));

			// lns:Plugin-X lns:hasDescription "description"
			core.addStatements(article, core.createStatement(pluginURI, hasDescriptionURI,
					core.createLiteral(info.getDescription())));

			// changes...
			for (Change change : info.getChanges()) {
				BlankNode changeURI = core.createBlankNode();
				core.addStatements(article,
						core.createStatement(changeURI, RDF.type, changeTypeURI));
				core.addStatements(article, core.createStatement(changeURI, hasTimeStampURI,
						core.createLiteral(change.getTimestamp())));
				core.addStatements(article, core.createStatement(changeURI, hasRevisionURI,
						core.createLiteral(change.getRevision())));
				core.addStatements(article, core.createStatement(changeURI, hasAuthorURI,
						core.createLiteral(change.getAuthor())));
				core.addStatements(article, core.createStatement(changeURI, hasCommitTextURI,
						core.createLiteral(change.getCommitText())));
				core.addStatements(article,
						core.createStatement(pluginURI, hasChangeURI, changeURI));
			}

			// stats...
			for (Entry<String, String> stat : info.getStat().entrySet()) {
				BlankNode statURI = core.createBlankNode();
				core.addStatements(article, core.createStatement(statURI, RDF.type, statTypeURI));
				core.addStatements(article, core.createStatement(statURI, hasKeyURI,
						core.createLiteral(stat.getKey())));
				core.addStatements(article, core.createStatement(statURI, hasValueURI,
						core.createLiteral(stat.getValue())));
				core.addStatements(article, core.createStatement(pluginURI, hasStatURI, statURI));
			}

			core.commit();

		}

	}

	private Collection<CIInfo> getCIInfos(Article article) throws IOException, JDOMException {

		Collection<CIInfo> result = new LinkedList<CIInfo>();
		Collection<WikiAttachment> attachments =
				Environment.getInstance().getWikiConnector().getAttachments(
						SaveCIInfoAction.ARTICLE);

		for (WikiAttachment attachment : attachments) {
			if (attachment.getFileName().endsWith(SaveCIInfoAction.FILENAMESUFFIX)) {
				for (int version : attachment.getAvailableVersions()) {
					result.addAll(parseCIInfo(attachment, version));
				}
			}
		}

		return result;
	}

	private Collection<CIInfo> parseCIInfo(WikiAttachment attachment, int version) throws JDOMException, IOException {

		Collection<CIInfo> result = new LinkedList<CIInfo>();
		Document doc = new SAXBuilder().build(attachment.getInputStream(version));

		List<?> pluginElements = doc.getRootElement().getChildren(EL_PLUGIN);
		for (Object o : pluginElements) {

			// plugin
			Element plugin = (Element) o;
			String pluginID = plugin.getAttributeValue(ATT_ID);
			CIInfo info = new CIInfo(pluginID);

			// description
			Element description = plugin.getChild(EL_DESCRIPTION);
			info.setDescription(description.getText());

			// stats
			Element stats = plugin.getChild(EL_STATS);
			List<?> statElements = stats.getChildren(EL_STAT);
			for (Object s : statElements) {
				Element stat = (Element) s;
				String statID = stat.getAttributeValue(ATT_ID);
				String statValue = stat.getAttributeValue(ATT_VALUE);
				info.addStat(statID, statValue);
			}

			// changes
			Element changes = plugin.getChild(EL_CHANGES);
			List<?> changeElements = changes.getChildren(EL_CHANGE);
			for (Object s : changeElements) {
				Element changeElement = (Element) s;
				String revision = changeElement.getAttributeValue(ATT_REVISION);
				String author = changeElement.getAttributeValue(ATT_AUTHOR);
				String timestamp = changeElement.getAttributeValue(ATT_TIMESTAMP);
				String commitText = changeElement.getText();
				Change change = new Change(revision, timestamp, author, commitText);
				info.addChange(change);
			}

			result.add(info);
		}

		return result;
	}
}
