/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3webviz.diafluxCity.kdtree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import javax.imageio.ImageIO;

/**
 * 
 * @author Reinhard Hatko
 * @created 04.02.2012
 */
public class RectanglePacker {

	public interface Mapping<I> {

		Dimension2D map(I object);

	}

	public static void main(String[] args) {

		// List<Dimension2D> dimensions = generateRandomDimensions(200, 1, 50);
		List<Dimension2D> dimensions = new ArrayList<Dimension2D>();
		dimensions.add(new Dimension(20, 20));
		dimensions.add(new Dimension(20, 20));
		dimensions.add(new Dimension(20, 20));
		dimensions.add(new Dimension(20, 20));

		Mapping<Dimension2D> map = new Mapping<Dimension2D>() {

			@Override
			public Dimension2D map(Dimension2D object) {
				return object;
			}
		};

		KDNode<Dimension2D> root = pack(dimensions, map);

		printResult(root, "d:\\city");

	}

	private static <I> void sortObjects(List<I> objects, final Mapping<I> map) {

		Collections.sort(objects, new Comparator<I>() {

			public int compare(I o1, I o2) {
				Dimension2D dim1 = map.map(o1);
				Dimension2D dim2 = map.map(o2);
				return (int) ((dim2.getWidth() * dim2.getHeight()) - (dim1.getWidth() *
				dim1.getHeight()));
			}
		});

	}

	public static <I> KDNode<I> pack(List<I> objects, Mapping<I> map) {

		Dimension2D sumOfDim = calculateDimensionSum(objects, map);
		Rectangle2D covRec = new Rectangle2D.Double();

		sortObjects(objects, map);

		KDNode<I> tree = new KDNode<I>(new Rectangle2D.Double(0, 0, sumOfDim.getWidth(),
				sumOfDim.getHeight()));

		for (I object : objects) {
			Dimension2D dim = map.map(object);

			// System.out.println("insert: " + dim);

			List<KDNode<I>> nodes = tree.visitInorder(new CollectEmptyLeavesOfSizeVisitor<I>(dim)).getNodes();
			NavigableMap<Double, KDNode<I>> preservers = new TreeMap<Double, KDNode<I>>();
			NavigableMap<Double, KDNode<I>> expanders = new TreeMap<Double, KDNode<I>>(
					new Comparator<Double>() {

						@Override
						public int compare(Double o2, Double o1) {
							return (int) (Math.abs(1 - o1.doubleValue()) - Math.abs(1
									- o2.doubleValue()));
						}

					}
					);

			for (KDNode<I> node : nodes) {
				Rectangle2D tempCovRec = (Rectangle2D) covRec.clone();
				Rectangle2D destination = node.findCompleteSplitForLeave(dim);

				boolean fits = tempCovRec.contains(destination);

				if (fits) {
					double waste = KDUtils.getArea(node.getBounds()) - KDUtils.getArea(destination);
					preservers.put(waste, node);

				}
				else {
					tempCovRec.add(destination);
					expanders.put(KDUtils.getRatio(tempCovRec), node);

				}

			}

			Entry<Double, KDNode<I>> entry;
			if (!preservers.isEmpty()) {
				entry = preservers.firstEntry();
			}
			else {
				entry = expanders.lastEntry();
				// for (Double d : expanders.navigableKeySet()) {
				// System.out.print(d + "  ");
				// }
				// System.out.println("\r\nChosen: " + entry.getKey());
			}
			KDNode<I> bestNode = entry.getValue();
			KDNode<I> targetNode = bestNode.insert(object, map);
			covRec.add(targetNode.getBounds());
			// printResult(tree, "d:\\city" + i);

		}

		return tree;

	}

	/**
	 * 
	 * @created 04.02.2012
	 * @param dims
	 * @return
	 */
	private static <I> Dimension2D calculateDimensionSum(List<I> dims, Mapping<I> map) {
		double width = 0;
		double height = 0;

		for (I object : dims) {

			Dimension2D dim = map.map(object);

			width += dim.getWidth();
			height += dim.getHeight();
		}

		return new DoubleDimension(width, height);

	}

	/**
	 * 
	 * @created 04.02.2012
	 * @param i
	 * @return
	 */
	public static List<Dimension2D> generateRandomDimensions(int count, int min, int max) {
		List<Dimension2D> dimensions = new ArrayList<Dimension2D>(count);

		for (int i = 0; i < count; i++) {
			dimensions.add(createDimensionRect(min, max));
		}

		return dimensions;
	}

	/**
	 * 
	 * @param max
	 * @param min
	 * @created 04.02.2012
	 * @return
	 */
	private static Dimension2D createDimensionRect(int min, int max) {
		int width = new Random().nextInt(max) + min;
		int height = new Random().nextInt(max) + min;
		// int width = height;
		return new DoubleDimension(width, height) {

			@Override
			public String toString() {
				return super.toString() + " area = " + getWidth() * getHeight();
			}
		};
	}

	public static <T> void printResult(KDNode<T> root, String pathname) {
		List<Rectangle2D> rects = root.visitInorder(new CollectRectanglesVisitor<T>(0)).getRects();
		Rectangle2D bounds = root.visitInorder(new AddRectanglesVisitor<T>()).getBounds();
		Dimension2D dim = KDUtils.getDimension(bounds);
		try {

			BufferedImage image = new BufferedImage((int) dim.getWidth() + 1,
					(int) dim.getHeight() + 1, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = image.createGraphics();
			graphics2D.setColor(Color.WHITE);
			graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
			Random random = new Random();

			for (Rectangle2D rectangle2d : rects) {

				// graphics2D.setColor(Color.BLACK);
				graphics2D.setColor(new Color(random.nextFloat(),
						random.nextFloat(),
						random.nextFloat(), 0.5f));

				graphics2D.fill(rectangle2d);

			}

			ImageIO.write(image, "png", new File(pathname + ".png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

}
