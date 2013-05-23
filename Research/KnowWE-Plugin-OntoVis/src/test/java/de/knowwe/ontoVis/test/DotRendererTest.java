package de.knowwe.ontoVis.test;

import static org.junit.Assert.assertEquals;

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
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.vis.RenderingCore;

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
		PackageManager.overrideAutocompileArticle(true);

		RDF2Go.register(new de.knowwe.rdf2go.modelfactory.SesameSwiftOwlimModelFactory());
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();
		rdfRepository = new Rdf2GoCore("http://localhost:8080/KnowWE/Wiki.jsp?page=", model);
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
		parameterMap.put(RenderingCore.FORMAT, format);

		parameterMap.put(RenderingCore.CONCEPT, "OP-Methoden");

		parameterMap.put(RenderingCore.EXCLUDED_RELATIONS, "label");

		parameterMap.put(RenderingCore.EXCLUDED_NODES, "Resource,WissassConcept");

		parameterMap.put(RenderingCore.GRAPH_SIZE, "690");

		parameterMap.put(RenderingCore.RANK_DIRECTION, "RL");

		parameterMap.put(RenderingCore.LINK_MODE, RenderingCore.LINK_MODE_BROWSE);

		parameterMap.put(RenderingCore.SHOW_OUTGOING_EDGES, "false");

		parameterMap.put(RenderingCore.SHOW_CLASSES, "false");

		parameterMap.put(RenderingCore.SHOW_SCROLLBAR, "false");

		String colorCodes = "";
		colorCodes += "kann: #009900;"; // green
		colorCodes += "muss: red;";
		colorCodes += "temporalGraph: #FFCC00;"; // dark yellow
		colorCodes += "assoziation: blue;";

		parameterMap.put(RenderingCore.RELATION_COLOR_CODES, colorCodes);

		parameterMap.put(RenderingCore.REQUESTED_DEPTH, "1");
		parameterMap.put(RenderingCore.REQUESTED_HEIGHT, "1");
		RenderingCore renderingCore = new RenderingCore("", null, parameterMap,
				new DummyLinkToTermDefinitionProvider(), rdfRepository);

		renderingCore.createDotSource();

		String generatedSource = renderingCore.getDotSource();
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
				"Length of generated dot-source does not match lenght of expected dot-source.",
				expectedSource.length(),
				generatedSource.length());
		List<Byte> expectedBytes = asSortedByteList(expectedSource);
		List<Byte> generatedBytes = asSortedByteList(generatedSource);

		assertEquals(
				"Generated dot-source does not match (sorted-bytes) expected dot-source.",
				expectedBytes, generatedBytes);
	}

	@Test
	public void testB() {
		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(RenderingCore.CONCEPT, "Phakoemulsifikation");

		parameterMap.put(RenderingCore.EXCLUDED_RELATIONS, "subClassOf");

		parameterMap.put(RenderingCore.EXCLUDED_NODES, "Resource,WissassConcept,Class");

		parameterMap.put(RenderingCore.REQUESTED_DEPTH, "2");
		parameterMap.put(RenderingCore.REQUESTED_HEIGHT, "2");

		RenderingCore renderingCore = new RenderingCore("", null, parameterMap,
				new DummyLinkToTermDefinitionProvider(), rdfRepository);

		renderingCore.createDotSource();

		String generatedSource = renderingCore.getDotSource();
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
				"Length of generated dot-source does not match lenght of expected dot-source.",
				expectedSource.length(),
				generatedSource.length());
		List<Byte> expectedBytes = asSortedByteList(expectedSource);
		List<Byte> generatedBytes = asSortedByteList(generatedSource);

		assertEquals(
				"Generated dot-source does not match (sorted-bytes) expected dot-source.",
				expectedBytes, generatedBytes);
	}

	/**
	 * 
	 * @created 23.05.2013
	 * @param expectedSource
	 * @return
	 */
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

	public enum Rdf2GoModel {
		JENA, BIGOWLIM, SESAME, SWIFTOWLIM
	}

	public enum Rdf2GoReasoning {
		RDF, RDFS, OWL
	}

}
