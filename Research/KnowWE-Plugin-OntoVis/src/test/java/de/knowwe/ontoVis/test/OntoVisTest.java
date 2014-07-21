/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

package de.knowwe.ontoVis.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;

import de.d3web.plugin.test.InitPluginManager;
import de.d3web.strings.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.RuleSet;
import de.knowwe.rdf2go.modelfactory.OWLIMLiteModelFactory;
import de.knowwe.rdfs.vis.OntoGraphDataBuilder;

import static org.junit.Assert.assertEquals;

/**
 * @author Johanna Latt
 * @created 19.07.2014.
 */
public class OntoVisTest {

	static Rdf2GoCore rdfRepository = null;

	@BeforeClass
	public static void setUp() throws IOException {
		InitPluginManager.init();

		RDF2Go.register(new OWLIMLiteModelFactory(RuleSet.OWL2_RL_REDUCED_OPTIMIZED));
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();
		rdfRepository = new Rdf2GoCore("http://localhost:8080/KnowWE/Wiki.jsp?page=",
				"http://ki.informatik.uni-wuerzburg.de/d3web/we/knowwe.owl#", model, RuleSet.OWL2_RL_REDUCED_OPTIMIZED);
		// rdfRepository.addNamespace("ns", bns);
		// rdfRepository.addNamespace(LNS_ABBREVIATION, lns);
		rdfRepository.addNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		rdfRepository.addNamespace("owl", "http://www.w3.org/2002/07/owl#");
		rdfRepository.addNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		rdfRepository.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
		rdfRepository.addNamespace("si", "http://www.example.org/ontology#");

		File ontologyFile = new File("src/test/resources/simpsons-ontology.xml");
		InputStream in = new FileInputStream(ontologyFile);
		rdfRepository.readFrom(in);
	}

