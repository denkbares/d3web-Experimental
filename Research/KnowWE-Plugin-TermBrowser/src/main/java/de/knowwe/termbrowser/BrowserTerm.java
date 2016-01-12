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
import de.knowwe.core.user.UserContext;

/**
 * @author Jochen Reutelshoefer (denkbares GmbH)
 * @created 13.10.14.
 */
public class BrowserTerm  {

    /*
     type of this term
     */
    private String type = null;
    private UserContext user;

    /*
        preferred label of this term
         */
    private String label = null;

    /*
    identifier of the term actually represented
     */
    private Identifier identifier;

    public BrowserTerm(String type, String label, UserContext user, String... pathElements) {
        this.user = user;
        identifier = new Identifier(pathElements);
        this.type = type;
        this.label = label;
    }

    public BrowserTerm(String type, String label, UserContext user, Identifier identifier) {
        this.user = user;
        this.identifier = identifier;
        this.type = type;
        this.label = label;
    }

    public BrowserTerm(Identifier identifier, UserContext user) {
        this(null, null, user, identifier);
    }

    public String getType() {
        return type;
    }

	@Override
	public String toString() {
		return label + "("+identifier+")";
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

    public UserContext getUser() {
        return user;
    }

    public String getLabel() {
        return label;
    }
}
