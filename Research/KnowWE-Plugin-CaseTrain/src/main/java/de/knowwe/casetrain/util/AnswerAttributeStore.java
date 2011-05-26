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
package de.knowwe.casetrain.util;



/**
 * 
 * @author Johannes Dienst
 * @created 16.05.2011
 */
public class AnswerAttributeStore {

	private final String posFactor;
	private final String negFactor;
	private final String text;
	private final String simplefeedback;
	private final String textArgument;

	public AnswerAttributeStore(String posFactor, String negFactor,
			String text, String simplefeedback, String textArgString) {
		this.posFactor = posFactor;
		this.negFactor = negFactor;
		this.text = text;
		this.simplefeedback = simplefeedback;
		this.textArgument = textArgString;
	}

	public String getPosFactor() {
		return posFactor;
	}


	public String getNegFactor() {
		return negFactor;
	}


	public String getText() {
		return text;
	}


	public String getSimplefeedback() {
		return simplefeedback;
	}

	public String getTextArgument() {
		return textArgument;
	}


}
