/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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
package org.openrdf.http.server.repository;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.BooleanQueryResultWriterFactory;

import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * View used to render boolean query results. Renders results in a format
 * specified using a parameter or Accept header.
 *
 * @author Arjohn Kampman
 */
public class BooleanQueryResultView extends QueryResultView {

	private static final BooleanQueryResultView INSTANCE = new BooleanQueryResultView();

	public static BooleanQueryResultView getInstance() {
		return INSTANCE;
	}

	private BooleanQueryResultView() {
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void render(Map model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		BooleanQueryResultWriterFactory brWriterFactory = (BooleanQueryResultWriterFactory) model.get(FACTORY_KEY);
		BooleanQueryResultFormat brFormat = brWriterFactory.getBooleanQueryResultFormat();

		response.setStatus(SC_OK);
		setContentType(response, brFormat);
		setContentDisposition(model, response, brFormat);

		boolean headersOnly = (Boolean) model.get(HEADERS_ONLY);

		if (!headersOnly) {
			try (OutputStream out = response.getOutputStream()) {
				BooleanQueryResultWriter qrWriter = brWriterFactory.getWriter(out);
				boolean value = (Boolean) model.get(QUERY_RESULT_KEY);
				qrWriter.handleBoolean(value);
			}
			catch (QueryResultHandlerException e) {
				if (e.getCause() != null && e.getCause() instanceof IOException) {
					throw (IOException) e.getCause();
				}
				else {
					throw new IOException(e);
				}
			}
		}
		//logEndOfRequest(request);
	}
}
