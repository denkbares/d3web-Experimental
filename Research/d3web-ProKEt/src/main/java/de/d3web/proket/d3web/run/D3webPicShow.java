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
package de.d3web.proket.d3web.run;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.d3web.core.knowledge.Resource;
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webXMLParser;
import de.d3web.proket.d3web.output.render.ImageHandler;

/**
 * * Servlet for displaying a picture. Therefore, the picture is created out of
 * several parameters and displayed via the tailored output stream. It is
 * included in the HTML by having sth like <img>D3webPicShow</img> and a URL
 * mapping in the web.xml that maps this servlet to the URL pattern
 * /D3webPicShow.
 * 
 * @author Martina Freiberg
 * 
 * @date 08.02.2011
 */
public class D3webPicShow extends HttpServlet {

	/* special parser for reading in the d3web-specification xml */
	private D3webXMLParser d3webParser;

	/* current d3web session */
	private Session d3webSession;

	/* d3web connector for storing certain relevant properties */
	private D3webConnector d3wcon;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public D3webPicShow() {
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

		d3wcon = D3webConnector.getInstance();

		String picsrc = request.getParameter("src");

		Resource r = ImageHandler.getResource("Fig7.jpg");
		// Resource r = ImageHandler.getResource(picsrc);

		show(request, response, r);
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
		System.out.println(r.getPathName());

		InputStream in = r.getInputStream();
		System.out.println(in.toString());
		/*
		 * BufferedImage bi = ImageIO.read(in); System.out.println(bi);
		 * 
		 * Iterator writers = ImageIO.getImageWritersByFormatName("jpg");
		 * ImageWriter writer = (ImageWriter) writers.next(); ImageOutputStream
		 * ios = ImageIO.createImageOutputStream(response.getOutputStream());
		 * writer.setOutput(ios);
		 */
		
		byte[] buf = new byte[1024];
		int len;
		ServletOutputStream os = response.getOutputStream();
		while ((len = in.read(buf)) != -1) {
			os.write(buf, 0, len);
		}

		in.close();
		os.close();
		// ios.close();
	}

}