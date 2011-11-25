package de.d3web.we.finding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.we.object.QuestionReference;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.report.message.NoSuchObjectError;

public class ApproximateQuestionNumReference extends QuestionReference {

	public ApproximateQuestionNumReference() {
		super();
		this.clearSubtreeHandlers();
		this.addSubtreeHandler(Priority.HIGH, new QuestionNumRegistrationHandler());

	}

	class QuestionNumRegistrationHandler extends SubtreeHandler<ApproximateQuestionNumReference> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<ApproximateQuestionNumReference> s) {

			KnowWEUtils.getTerminologyHandler(article.getWeb())
					.registerTermReference(article, s);

			Question question = s.get().getTermObject(article, s);

			if (question == null) {
				return Arrays.asList((KDOMReportMessage) new NoSuchObjectError(
						s.get().getName()
								+ ": " + s.get().getTermIdentifier(s)));
			}

			// check for QuestionNum
			if (!(question instanceof QuestionNum)) {
				return Arrays.asList((KDOMReportMessage) new NoSuchObjectError(
						s.get().getName()
								+ " QuestionNum expected:  " + s.get().getTermIdentifier(s)));
			}

			return new ArrayList<KDOMReportMessage>(0);
		}

		@Override
		public void destroy(KnowWEArticle article, Section<ApproximateQuestionNumReference> s) {
			KnowWEUtils.getTerminologyHandler(article.getWeb()).unregisterTermReference(
					article, s);
		}

	}
}
