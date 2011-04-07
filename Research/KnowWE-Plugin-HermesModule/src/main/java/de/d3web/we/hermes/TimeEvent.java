/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.hermes;

import java.util.ArrayList;
import java.util.List;

public class TimeEvent implements Comparable<TimeEvent> {

	public void setDescription(String description) {
		this.description = description;
	}

	private Integer importance;

	private final String title;
	private String description;
	private List<String> sources = new ArrayList<String>();
	private final String textOriginNode;
	private final String topic;

	public String getTopic() {
		return topic;
	}

	private TimeStamp time;

	public TimeEvent(String title, String description, Integer imp, List<String> sources, String time, String textOriginNode, String topic) {
		this.title = title;
		this.description = description;
		this.sources = sources;
		this.importance = imp;
		this.textOriginNode = textOriginNode;
		this.topic = topic;
		if (time != null) {
			this.time = new TimeStamp(time);
		}
	}

	/**
	 * Allows to instanciate TimeEvents directly from the markup. Other data is
	 * added later.
	 * 
	 * @param title
	 * @param textOriginNode
	 * @param topic
	 */
	public TimeEvent(String title, String textOriginNode, String topic) {
		this.textOriginNode = textOriginNode;
		this.topic = topic;
		this.title = title;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}

	public void addSource(String src) {
		this.sources.add(src);
	}

	public void setTime(TimeStamp time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}

	public Integer getImportance() {
		return importance;
	}

	public List<String> getSources() {
		return sources;
	}

	public String getTextOriginNode() {
		return textOriginNode;
	}

	public TimeStamp getTime() {
		return time;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int compareTo(TimeEvent o) {
		return this.getTime().compareTo(o.getTime());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((importance == null) ? 0 : importance.hashCode());
		result = prime * result + ((sources == null) ? 0 : sources.hashCode());
		result = prime * result + ((textOriginNode == null) ? 0 : textOriginNode.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TimeEvent other = (TimeEvent) obj;
		if (description == null) {
			if (other.description != null) return false;
		}
		else if (!description.equals(other.description)) return false;
		if (importance == null) {
			if (other.importance != null) return false;
		}
		else if (!importance.equals(other.importance)) return false;
		if (sources == null) {
			if (other.sources != null) return false;
		}
		else if (!sources.equals(other.sources)) return false;
		if (textOriginNode == null) {
			if (other.textOriginNode != null) return false;
		}
		else if (!textOriginNode.equals(other.textOriginNode)) return false;
		if (time == null) {
			if (other.time != null) return false;
		}
		else if (!time.equals(other.time)) return false;
		if (title == null) {
			if (other.title != null) return false;
		}
		else if (!title.equals(other.title)) return false;
		if (topic == null) {
			if (other.topic != null) return false;
		}
		else if (!topic.equals(other.topic)) return false;
		return true;
	}

}
