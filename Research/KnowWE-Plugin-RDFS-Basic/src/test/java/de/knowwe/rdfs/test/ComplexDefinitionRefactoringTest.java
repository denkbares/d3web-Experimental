package de.knowwe.rdfs.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.TestArticleManager;
import utils.TestUtils;
import de.d3web.plugin.test.InitPluginManager;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.rdf2go.Rdf2GoCore;

public class ComplexDefinitionRefactoringTest {

	private static final String AUTHOR_OF_EXAMPLE = "authorOf-Example";
	/* Just for convenience */
	private final Environment environment = Environment.getInstance();
	private final ArticleManager articleManager = environment.getArticleManager(Environment.DEFAULT_WEB);

	private final ReferenceManager manager = IncrementalCompiler.getInstance().getTerminology();
	private final Rdf2GoCore core = Rdf2GoCore.getInstance();

	private static final String TESTFILE = "src/test/resources/"
			+ "authorOf-Example.txt";
	private static final String TESTFILE2 = "src/test/resources/"
			+ "authorOf-Example2.txt";

	@BeforeClass
	public static void setUp() throws IOException {
		InitPluginManager.init();
		PackageManager.overrideAutocompileArticle(true);

		// System.out.println(IncrementalCompiler.getInstance().getTerminology().toString());

		TestArticleManager.getArticle(TESTFILE);
	}

	@Test
	public void testRefactoringComplexDef() throws IOException {
		/* Change text and test */
		String oldText = TestUtils.readTxtFile(TESTFILE);
		String newText = TestUtils.readTxtFile(TESTFILE2);
		// change definition of Author and its reference in authorOf's complex
		// def
		changeText(oldText, newText, Type.class);

		// test whether hasAuthor is still alive and running and has not been
		// killed by refactoring
		assertTrue(IncrementalCompiler.getInstance().getTerminology().isValid("hasAuthor"));

		// check store and query engine
		assertTrue(core.sparqlAsk("ASK { ?x ?y ?z . }"));

		// check compiled data
		assertTrue(core.sparqlAsk("ASK { lns:hasAuthor owl:inverseOf lns:authorOff . }"));
	}

	@AfterClass
	public static void tearDown() {
		// Remove the statements created in the test to avoid problems
		Article article = TestArticleManager.getArticle(TESTFILE);
		Rdf2GoCore.getInstance().removeArticleStatementsRecursive(article);
		Rdf2GoCore.getInstance().removeAllCachedStatements();
		// Finally remove the formerly created article
		TestArticleManager.clear();
	}

	private <T extends Type> void changeText(String oldText, String newText,
			Class<T> sectionType) throws IOException {
		Section<T> oldSection = findSectionWithText(oldText, sectionType);
		if (oldSection != null) {
			Map<String, String> nodesMap = new HashMap<String, String>();
			nodesMap.put(oldSection.getID(), newText);
			Sections.replaceSections(TestUtils.createTestActionContext("",
					""), nodesMap);
		}
		else {
			Logger.getLogger(getClass()).fatal("Unable to get section with text: " +
					oldText);
		}
	}

	private <T extends Type> Section<T> findSectionWithText(String text, Class<T>
			sectionType) {
		Section<?> root = getArticle().getRootSection();
		List<Section<T>> typedSections = new LinkedList<Section<T>>();
		Sections.findSuccessorsOfType(root, sectionType, typedSections);
		for (Section<T> section : typedSections) {
			if (section.getText().equals(text)) {
				return section;
			}
		}
		return null;
	}

	private Article getArticle() {
		return articleManager.getArticle(AUTHOR_OF_EXAMPLE);
	}

}
