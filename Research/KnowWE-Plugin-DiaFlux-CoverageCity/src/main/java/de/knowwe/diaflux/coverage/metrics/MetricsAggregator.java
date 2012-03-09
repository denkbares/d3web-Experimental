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
 * @created 07.02.2012
 */
public class MetricsAggregator<I> {

	private Metric<I, Double> widthMetric;
	private Metric<I, Double> lengthMetric;
	private Metric<I, Double> heightMetric;
	private Metric<I, Color> colorMetric;

	public double getHeight(I object) {
		return heightMetric.getValue(object);
	}

	public double getWidth(I object) {
		return widthMetric.getValue(object);
	}

	public double getLength(I object) {
		return lengthMetric.getValue(object);
	}

	public double[] getSize(I object) {
		return new double[] {
				lengthMetric.getValue(object), widthMetric.getValue(object),
				heightMetric.getValue(object) };
	}

	public Color getColor(I object) {
		return colorMetric.getValue(object);
	}

	public Metric<I, Double> getWidthMetric() {
		return widthMetric;
	}

	public void setWidthMetric(Metric<I, Double> widthMetric) {
		this.widthMetric = widthMetric;
	}

	public Metric<I, Double> getLengthMetric() {
		return lengthMetric;
	}

	public void setLengthMetric(Metric<I, Double> lengthMetric) {
		this.lengthMetric = lengthMetric;
	}

	public Metric<I, Double> getHeightMetric() {
		return heightMetric;
	}

	public void setHeightMetric(Metric<I, Double> heightMetric) {
		this.heightMetric = heightMetric;
	}

	public Metric<I, Color> getColorMetric() {
		return colorMetric;
	}

	public void setColorMetric(Metric<I, Color> colorMetric) {
		this.colorMetric = colorMetric;
	}

}
