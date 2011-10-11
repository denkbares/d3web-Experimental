/**
 * KnowWE Metatool
 * Copyright (C) 2011 Alex Legler
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package de.uniwue.knowwe.metatool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import utils.MyTestArticleManager;
import utils.TestUtils;
import de.d3web.plugin.test.InitPluginManager;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * A launcher class that renders an article
 * 
 * @author Alex Legler
 */
public class TestLauncher {
	
	public final static String PREPEND =
			"<style>\n" + 
			"* { white-space: pre-wrap; }\n" + 
			"</style>\n";
	
	/**
	 * @param args
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			throw new RuntimeException(
					"Usage: java TestLauncher <kmx file> <target directory>");
		}

		File markupFile = new File(args[0]);

		if (!markupFile.exists()) {
			throw new RuntimeException(markupFile.getAbsolutePath()
					+ ": No such file");
		}

		File targetDir = new File(args[1]);

		if (!targetDir.isDirectory()) {
			if (!targetDir.mkdirs()) {
				throw new RuntimeException(targetDir.getAbsolutePath()
						+ ": No such directory and cannot create it.");
			}
		}

		InitPluginManager.init();

		KnowWEArticle art = MyTestArticleManager.getArticle(markupFile
				.getPath());

		StringBuilder sb = new StringBuilder();
		UserContext userContext = TestUtils.createTestActionContext(null, null);
		art.getRenderer().render(art, art.getSection(), userContext, sb);

		File targetFile = new File(targetDir, markupFile.getName() + ".html");

		String content = KnowWEUtils.unmaskHTML(sb.toString());
		
		try {
			FileWriter fstream = new FileWriter(targetFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<title>MetaTool Test: " + markupFile.getName() + "</title>");
			out.write(PREPEND);
			out.write(content);
			out.close();
			fstream.close();
		} catch (IOException e) {
			throw new RuntimeException("Cannot write to file: "
					+ e.getMessage(), e);
		}
		
		System.out.println("Rendering done.");
		
		System.exit(0);
	}
}
