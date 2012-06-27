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
package de.knowwe.diaflux.coverage.metrics;

import java.awt.Color;


/**
 * 
 * @author Reinhard Hatko
 * @created 20.05.2012
 */
public abstract class AbstractColorMetric<I> implements Metric<I, Color> {

	private final float fromColor;
	private final float toColor;

	public AbstractColorMetric() {
		this(0, 0.333f);
	}

	public AbstractColorMetric(float fromColor, float toColor) {
		this.fromColor = fromColor;
		this.toColor = toColor;
	}

	@Override
	public Color getValue(I object) {

		float hue = fromColor + (toColor - fromColor) * getColorValue(object);
		// hue = .9f;
		return Color.getHSBColor(hue, 1f, 8f);
	}

	protected abstract float getColorValue(I object);

}
