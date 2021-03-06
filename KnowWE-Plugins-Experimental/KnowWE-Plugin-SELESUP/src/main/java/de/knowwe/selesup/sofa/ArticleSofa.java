/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.selesup.sofa;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import de.knowwe.core.kdom.Article;

/**
 * Implementation of the {@link Sofa} interface, that encapsulates a {@link Article}.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 29.08.14
 */
public class ArticleSofa implements Sofa {

	private final Article article;

	public ArticleSofa(Article article) {
		if (article == null) {
			throw new NullPointerException();
		}
		this.article = article;
	}

	@Override
	public String getName() {
		return article.getTitle();
	}

	@Override
	public String getType() {
		return Article.class.getSimpleName();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return IOUtils.toInputStream(article.getText(), Charset.forName("UTF-8"));
	}
}
