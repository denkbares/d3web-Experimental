/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.event;

import de.knowwe.core.event.Event;

/**
 * The NewCommentEvent is fired when a user adds a new comment. You can listen
 * to this event through your own event handler and do whatever you need in your
 * plugin.
 *
 * @author smark
 * @created 08.03.2011
 */
public class NewCommentEvent extends Event {

	private final String comment;
	private final String topic;

	public NewCommentEvent(String comment, String topic) {

		if (comment == null || topic == null) throw new IllegalArgumentException(
				"Paramters mustn't be null!");

		this.comment = comment;
		this.topic = topic;
	}

	/**
	 * Returns the topic of the comment. E.g. the pagename the user commented
	 * on.
	 *
	 * @created 08.03.2011
	 * @return String
	 */
	public String getTopic() {
		return this.topic;
	}

	/**
	 * Returns the comment of the user.
	 *
	 * @created 08.03.2011
	 * @return String
	 */
	public String getComment() {
		return this.comment;
	}
}
