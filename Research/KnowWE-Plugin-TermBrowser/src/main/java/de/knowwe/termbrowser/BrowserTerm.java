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

package de.knowwe.termbrowser;

import de.d3web.strings.Identifier;

/**
 * @author Jochen Reutelshoefer (denkbares GmbH)
 * @created 13.10.14.
 */
public class BrowserTerm  {

    /*
     type of this term
     */
    private String type = null;

    public String getLabel() {
        return label;
    }

    /*
        preferred label of this term
         */
    private String label = null;

    /*
    identifier of the term actually represented
     */
    private Identifier identifier;

    public BrowserTerm(String type, String label, String... pathElements) {
        identifier = new Identifier(pathElements);
        this.type = type;
        this.label = label;
    }

    public BrowserTerm(String type, String label, Identifier identifier) {
        this.identifier = identifier;
        this.type = type;
        this.label = label;
    }

    public BrowserTerm(String type, Identifier identifier) {
        this(type, null, identifier);
    }

    public BrowserTerm(String type, String... pathElements) {
        this(type, null, pathElements);
    }

    public BrowserTerm(Identifier identifier) {
        this(null, identifier);
    }

    public BrowserTerm(String... pathElements) {
        this(null, pathElements);
    }

    public String getType() {
        return type;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BrowserTerm that = (BrowserTerm) o;

        if (!identifier.equals(that.identifier)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
