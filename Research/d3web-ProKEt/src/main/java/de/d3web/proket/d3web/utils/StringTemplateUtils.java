/**
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.d3web.proket.d3web.utils;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 *
 * Basic utils class for managing the StringTemplate files.
 * Contains methods for initializing the basic StringTemplate
 * path as well as for retriving given templates
 * 
 * @author Martina Freiberg
 * @date Oct 2012
 * 
 */
public class StringTemplateUtils {

    private static String stBasePath = "";
    private static StringTemplateGroup baseSTG = null;

    public static void initializeStringTemplateStructure(String basePath) {

        stBasePath = basePath;
        //System.out.println(basePath);
        // this is the topmost ST directory
        baseSTG = new StringTemplateGroup("stGroup", stBasePath);
    }

    public static StringTemplate getTemplate(String templateSubPathAndName) {
        StringTemplate st = null;
        
        return baseSTG.getInstanceOf(templateSubPathAndName);
       
    }
}
