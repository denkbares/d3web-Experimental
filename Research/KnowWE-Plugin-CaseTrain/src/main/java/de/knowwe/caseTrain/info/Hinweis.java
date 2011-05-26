/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.casetrain.info;

import de.knowwe.casetrain.type.general.SubblockMarkup;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Bild;
import de.knowwe.casetrain.type.multimedia.Video;


/**
 * 
 * @author Johannes Dienst
 * @created 12.05.2011
 */
public class Hinweis extends SubblockMarkup {

	public Hinweis() {
		super("Hinweis");
		this.addChildType(new Title());
		this.addContentType(new Bild());
		this.addContentType(new Video());
	}

}
