package de.d3web.KnOfficeParser.rule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;

import org.antlr.runtime.RecognitionException;

import de.d3web.KnOfficeParser.SingleKBMIDObjectManager;
import de.d3web.report.Message;
import de.d3web.kernel.domainModel.KnowledgeBaseManagement;


public class Complexrulestester {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws RecognitionException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, RecognitionException {
		File file = new File("examples\\Regeln4.txt");
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();
		D3ruleBuilder builder = new D3ruleBuilder(file.toString(), true, new SingleKBMIDObjectManager(kbm));
		Reader r = new FileReader(file);
		Collection<Message> col = builder.addKnowledge(r, new SingleKBMIDObjectManager(kbm), null);
		List<Message> errors=(List<Message>) col;
		for (Message m: errors) {
			System.out.println(m);
		}
	}

}
