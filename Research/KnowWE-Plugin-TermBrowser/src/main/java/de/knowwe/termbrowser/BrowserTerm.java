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
public class BrowserTerm extends Identifier {

    /*
     type of this term
     */
    private String type = null;

    public BrowserTerm(String type, String... pathElements) {
        super(pathElements);
        this.type = type;
    }

    public BrowserTerm(String... pathElements) {
        super(pathElements);
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (! (obj instanceof Identifier)) return false;
        Identifier other = (Identifier) obj;
        return this.toExternalForm().toLowerCase().equals(other.toExternalForm().toLowerCase());
    }

}
