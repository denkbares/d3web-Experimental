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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.http.protocol.Protocol;
import org.openrdf.http.server.ClientHTTPException;
import org.openrdf.http.server.ProtocolUtil;
import org.openrdf.http.server.ServerHTTPException;
import org.openrdf.http.server.ServerInterceptor;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.sparqlendpoint.SparqlEndpointAction;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Interceptor for repository requests. Handles the opening and closing of
 * connections to the repository specified in the request. Should not be a
 * singleton bean! Configure as inner bean in openrdf-servlet.xml
 *
 * @author Herko ter Horst
 * @author Arjohn Kampman
 * @author Stefan Plehn (modified)
 */
public class RepositoryInterceptor extends ServerInterceptor {

	/*-----------*
	 * Constants *
	 *-----------*/

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String REPOSITORY_ID_KEY = "repositoryID";

	private static final String REPOSITORY_KEY = "repository";

	private static final String REPOSITORY_CONNECTION_KEY = "repositoryConnection";

	/*-----------*
	 * Variables *
	 *-----------*/

	private RepositoryManager repositoryManager;

	private String repositoryID;

	private RepositoryConnection repositoryCon;

	/*---------*
	 * Methods *
	 *---------*/

	public void setRepositoryManager(RepositoryManager repMan) {
		repositoryManager = repMan;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse respons, Object handler)
			throws Exception {
		String pathInfoStr = request.getPathInfo();
		logger.debug("path info: {}", pathInfoStr);

		repositoryID = null;

		if (pathInfoStr != null && !pathInfoStr.equals("/")) {
			String[] pathInfo = pathInfoStr.substring(1).split("/");
			if (pathInfo.length > 0) {
				repositoryID = pathInfo[0];
				logger.debug("repositoryID is '{}'", repositoryID);
			}
		}

		ProtocolUtil.logRequestParameters(request);

		return super.preHandle(request, respons, handler);
	}

	@Override
	protected String getThreadName() {
		String threadName = Protocol.REPOSITORIES;

		if (repositoryID != null) {
			threadName += "/" + repositoryID;
		}

		return threadName;
	}

	@Override
	protected void setRequestAttributes(HttpServletRequest request)
			throws ClientHTTPException, ServerHTTPException {
		if (repositoryID != null) {
			try {
				Repository repository = repositoryManager.getRepository(repositoryID);

				if (repository == null) {
					throw new ClientHTTPException(SC_NOT_FOUND, "Unknown repository: " + repositoryID);
				}

				repositoryCon = repository.getConnection();

				// SES-1834 by default, the Sesame server should not treat datatype or language value verification errors
				// as fatal. This is to be graceful, by default, about accepting "dirty" data.
				// FIXME SES-1833 this should be configurable by the user.
				repositoryCon.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
				repositoryCon.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_LANGUAGE_TAGS);

				// FIXME: hack for repositories that return connections that are not
				// in auto-commit mode by default
				if (!repositoryCon.isAutoCommit()) {
					repositoryCon.setAutoCommit(true);
				}

				request.setAttribute(REPOSITORY_ID_KEY, repositoryID);
				request.setAttribute(REPOSITORY_KEY, repository);
				request.setAttribute(REPOSITORY_CONNECTION_KEY, repositoryCon);
			}
			catch (RepositoryConfigException e) {
				throw new ServerHTTPException(e.getMessage(), e);
			}
			catch (RepositoryException e) {
				throw new ServerHTTPException(e.getMessage(), e);
			}
		}
	}

	@Override
	protected void cleanUpResources()
			throws ServerHTTPException {
		if (repositoryCon != null) {
			try {
				repositoryCon.close();
			}
			catch (RepositoryException e) {
				throw new ServerHTTPException(e.getMessage(), e);
			}
		}
	}

	public static String getRepositoryID(HttpServletRequest request) {
		return (String) request.getAttribute(REPOSITORY_ID_KEY);
	}

	public static Repository getRepository(HttpServletRequest request) {

		Repository repository = (Repository) getRdf2GoCore(request).getUnderlyingModelImplementation();
		return repository;
	}

	public static Rdf2GoCore getRdf2GoCore(HttpServletRequest request) {
		String pack = request.getParameter(SparqlEndpointAction.PACKAGE);
		ArticleManager articleManager = KnowWEUtils.getArticleManager(Environment.DEFAULT_WEB);
		Collection<Section<?>> sectionsOfPackage = KnowWEUtils.getPackageManager(Environment.DEFAULT_WEB)
				.getSectionsOfPackage(pack);
		Class<Rdf2GoCompiler> compilerClass = Rdf2GoCompiler.class;
		if (sectionsOfPackage.isEmpty()) {
			return Rdf2GoCore.getInstance(Compilers.getCompiler(articleManager, compilerClass));
		}
		else {
			Rdf2GoCompiler compiler = Compilers.getCompiler(sectionsOfPackage.stream()
					.findFirst()
					.get(), compilerClass);
			return Rdf2GoCore.getInstance(compiler);
		}
	}

	public static RepositoryConnection getRepositoryConnection(HttpServletRequest request) {
		try {
			return getRepository(request).getConnection();
		}
		catch (RepositoryException e) {
			e.printStackTrace();
		}
		return null;

	}
}
