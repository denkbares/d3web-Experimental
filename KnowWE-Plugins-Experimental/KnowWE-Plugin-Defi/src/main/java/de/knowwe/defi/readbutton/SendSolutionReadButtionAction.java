/*
 * Copyright (C) 2014 think-further.de
 */
package de.knowwe.defi.readbutton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

import de.d3web.core.records.FactRecord;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Session;
import de.d3web.we.basic.SessionProvider;
import de.d3web.we.knowledgebase.D3webCompiler;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.defi.user.UserUtilities;

/**
 * @author Sebastian Furth
 * @created 03.11.16
 */
public class SendSolutionReadButtionAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String sectionId = context.getParameter("section");
		Section<?> section = Sections.get(sectionId);

		if (section != null) {
			D3webCompiler compiler = Compilers.getCompiler(section, D3webCompiler.class);
			if (compiler != null) {
				Session session = SessionProvider.getSession(context, compiler.getKnowledgeBase());
				String userName = context.getUserName();
				Article article = UserUtilities.getDataPage(userName);
				storeSessionAsAttachment(userName, session, article.getTitle(),
						"Fragebogen - " + section.getArticle().getTitle() + " - " + userName + ".csv");
			}
		}

	}

	private void storeSessionAsAttachment(String user, Session session, String attachmentArticle, String attachmentName) throws IOException {

		SessionRecord record = SessionConversionFactory.copyToSessionRecord(session);

		Collection<FactRecord> facts = new LinkedHashSet<>();
		facts.addAll(record.getInterviewFacts());
		facts.addAll(record.getValueFacts());

		// write to output stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (FactRecord fact : facts) {
			baos.write(fact.getObjectName().getBytes("UTF-8"));
			baos.write(";".getBytes("UTF-8"));
			baos.write(fact.getValue().toString().getBytes("UTF-8"));
			baos.write("\n".getBytes("UTF-8"));
		}

		// save attachment
		ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
		Environment.getInstance().getWikiConnector()
				.storeAttachment(attachmentArticle, attachmentName, user, inputStream);
	}
}
