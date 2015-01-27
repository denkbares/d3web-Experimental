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
package org.openrdf.http.server;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import info.aduna.webapp.views.SimpleResponseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * Simple resolver for Exceptions: returns the correct response code and message
 * to the client.
 *
 * @author Herko ter Horst
 */
public class ProtocolExceptionResolver implements HandlerExceptionResolver {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
										 Object handler, Exception exception) {
		logger.debug("ProtocolExceptionResolver.resolveException() called");

		int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errMsg = exception.getMessage();

		if (exception instanceof HTTPException) {
			HTTPException httpExc = (HTTPException) exception;
			statusCode = httpExc.getStatusCode();

			if (exception instanceof ClientHTTPException) {
				logger.info("Client sent bad request ({}): {}", statusCode, errMsg);
			}
			else {
				logger.error("Error while handling request ({}): {}", statusCode, errMsg);
			}
		}
		else {
			logger.error("Error while handling request", exception);
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put(SimpleResponseView.SC_KEY, Integer.valueOf(statusCode));
		model.put(SimpleResponseView.CONTENT_KEY, errMsg);

		return new ModelAndView(SimpleResponseView.getInstance(), model);
	}
}
