package de.knowwe.ontoVis.test;

import de.d3web.plugin.test.InitPluginManager;
import de.d3web.strings.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.vis.OntoGraphDataBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/*
 * Copyright (C) 2013 denkbares GmbH
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

/**
 * 
 * @author jochenreutelshofer
 * @created 23.05.2013
 */
public class DotRendererTest {

	static Rdf2GoCore rdfRepository = null;

	@BeforeClass
	public static void setUp() throws IOException {
		InitPluginManager.init();

		RDF2Go.register(new de.knowwe.rdf2go.modelfactory.SesameSwiftOwlimModelFactory());
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();
		rdfRepository = new Rdf2GoCore("http://localhost:8080/KnowWE/Wiki.jsp?page=",
				"http://ki.informatik.uni-wuerzburg.de/d3web/we/knowwe.owl#", model);
		// rdfRepository.addNamespace("ns", bns);
		// rdfRepository.addNamespace(LNS_ABBREVIATION, lns);
		rdfRepository.addNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		rdfRepository.addNamespace("w", "http://www.umweltbundesamt.de/wisec#");
		rdfRepository.addNamespace("owl", "http://www.w3.org/2002/07/owl#");
		rdfRepository.addNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		rdfRepository.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");

		File ontologyFile = new File("src/test/resources/wissass-ontology.xml");
		InputStream in = new FileInputStream(ontologyFile);
		rdfRepository.readFrom(in);

	}

	@Test
	public void testA() {
		Map<String, String> parameterMap = new HashMap<String, String>();
		String format = "svg";
		parameterMap.put(OntoGraphDataBuilder.FORMAT, format);

		parameterMap.put(OntoGraphDataBuilder.CONCEPT, "OP-Methoden");

		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_RELATIONS, "label,owl:sameAs");

		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_NODES, "rdfs:Resource,WissassConcept");

		parameterMap.put(OntoGraphDataBuilder.GRAPH_SIZE, "690");

		parameterMap.put(OntoGraphDataBuilder.RANK_DIRECTION, "RL");

		parameterMap.put(OntoGraphDataBuilder.LINK_MODE, OntoGraphDataBuilder.LINK_MODE_BROWSE);

		parameterMap.put(OntoGraphDataBuilder.SHOW_OUTGOING_EDGES, "false");

		parameterMap.put(OntoGraphDataBuilder.SHOW_CLASSES, "false");

		parameterMap.put(OntoGraphDataBuilder.SHOW_SCROLLBAR, "false");

		String colorCodes = "";
		colorCodes += "kann: #009900;"; // green
		colorCodes += "muss: red;";
		colorCodes += "temporalGraph: #FFCC00;"; // dark yellow
		colorCodes += "assoziation: blue;";

		parameterMap.put(OntoGraphDataBuilder.RELATION_COLOR_CODES, colorCodes);

		parameterMap.put(OntoGraphDataBuilder.REQUESTED_DEPTH, "1");
		parameterMap.put(OntoGraphDataBuilder.REQUESTED_HEIGHT, "1");
		OntoGraphDataBuilder OntoGraphDataBuilder = new OntoGraphDataBuilder("", null,
				parameterMap,
				new DummyLinkToTermDefinitionProvider(), rdfRepository);

		OntoGraphDataBuilder.createData();

		String generatedSource = OntoGraphDataBuilder.getSource();
		String expectedSource = null;
		try {
			expectedSource = Strings.readFile(new File("src/test/resources/graph-OP-Methoden.dot"));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
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
	public void testB() {
		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(OntoGraphDataBuilder.CONCEPT, "Phakoemulsifikation");

		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_RELATIONS, "rdfs:subClassOf,owl:sameAs");

		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_NODES, "rdfs:Resource,WissassConcept,Class,owl:Thing");

		parameterMap.put(OntoGraphDataBuilder.REQUESTED_DEPTH, "2");
		parameterMap.put(OntoGraphDataBuilder.REQUESTED_HEIGHT, "2");

		OntoGraphDataBuilder OntoGraphDataBuilder = new OntoGraphDataBuilder("", null,
				parameterMap,
				new DummyLinkToTermDefinitionProvider(), rdfRepository);

		OntoGraphDataBuilder.createData();

		String generatedSource = OntoGraphDataBuilder.getSource();
		String expectedSource = null;
		try {
			expectedSource = Strings.readFile(new File("src/test/resources/graph-Phako.dot"));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
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
