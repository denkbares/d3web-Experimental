/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.d3web.proket.oldstore;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.d3web.core.knowledge.Resource;
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webXMLParser;

/**
 * Servlet for creating and using dialogs with d3web binding. Binding is more of
 * a loose binding: if no d3web etc session exists, a new d3web session is
 * created and knowledge base and specs are read from the corresponding XML
 * specfication.
 * 
 * Basically, when the user selects answers in the dialog, those are transferred
 * back via AJAX calls and processed by this servlet. Here, values are
 * propagated to the d3web session (and later re-read by the renderers).
 * 
 * Both browser refresh and pressing the "new case"/"neuer Fall" Button in the
 * dialog leads to the creation of a new d3web session, i.e. all values set so
 * far are discarded, and an "empty" problem solving session begins.
 * 
 * @author Martina Freiberg
 * 
 * @date 14.01.2011; Update: 28/01/2011
 * 
 */
public class ImageQuestions extends HttpServlet {

	/* special parser for reading in the d3web-specification xml */
	private D3webXMLParser d3webParser;

	/* current d3web session */
	private Session d3webSession;

	/* d3web connector for storing certain relevant properties */
	private D3webConnector d3wcon;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageQuestions() {
		super();
	}

	/**
	 * Basic initialization and servlet method.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String source = "ImageQuestions";
		if (!source.endsWith(".xml")) {
			source = source + ".xml";
		}

		// d3web parser for interpreting the source/specification xml
		d3webParser = new D3webXMLParser();
                d3webParser.setSourceToParse(source);

		d3wcon = D3webConnector.getInstance();

		// only invoke parser, if XML hasn't been parsed before
		// if it has, a knowledge base already exists
		if (d3wcon.getKb() == null) {
			d3wcon.setKb(d3webParser.getKnowledgeBase());
			d3wcon.setKbName(d3webParser.getKnowledgeBaseName());
			d3wcon.setDialogStrat(d3webParser.getStrategy());
			d3wcon.setDialogType(d3webParser.getType());
			d3wcon.setDialogColumns(d3webParser.getDialogColumns());
			d3wcon.setQuestionnaireColumns(d3webParser.getQuestionnaireColumns());
			d3wcon.setCss(d3webParser.getCss());
			d3wcon.setHeader(d3webParser.getHeader());
			d3wcon.setUIprefix(d3webParser.getUIPrefix());
			}

		// Resource r = ImageHandler.getResource("example.jpg");
		// Resource r = ImageHandler.getResource(picsrc);

		// show(request, response, r);
		return;

	}

	/**
	 * Basic servlet method for displaying the picture servlet.
	 * 
	 * @created 28.01.2011
	 * @param request
	 * @param response
	 * @param Resource
	 * @throws IOException
	 */
	private void show(HttpServletRequest request, HttpServletResponse response,
			Resource r) throws IOException {

		response.setContentType("image/jpeg");
		Image image = null;

		InputStream in = r.getInputStream();
		try {
			/*
			 * BufferedImage bi = ImageIO.read(in); System.out.println(bi);
			 * 
			 * Iterator writers = ImageIO.getImageWritersByFormatName("jpg");
			 * ImageWriter writer = (ImageWriter) writers.next();
			 * ImageOutputStream ios =
			 * ImageIO.createImageOutputStream(response.getOutputStream());
			 * writer.setOutput(ios);
			 */

			image = ImageIO.read(in);
			BufferedImage bui = toBufferedImage(image);
			//System.out.println(bui);
			ImageIO.write(bui, "jpg", new File("/mymage.jpg"));

			// works...
			byte[] buf = new byte[1024];
			int len;
			ServletOutputStream os = response.getOutputStream();
			while ((len = in.read(buf)) != -1) {
				os.write(buf, 0, len);
			}

			os.close();

		}
		finally {
			in.close();
		}
	}

	public BufferedImage toBufferedImage(final Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		// if (image instanceof VolatileImage)
		// return ((VolatileImage) image).getSnapshot();
		// loadImage(image);
		final BufferedImage buffImg = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_CUSTOM);
		final Graphics2D g2 = buffImg.createGraphics();
		g2.drawImage(image, null, null);
		g2.dispose();
		return buffImg;
	}
}