	@Test
	public void test_Instances() {
		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(OntoGraphDataBuilder.CONCEPT, "si:bart");

		parameterMap.put(OntoGraphDataBuilder.FORMAT, "svg");

		parameterMap.put(OntoGraphDataBuilder.FILTERED_RELATIONS, "si:sibling, si:child");

		parameterMap.put(OntoGraphDataBuilder.GRAPH_SIZE, "750");

		parameterMap.put(OntoGraphDataBuilder.RANK_DIRECTION, "TB");

		parameterMap.put(OntoGraphDataBuilder.SHOW_OUTGOING_EDGES, "true");

		parameterMap.put(OntoGraphDataBuilder.USE_LABELS, "true");

		String colorCodes = "";
		colorCodes += "si:sibling: #511F7A;";
		colorCodes += "si:child: #398743;";

		parameterMap.put(OntoGraphDataBuilder.RELATION_COLOR_CODES, colorCodes);

		parameterMap.put(OntoGraphDataBuilder.REQUESTED_DEPTH, "1");
		parameterMap.put(OntoGraphDataBuilder.REQUESTED_HEIGHT, "1");
		OntoGraphDataBuilder ontoGraphDataBuilder = new OntoGraphDataBuilder("", null,
				parameterMap,
				new DummyLinkToTermDefinitionProvider(), rdfRepository);

		ontoGraphDataBuilder.createData();

		String generatedSource = ontoGraphDataBuilder.getSource();
		String expectedSource = null;
		try {
			expectedSource = Strings.readFile(new File("src/test/resources/graph-Bart.dot"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// the expressions do not have constant order within the dot-code
		// therefore we need some fuzzy-compare

		assertEquals(
				"Length of generated dot-source does not match length of expected dot-source.",
				String.valueOf(expectedSource).length(),
				String.valueOf(generatedSource).length());
		List<Byte> expectedBytes = asSortedByteList(expectedSource);
		List<Byte> generatedBytes = asSortedByteList(generatedSource);

		assertEquals(
				"Generated dot-source does not match (sorted-bytes) expected dot-source.",
				expectedBytes, generatedBytes);
	}

	@Test
	public void test_Classes() {
		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(OntoGraphDataBuilder.CONCEPT, "si:Human");

		parameterMap.put(OntoGraphDataBuilder.FORMAT, "png");

		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_NODES, "owl:Nothing");

		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_RELATIONS, "rdf:first, owl:equivalentClass, rdf:type, owl:assertionProperty, owl:sameAs");

		parameterMap.put(OntoGraphDataBuilder.SHOW_OUTGOING_EDGES, "false");

		parameterMap.put(OntoGraphDataBuilder.USE_LABELS, "true");

		parameterMap.put(OntoGraphDataBuilder.LANGUAGE, "en");

		String colorCodes = "";
		colorCodes += "rdfs:subClassOf: #19F193;";

		parameterMap.put(OntoGraphDataBuilder.RELATION_COLOR_CODES, colorCodes);

		parameterMap.put(OntoGraphDataBuilder.REQUESTED_HEIGHT, "3");

		OntoGraphDataBuilder ontoGraphDataBuilder = new OntoGraphDataBuilder("", null,
				parameterMap,
				new DummyLinkToTermDefinitionProvider(), rdfRepository);

		ontoGraphDataBuilder.createData();

		String generatedSource = ontoGraphDataBuilder.getSource();
		String expectedSource = null;
		try {
			expectedSource = Strings.readFile(new File("src/test/resources/graph-Human.dot"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// the expressions do not have constant order within the dot-code
		// therefore we need some fuzzy-compare

		assertEquals(
				"Length of generated dot-source does not match length of expected dot-source.",
				String.valueOf(expectedSource).length(),
				String.valueOf(generatedSource).length());
		List<Byte> expectedBytes = asSortedByteList(expectedSource);
		List<Byte> generatedBytes = asSortedByteList(generatedSource);

		assertEquals(
				"Generated dot-source does not match (sorted-bytes) expected dot-source.",
				expectedBytes, generatedBytes);
	}

	@Test
	public void test_Properties() {
		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(OntoGraphDataBuilder.CONCEPT, "si:child");

		parameterMap.put(OntoGraphDataBuilder.FORMAT, "svg");

		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_RELATIONS, "rdf:type");

		parameterMap.put(OntoGraphDataBuilder.SHOW_OUTGOING_EDGES, "false");

		parameterMap.put(OntoGraphDataBuilder.USE_LABELS, "true");

		String colorCodes = "";
		colorCodes += "si:child: #398743;";
		colorCodes += "si:parent si:color #123A56;";
		colorCodes += "si:relatedWith si:color #987F65;";

		parameterMap.put(OntoGraphDataBuilder.RELATION_COLOR_CODES, colorCodes);

		parameterMap.put(OntoGraphDataBuilder.REQUESTED_HEIGHT, "3");

		OntoGraphDataBuilder ontoGraphDataBuilder = new OntoGraphDataBuilder("", null,
				parameterMap,
				new DummyLinkToTermDefinitionProvider(), rdfRepository);

		ontoGraphDataBuilder.createData();

		String generatedSource = ontoGraphDataBuilder.getSource();
		String expectedSource = null;
		try {
			expectedSource = Strings.readFile(new File("src/test/resources/graph-Child.dot"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// the expressions do not have constant order within the dot-code
		// therefore we need some fuzzy-compare

		assertEquals(
				"Length of generated dot-source does not match length of expected dot-source.",
				String.valueOf(expectedSource).length(),
				String.valueOf(generatedSource).length());
		List<Byte> expectedBytes = asSortedByteList(expectedSource);
		List<Byte> generatedBytes = asSortedByteList(generatedSource);

		assertEquals(
				"Generated dot-source does not match (sorted-bytes) expected dot-source.",
				expectedBytes, generatedBytes);
	}

	@Test
	public void test_Table() {
		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(OntoGraphDataBuilder.CONCEPT, "si:lisa");

		parameterMap.put(OntoGraphDataBuilder.FORMAT, "svg");

		parameterMap.put(OntoGraphDataBuilder.FILTERED_RELATIONS, "si:age, rdfs:label, si:child");

		parameterMap.put(OntoGraphDataBuilder.SHOW_OUTGOING_EDGES, "false");

		parameterMap.put(OntoGraphDataBuilder.USE_LABELS, "false");

		OntoGraphDataBuilder ontoGraphDataBuilder = new OntoGraphDataBuilder("", null,
				parameterMap,
				new DummyLinkToTermDefinitionProvider(), rdfRepository);

		ontoGraphDataBuilder.createData();

		String generatedSource = ontoGraphDataBuilder.getSource();
		String expectedSource = null;
		try {
			expectedSource = Strings.readFile(new File("src/test/resources/graph-Table.dot"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// the expressions do not have constant order within the dot-code
		// therefore we need some fuzzy-compare

		assertEquals(
				"Length of generated dot-source does not match length of expected dot-source.",
				String.valueOf(expectedSource).length(),
				String.valueOf(generatedSource).length());
		List<Byte> expectedBytes = asSortedByteList(expectedSource);
		List<Byte> generatedBytes = asSortedByteList(generatedSource);

		assertEquals(
				"Generated dot-source does not match (sorted-bytes) expected dot-source.",
				expectedBytes, generatedBytes);
	}

	private List<Byte> asSortedByteList(String expectedSource) {
		byte[] bytes = expectedSource.getBytes();
		Byte[] Bytes = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			Bytes[i] = bytes[i];
		}
		List<Byte> list = Arrays.asList(Bytes);
		Collections.sort(list);
		return list;
	}

}
