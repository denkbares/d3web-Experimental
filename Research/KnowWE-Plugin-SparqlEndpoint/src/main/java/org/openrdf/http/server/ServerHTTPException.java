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

import java.net.HttpURLConnection;

/**
 * HTTP-related exception indicating that an error occurred in a server. Status
 * codes for these types of errors are in the 5xx range. The default status code
 * for constructors without a <tt>statusCode</tt> parameter is
 * <tt>500 Internal Server Error</tt>.
 *
 * @author Arjohn Kampman
 */
public class ServerHTTPException extends HTTPException {

	private static final long serialVersionUID = -3949837199542648966L;

	private static final int DEFAULT_STATUS_CODE = HttpURLConnection.HTTP_INTERNAL_ERROR;

	/**
	 * Creates a {@link ServerHTTPException} with status code 500 "Internal
	 * Server Error".
	 */
	public ServerHTTPException() {
		this(DEFAULT_STATUS_CODE);
	}

	/**
	 * Creates a {@link ServerHTTPException} with status code 500 "Internal
	 * Server Error".
	 */
	public ServerHTTPException(String msg) {
		this(DEFAULT_STATUS_CODE, msg);
	}

	/**
	 * Creates a {@link ServerHTTPException} with status code 500 "Internal
	 * Server Error".
	 */
	public ServerHTTPException(String msg, Throwable t) {
		this(DEFAULT_STATUS_CODE, t);
	}

	/**
	 * Creates a {@link ServerHTTPException} with the specified status code. The
	 * supplied status code must be in the 5xx range.
	 *
	 * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 5xx range.
	 */
	public ServerHTTPException(int statusCode) {
		super(statusCode);
	}

	/**
	 * Creates a {@link ServerHTTPException} with the specified status code. The
	 * supplied status code must be in the 5xx range.
	 *
	 * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 5xx range.
	 */
	public ServerHTTPException(int statusCode, String message) {
		super(statusCode, message);
	}

	/**
	 * Creates a {@link ServerHTTPException} with the specified status code. The
	 * supplied status code must be in the 5xx range.
	 *
	 * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 5xx range.
	 */
	public ServerHTTPException(int statusCode, String message, Throwable t) {
		super(statusCode, message, t);
	}

	/**
	 * Creates a {@link ServerHTTPException} with the specified status code. The
	 * supplied status code must be in the 5xx range.
	 *
	 * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 5xx range.
	 */
	public ServerHTTPException(int statusCode, Throwable t) {
		super(statusCode, t);
	}

	@Override
	protected void setStatusCode(int statusCode) {
		if (statusCode < 500 || statusCode > 599) {
			throw new IllegalArgumentException("Status code must be in the 5xx range, is: " + statusCode);
		}

		super.setStatusCode(statusCode);
	}
}
