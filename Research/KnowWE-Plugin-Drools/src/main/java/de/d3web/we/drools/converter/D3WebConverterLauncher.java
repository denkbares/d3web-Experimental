/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.drools.converter;

import java.io.File;
import java.io.IOException;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ConsoleProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.plugin.test.InitPluginManager;

public class D3WebConverterLauncher {

	/**
	 * The Path to the d3web-KnowledgeBase
	 */
	private static final String d3KbPath = System.getProperty("user.dir") + "/src/main/resources/misc/thermostat.jar";
	
	
	public static void main(String[] args) {
		try {
			InitPluginManager.init();
			System.out.println(d3KbPath);
			KnowledgeBase d3Kb = loadD3KnowledgeBase();
			D3WebConverter converter = new D3WebConverter(d3Kb);
			converter.convert("wikidemo-thermostat.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the KnowledgeBase.
	 * @param kbPath URL-formatted String representing the path to the KnowledgeBase
	 * @return the loaded KnowledgeBase
	 * @throws IOException 
	 */
	private static KnowledgeBase loadD3KnowledgeBase()	throws IOException {
		PersistenceManager pm = PersistenceManager.getInstance();
		KnowledgeBase kb = pm.load(new File(d3KbPath), new ConsoleProgressListener());
		return kb;
	}
	
}
