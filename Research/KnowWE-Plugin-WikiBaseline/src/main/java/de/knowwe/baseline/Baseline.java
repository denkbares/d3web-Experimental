/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.baseline;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

public class Baseline implements Comparable<Baseline> {

	private final String username;
	private final String name;
	private final long date;
	private final Map<String, Integer> articles;

	public Baseline(String name, long date, String username) {
		this.name = name;
		this.username = username;
		this.date = date;
		this.articles = new HashMap<String, Integer>();
	}

	public long getDate() {
		return date;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public Collection<String> getArticles() {
		return Collections.unmodifiableCollection(articles.keySet());
	}

	public void addArticle(String title, int version) {
		articles.put(title, Integer.valueOf(version));
	}

	public boolean contains(String title) {
		return articles.containsKey(title);
	}

	public int getVersion(String title) {
		Integer version = articles.get(title);
		if (version == null) {
			return -1;
		}
		else {
			return version.intValue();
		}
	}

	public static String toXML(Baseline baseline) {
		StringBuilder bob = new StringBuilder();
		bob.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	
		bob.append("<baseline ");
		bob.append("name=\"");
		bob.append(baseline.getName());
		bob.append("\" user=\"");
		bob.append(baseline.getUsername());
		bob.append("\" date=\"");
		bob.append(baseline.getDate());
		bob.append("\">\n");
		for (String title : baseline.getArticles()) {
			bob.append("<article version=\"");
			bob.append(baseline.getVersion(title));
			bob.append("\">");
			bob.append(StringEscapeUtils.escapeXml(title));
			bob.append("</article>\n");
	
		}
		bob.append("</baseline>");
		return bob.toString();
	}

	@Override
	public int compareTo(Baseline o) {
		long l = this.getDate() - o.getDate();
		if (l > 0)  {
			return 1;
		} else if (l < 0) {
			return -1;
		} else return 0;
		
	}


}