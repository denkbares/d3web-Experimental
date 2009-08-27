package de.d3web.KnOfficeParser.table;

import java.util.ResourceBundle;

import de.d3web.KnOfficeParser.IDObjectManagement;
import de.d3web.report.Message;
import de.d3web.KnOfficeParser.util.MessageKnOfficeGenerator;
import de.d3web.KnOfficeParser.util.Scorefinder;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
/**
 * Erstellet Scoring Rules aus Tabellenzellen
 * @author Markus Friedrich
 *
 */
public class ScoringRuleBuilder implements CellKnowledgeBuilder {

	private ResourceBundle properties;
	
	public ScoringRuleBuilder(String file) {
		properties = ResourceBundle.getBundle(file);
	}
	
	@Override
	public Message add(IDObjectManagement idom, int line, int column, String file,
			AbstractCondition cond, String text, Diagnosis diag, boolean errorOccured) {
		Score score = Scorefinder.getScore(text);
		if (score==null) {
			String s;
			try {
				s = properties.getString(text);
				score = Scorefinder.getScore(s);
			} catch (Exception e) {
				return MessageKnOfficeGenerator.createScoreDoesntExistError(file, line, column, "", text);
			}
		}
		String newRuleID = idom.findNewIDFor(new RuleComplex());
		RuleFactory.createHeuristicPSRule(newRuleID, diag, score, cond);
		return null;
	}

}
