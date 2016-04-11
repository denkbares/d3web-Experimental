package de.knowwe.defi.feedback;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

public class FeedbackFillAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String path = Environment.getInstance().getWikiConnector().getSavePath();
		String filename = context.getUserName().toLowerCase() + "_feedback.xml";
		StringBuilder answers = new StringBuilder();

		try {
			DocumentBuilderFactory dbf =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new FileInputStream(path + "/" + filename));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("answer");

			Element knoten;
			for (int i = 0; i < nodes.getLength(); i++) {
				knoten = (Element) nodes.item(i);
				answers.append(knoten.getAttribute("id") + "=#=" + knoten.getTextContent() + "=@=");
			}
			answers.setLength(answers.length() - 3);
		}
		catch (Exception e) {
		}


		HttpServletResponse response = context.getResponse();
		response.getWriter().write(answers.toString());
	}

}
