package de.d3web.proket.d3web.input.officexmlParser;

//import de.d3web.proket.d3web.input.officexmlParser.toolkit.*;
import de.uniwue.abstracttools.StringUtils;
import de.uniwue.abstracttools.TreeNode;
import de.uniwue.abstracttools.xml.XmlElement;
import de.uniwue.abstracttools.xml.XmlToTreeReader;
/*import de.uniwue.abstracttools.xml.XmlTree;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


import officeconverter.Config; 
import officeconverter.Converter;

public class WordToProketConverter {

	private static final String INPUT = "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/RNS_NEW.doc";
	private static final String OUTPUT = "/Users/mafre/Promotion/Projects/2012JuriSearch/WordParsing/RNS_newparsed.xml";
	
	private static final String HTML_FILE_PREFIX = "html_convert_";
	private static final String KB_DEF_START_TAG = "wbstart";
	
	public static void convert(String inFile, String outFile) throws ParserConfigurationException, SAXException, IOException {
		String htmlFile = getHtmlFileName(inFile);
		Config conf = new Config().setEmbeddedObjectsDir(extractPath(inFile)).setWithLists(true).setLeaveUmlauts(true).setWithHeadlineDepths(true);
		Converter.convertFile2File(new File(inFile), new File(htmlFile), conf);
		removeIndexPart(htmlFile);
		writeHTMLHeaderFooter(htmlFile);
		
		XmlTree t = XmlToTreeReader.read(htmlFile);
		StringUtils.writefileString(htmlFile, t.toString());
		XmlElement root = t.getRootElement();
		removeErlaeuterung(root);
		
		XmlTree tn = buildEmptyTree();

		root = root.getRelativeElements("body").getFirst().getChildren().getFirst();
		buildHierarchy(root, tn.getRootElement(), 0); 
		
		String proketText = buildProket(tn);		
		StringUtils.writefileString(outFile, proketText);
	}
	
	
	private static String cleanProketText(String s) {
		s = s.replaceAll("ä", "&#228;");
		s = s.replaceAll("ü", "&#252;");
		s = s.replaceAll("ö", "&#246;");
		s = s.replaceAll("Ü", "&#220;");
		s = s.replaceAll("ß", "&#223;");
        s = s.replaceAll("Ä", "&#196;");
        s = s.replaceAll("Ö", "&#214;");
		s = s.replaceAll("§", "&#167;");
		s = s.replaceAll("\"", "&#8220;");
		s = s.replaceAll("\"", "&#8222;");
		s = s.replaceAll("\\.\\.\\.", "&#8230;");
		s = s.replaceAll("<p>", "");
		s = s.replaceAll("</p>", "");
		s = s.replaceAll("<", "&#60;");
		s = s.replaceAll(">", "&#62;");
		return s;
	}
	
	
	
	private static XmlTree buildEmptyTree() {
		XmlTree hTree = new XmlTree();
		TreeNode rootNode = new TreeNode("", "root");
		XmlElement hRoot = new XmlElement("", "", "root", new AttributesImpl(), rootNode, "");
		rootNode.setValue(hRoot);
		hTree.insertNode(rootNode);
		return hTree;
	}
	
	private static void removeIndexPart(String htmlFile) throws IOException {
		String c = StringUtils.readFileString(htmlFile);
		int i = c.indexOf(KB_DEF_START_TAG);
		int j = c.indexOf('<', i);
		int k = c.indexOf(">",j);
		c = c.substring(k+1);
		StringUtils.writefileString(htmlFile, c);
	}
	
	private static void writeHTMLHeaderFooter(String htmlFile) throws IOException {
		String c = StringUtils.readFileString(htmlFile);
		c = "<html><body>" + c + "</body></html>";
		StringUtils.writefileString(htmlFile, c);
	}
	
	
	private static String getHtmlFileName(String inFile) {
		File inF = new File(inFile);
		String dir = extractPath(inFile);
		String inNameNoPath = inF.getName();
		int i = inNameNoPath.lastIndexOf('.');
		if (i == -1) i = inNameNoPath.length();
		if (dir.endsWith("/")) dir = dir.substring(0, dir.length()-1);
		if (dir.endsWith("\\")) dir = dir.substring(0, dir.length()-1);
		return  dir + "/" + HTML_FILE_PREFIX + inNameNoPath.substring(0, i) + ".html";
	}
	
	
	private static String extractPath(String fileName) {
		File inF = new File(fileName);
		
		return inF.getAbsolutePath().substring(0, inF.getAbsolutePath().length()-inF.getName().length());
	}
	
	
	private static void removeErlaeuterung(XmlElement e) {
		LinkedList<XmlElement> l = new LinkedList<XmlElement>(); 
		for (XmlElement c : e.getChildren()) {
			String d = c.getPCDATA().trim();
			if (d.startsWith("Erläuterung:")) l.add(c);
			else removeErlaeuterung(c);
		}
		for (XmlElement c : l) e.removeChild(c);
	}
	
	
	private static XmlElement buildHierarchy(XmlElement source, XmlElement dest, int destLv) {
		while (source != null) {
			int lv = getQuestionLevel(source, destLv);
			
			while (lv == -1) {
				XmlElement next = source.getLastSuccessorInDepthFirstOrder().getNextInDepthFirstOrder();
				dest.addChild(source);
				source = next;
				lv = getQuestionLevel(source, destLv);
			}
			if (lv <= destLv) return source;
			if (lv == destLv + 1) {
				if (dest.getQName().equals("description")) dest = dest.getParent();
				XmlElement next = source.getLastSuccessorInDepthFirstOrder().getNextInDepthFirstOrder();
				XmlElement ne = new XmlElement("", "", "question", new AttributesImpl(), "");
				dest.addChild(ne);
				
				XmlElement neContent = new XmlElement("", "", "qText", new AttributesImpl(), "");
				if (source.getChild() == null) {
					neContent.setPCDATA(source.getPCDATA());
				} else {
					neContent.addChild(source.getChild());
				}
				ne.addChild(neContent);
				XmlElement neErl = new XmlElement("", "", "description", new AttributesImpl(), "");
				ne.addChild(neErl);
				
				source = buildHierarchy(next, neErl, destLv + 1);
			}
			if (lv > destLv + 1) {
				String tag = source.getQName().toLowerCase().trim();
				throw new RuntimeException("Element with tag <" + tag + "> has wrong level with respect to level " + destLv);
			}
		}
		return null;
	}
	
	
	private static int getQuestionLevel(XmlElement source, int actLv) {	
		if (source == null) return -2;
		String tag = source.getQName().toLowerCase().trim();
		if ((tag.startsWith("h")) && (!"html".equals(tag))) {
			String n = tag.substring(1);
			return Integer.parseInt(n);
		} else {
			String d = source.getPCDATA().trim();
			int c = 0;
			while (d.startsWith("-")) {
				c++;
				d = d.substring(1);
			}
			if (c==0) return -1;
			return actLv + c;
		}
	}
	
	
	private static String buildProket(XmlTree t) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		sb.append("<dialog sub-type='front' type='legal' css='legal, nofoot' header='Name of Wiki' and-or-type='AND' id='dialog1'>");
		XmlElement root = t.getRootElement().getRelativeElements("question").getFirst();
		buildQuestionHierarchy(root, sb, 0, 0);
		sb.append("</dialog>");
		return sb.toString();
	}
	
	private static String remove(String text, String toRemove) {
		int i = text.indexOf(toRemove);
		if (i != -1) {
			if (text.length() - 4 > i)
				text = text.substring(0,i) + text.substring(i + 4);
			else 
				text = text.substring(0,i);
		}
		return text;
	}
	
	
	private static String buildQuestionText(String input) {
		String s = "";
		int i = input.indexOf("[jn]");
		if (i != -1) {
			if (input.length() - 4 > i)
				input = input.substring(0,i) + input.substring(i + 4);
			else 
				input = input.substring(0,i);
		}
		int j = input.indexOf("[oder]");
		if (j != -1) {
			if (input.length() - 6 > j)
				input = input.substring(0,j) + input.substring(j + 6);
			else 
				input = input.substring(0,j);
			s = s + " and-or-type='OR'";
		} else {
			s = s + " and-or-type='AND'";
		}
		int k = input.indexOf("[dummy]");
		if (k != -1) {
			if (input.length() - 7 > k)
				input = input.substring(0,k) + input.substring(k + 7);
			else 
				input = input.substring(0,k);
			s = s + " dummy='true'";
		}
		int l = input.indexOf("[nein]");
		if (l != -1) {
			if (input.length() - 6 > l)
				input = input.substring(0,l) + input.substring(l + 6);
			else 
				input = input.substring(0,l);
			s = s + " defining='nein'";
		}
		return "title='" + input + "'" + s;
	}
	
	private static int buildQuestionHierarchy(XmlElement e, StringBuilder sb, int parentId, int actId) {
		int thisId = actId++;
		String tag = e.getQName().toLowerCase();
		if ("question".equals(tag)) {
			String qText = getQuestionText(e);
			String erl = getDescription(e);
			String parent = "";
			if (parentId != -1) parent = " parent-id='" + parentId + "'";
			
			String s = cleanProketText(qText + " id='" + thisId + "'" + parent + " bonus-text='" + erl);  
					
			sb.append("<legalQuestion "+ s + "'/>\n");
			for (XmlElement child : e.getRelativeElements("question")) {
				actId = buildQuestionHierarchy(child, sb, thisId, actId);
			} 
			return actId;
		} else throw new RuntimeException("Question tag expected.");
	}
	
	private static String getQuestionText(XmlElement e) {
		XmlElement q = e.getRelativeElements("qText").getFirst();
		String s = q.getPCDATAOfSubTree();
		return buildQuestionText(s);
	}
	
	private static String getDescription(XmlElement e) {
		e = e.getRelativeElements("description").getFirst();
		return e.getXml(false);
	}
	
	
	
	public static void main(String[] args) {
		try {
			WordToProketConverter.convert(INPUT, OUTPUT);
		} catch (Exception exc) {
			exc.printStackTrace(System.err);
		}
	}
	
	
	
}
*/