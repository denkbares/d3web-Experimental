
<%@page import="bibtex.dom.BibtexEntry"%>
<%@page import="de.d3web.we.kdom.bibtex.BibTexContent"%>
<%@page import="de.d3web.we.utils.KnowWEUtils"%>
<%@page import="bibtex.dom.BibtexFile"%>
<%@page import="de.d3web.we.kdom.Section"%>
<%@page import="de.d3web.we.kdom.KnowWEArticle"%><%@page
	import="de.d3web.we.core.*"%><%@ page import="com.ecyrd.jspwiki.*"%>
<%@ page import="java.util.*,java.io.*"%>
<%!String findParam(PageContext ctx, String key) {
		ServletRequest req = ctx.getRequest();
		String val = req.getParameter(key);
		if (val == null) {
			val = (String) ctx.findAttribute(key);
		}
		return val;
	}%>
<%
	//String value = 
	String filename = findParam(pageContext, "filename");
	;
	String nodeID = findParam(pageContext, "nodeID");
	String bibID = findParam(pageContext, "bibID");
	String topic = findParam(pageContext, KnowWEAttributes.TOPIC);
	KnowWEArticle art = KnowWEEnvironment.getInstance()
			.getArticleManager("default_web").getArticle(topic);
	String data = "no Article found" + topic;
	if (art != null) {
		Section sec = art.getNode(nodeID);
		BibtexFile bt = (BibtexFile) KnowWEUtils.getStoredObject(sec,
				BibTexContent.BIBTEXs);
		if (bibID == null) {
			data = bt.toString();
		} else {
			boolean found = false;
			for (BibtexEntry cur : (List<BibtexEntry>) bt.getEntries()) {
				if (cur.getEntryKey().equals(bibID)) {
					data = cur.toString();					
					found = true;
					break;
				}
			}
			if (!found) {
				data = "bibID " + bibID + " not found";
			}
		}
	}

	//set the content type(can be excel/word/powerpoint etc..)
	response.setContentType("application/txt");
	//set the header and also the Name by which user will be prompted to save
	response.setHeader("Content-Disposition", "attachment;filename=\""
			+ filename + "\"");

	//get the file name
	//OPen an input stream to the file and post the file contents thru the 
	//servlet output stream to the client m/c
	if (data != null) {
		StringBuffer StringBuffer1 = new StringBuffer(data);
		ByteArrayInputStream Bis1 = new ByteArrayInputStream(
				StringBuffer1.toString().getBytes("UTF-8"));

		InputStream in = Bis1;
		ServletOutputStream outs = response.getOutputStream();

		int bit = 256;

		try {
			while ((bit) >= 0) {
				bit = in.read();
				outs.write(bit);
			}
			//System.out.println("" +bit);

		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}
		//		System.out.println( "\n" + i + " byt
		//     es sent.");
		//		System.out.println( "\n" + f.length(
		//     ) + " bytes sent.");
		outs.flush();
		outs.close();
		in.close();
	}
%>