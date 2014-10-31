/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

package de.knowwe.termbrowser.autocompletion;

import java.util.Collection;
import java.util.HashSet;

import com.denkbares.semanticautocompletion.Completion;
import com.denkbares.semanticautocompletion.CompletionProvider;
import com.denkbares.semanticautocompletion.DefaultCompletion;
import com.denkbares.semanticautocompletion.DefaultConcept;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;

/**
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 16.09.14.
 */
public class WikiPageCompletionProvider implements CompletionProvider {
    @Override
    public Collection<Completion> getCompletions() {
        final Collection<Article> articles = Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB).getArticles();
        Collection<Completion> result = new HashSet<>();
        for (Article article : articles) {
            result.add(new DefaultCompletion(article.getTitle(), new DefaultConcept(article.getTitle(), article.getTitle(), "Page")));
        }
        return result;
    }
}
