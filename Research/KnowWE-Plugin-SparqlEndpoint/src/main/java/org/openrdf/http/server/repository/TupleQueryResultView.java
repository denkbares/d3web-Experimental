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

import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.query.QueryResults;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * View used to render tuple query results. Renders results in a format
 * specified using a parameter or Accept header.
 *
 * @author Herko ter Horst
 * @author Arjohn Kampman
 */
public class TupleQueryResultView extends QueryResultView {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final TupleQueryResultView INSTANCE = new TupleQueryResultView();

	public static TupleQueryResultView getInstance() {
		return INSTANCE;
	}

	private TupleQueryResultView() {
	}

	public String getContentType() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public void render(Map model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		TupleQueryResultWriterFactory qrWriterFactory = (TupleQueryResultWriterFactory) model.get(FACTORY_KEY);
		TupleQueryResultFormat qrFormat = qrWriterFactory.getTupleQueryResultFormat();

		response.setStatus(SC_OK);
		setContentType(response, qrFormat);
		setContentDisposition(model, response, qrFormat);

		boolean headersOnly = (Boolean) model.get(HEADERS_ONLY);

		if (!headersOnly) {
			OutputStream out = response.getOutputStream();
			try {
				TupleQueryResultWriter qrWriter = qrWriterFactory.getWriter(out);
				TupleQueryResult tupleQueryResult = (TupleQueryResult) model.get(QUERY_RESULT_KEY);
				QueryResults.report(tupleQueryResult, qrWriter);
			}
			catch (QueryInterruptedException e) {
				logger.error("Query interrupted", e);
				response.sendError(SC_SERVICE_UNAVAILABLE, "Query evaluation took too long");
			}
			catch (QueryEvaluationException e) {
				logger.error("Query evaluation error", e);
				response.sendError(SC_INTERNAL_SERVER_ERROR, "Query evaluation error: " + e.getMessage());
			}
			catch (TupleQueryResultHandlerException e) {
				logger.error("Serialization error", e);
				response.sendError(SC_INTERNAL_SERVER_ERROR, "Serialization error: " + e.getMessage());
			}
			finally {
				out.close();
			}
		}
		//logEndOfRequest(request);
	}
}
