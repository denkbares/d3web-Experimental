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

package de.knowwe.termbrowser.util;


/**
 * @author Jochen Reutelshoefer (denkbares GmbH)
 * @created 15.10.14.
 */
public class TermBrowserUtils {

    public static String abbreviateTypeNameForURI(String uri) {
        if (uri == null || !uri.startsWith("http:")) {
            throw new IllegalArgumentException("not a valid uri");
        }
        String typeName = null;
        if (uri.contains("#")) {
            typeName = uri.substring(uri.lastIndexOf('#') + 1);
        } else {
            typeName = uri.substring(uri.lastIndexOf('/') + 1);
        }
        return TermBrowserUtils.abbreviateTypeName(typeName);
    }

    public static String abbreviateTypeName(String typeName) {
        if (typeName.length() > 5) {
            StringBuffer buffy = new StringBuffer();
            boolean leadingVowel = true;
            char last = 0;
            for (int i = 0; i < typeName.length(); i++) {
                char c = typeName.charAt(i);
                if (Character.toString(c).matches("[aeiouAEIOU]")) {
                    if (leadingVowel) {
                        if (c != last) {
                            buffy.append(c);
                            last = c;
                        }
                    }
                } else {
                    leadingVowel = false;
                    if (c != last) {

                        buffy.append(c);
                        last = c;
                    }
                }

            }
            return buffy.toString();
        }
        return typeName;
    }
}
