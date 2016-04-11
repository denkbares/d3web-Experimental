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
 * HTTP-related exception indicating that an HTTP client has erred. Status codes
 * for these types of errors are in the 4xx range. The default status code for
 * constructors without a <tt>statusCode</tt> parameter is
 * <tt>400 Bad Request</tt>.
 *
 * @author Arjohn Kampman
 */
public class ClientHTTPException extends HTTPException {

	private static final long serialVersionUID = 7722604284325312749L;

	private static final int DEFAULT_STATUS_CODE = HttpURLConnection.HTTP_BAD_REQUEST;

	/**
	 * Creates a {@link ClientHTTPException} with status code 400 "Bad Request".
	 */
	public ClientHTTPException() {
		this(DEFAULT_STATUS_CODE);
	}

	/**
	 * Creates a {@link ClientHTTPException} with status code 400 "Bad Request".
	 */
	public ClientHTTPException(String msg) {
		this(DEFAULT_STATUS_CODE, msg);
	}

	/**
	 * Creates a {@link ClientHTTPException} with status code 400 "Bad Request".
	 */
	public ClientHTTPException(String msg, Throwable t) {
		this(DEFAULT_STATUS_CODE, t);
	}

	/**
	 * Creates a {@link ClientHTTPException} with the specified status code.
	 *
	 * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 4xx range.
	 */
	public ClientHTTPException(int statusCode) {
		super(statusCode);
	}

	/**
	 * Creates a {@link ClientHTTPException} with the specified status code.
	 *
	 * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 4xx range.
	 */
	public ClientHTTPException(int statusCode, String message) {
		super(statusCode, message);
	}

	/**
	 * Creates a {@link ClientHTTPException} with the specified status code.
	 *
	 * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 4xx range.
	 */
	public ClientHTTPException(int statusCode, String message, Throwable t) {
		super(statusCode, message, t);
	}

	/**
	 * Creates a {@link ClientHTTPException} with the specified status code.
	 *
	 * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 4xx range.
	 */
	public ClientHTTPException(int statusCode, Throwable t) {
		super(statusCode, t);
	}

	@Override
	protected void setStatusCode(int statusCode) {
		if (statusCode < 400 || statusCode > 499) {
			throw new IllegalArgumentException("Status code must be in the 4xx range, is: " + statusCode);
		}

		super.setStatusCode(statusCode);
	}
}
