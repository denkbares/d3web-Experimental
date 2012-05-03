package officeconverter.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import officeconverter.Config;

public class ImageClipping {

	private final static Pattern clipPattern = Pattern.compile("rect\\(([^)]*)");

	public boolean clipImage(Config config, URL imageURL, File outFile, String clip, String scale, Logger logger) {
		int[] resolution = getResolution(logger, imageURL);

		float[] clipInformation = getClippingBoundaries(clip);
		
		boolean isZeros = true;
		for (float f : clipInformation)
			if (f != 0)
				isZeros = false;
		if (isZeros)
			return false;

		int top = getCmToPixel(clipInformation[0], resolution[1]);
		int right = getCmToPixel(clipInformation[1], resolution[0]);
		int bottom = getCmToPixel(clipInformation[2], resolution[1]);
		int left = getCmToPixel(clipInformation[3], resolution[0]);

		float[] scaleInformation = getScaling(scale);
		// int svg_y = getCmToPixel(scaleInformation[0], resolution[1]);
		int w = getCmToPixel(scaleInformation[1], resolution[0]);
		int h = getCmToPixel(scaleInformation[2], resolution[1]);
		// int zindex = getCmToPixel(scaleInformation[3], resolution[0]);

		BufferedImage bImage = null;
		try {
			bImage = getImage(imageURL);
			if (bImage == null) {
				logger.severe(imageURL + " falsches Dateiformat!");
				return false;
			}
		} catch (IOException e) {
			logger.severe(e.getLocalizedMessage());
		}
		
		//ERST beschneiden! Clipping-Werte beziehen sich auf ORIGINALGRÖSSE!
		int width = bImage.getWidth() - (left + right);
		int height = bImage.getHeight() - (top + bottom);

		int x = left, y = top;			
		
		BufferedImage clippedImage =
			new BufferedImage(
				width, height,
				BufferedImage.TYPE_INT_ARGB
			);
		
		Graphics2D g = clippedImage.createGraphics();
		g.drawImage(bImage, -x, -y, null, null);
		g.dispose();
		
		//DANN skalieren
		BufferedImage outImage;
		if (config.isWithImageScaling()) {
			Image im = clippedImage.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
			outImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gScaled = outImage.createGraphics();
			gScaled.drawImage(im, null, null);
			gScaled.dispose();
		} else
			outImage = clippedImage;
		
		try {
			writeImage(outFile, outImage);
		} catch (FileNotFoundException e) {
			logger.severe(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe(e.getLocalizedMessage());
		}
		return true;
	}

	/**
	 * reads the clipping boundaries from the clippingText (e.g. "rect(5.375cm
	 * 2.944cm 5.909cm 3.447cm)" and returns it in the following order: top
	 * right bottom left.
	 * 
	 * @param clip
	 * @return
	 */
	private static float[] getClippingBoundaries(String clip) {

		Matcher matcher = clipPattern.matcher(clip);
		if (matcher.find()) {
			String boundariesString = matcher.group(1);

			String[] stringBoundaries = boundariesString.split("cm");
			float[] floatBoundaries = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
			for (int i = 0; i < stringBoundaries.length; i++) {
				String stringBoundary = stringBoundaries[i];
				if (stringBoundary.startsWith(","))
					stringBoundary = stringBoundary.substring(1).trim();
				try {
					floatBoundaries[i] = Float.parseFloat(stringBoundary);
				} catch (NumberFormatException e) {
					// e.printStackTrace();
				}
			}
			return floatBoundaries;
		}
		return new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
	}

	private static float[] getScaling(String scale) {
		Pattern scalePattern = Pattern.compile("scaling[\\W]([\\w]|.)+\\)");
		Matcher matcher = scalePattern.matcher(scale); //richtiges Pattern!
		if (matcher.find()) {
			String boundariesString = matcher.group();

			boundariesString = boundariesString.substring(8, boundariesString.length() - 1);
			
			String[] stringBoundaries = boundariesString.split("cm");
			float[] floatBoundaries = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
			for (int i = 0; i < stringBoundaries.length; i++) {
				String stringBoundary;
				if (i > 0 && i < stringBoundaries.length)
					 stringBoundary = stringBoundaries[i].substring(2);
				else
					stringBoundary = stringBoundaries[i];
				try {
					floatBoundaries[i] = Float.parseFloat(stringBoundary);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			return floatBoundaries;
		}
		return new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
	}
	
	/**
	 * @param imageURL
	 * @return
	 * @throws IOException
	 */
	private BufferedImage getImage(URL imageURL) throws IOException {
		
		InputStream in = imageURL.openStream();
		BufferedImage bImage = null;
		try {
			bImage = ImageIO.read(in);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
		return bImage;
		
	}
	
	/**
	 * @param outFile
	 * @param bImage
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private File writeImage(File outFile, BufferedImage bImage) throws IOException, FileNotFoundException {
		OutputStream out = null;
		try {
			outFile.getParentFile().mkdirs();
			out = new FileOutputStream(outFile);
			if (outFile.getName().substring(outFile.getName().length() - 3).equals("png"))
				ImageIO.write(bImage, "PNG", out);
			else
				ImageIO.write(bImage, "JPG", out);
		} finally {
			if (out != null)
				out.close();
		}
		return outFile;
	}

	private static int defaultResolution = 96;

	private static int getCmToPixel(float cm, int dpi) {
		if (dpi < 0)
			dpi = defaultResolution;
		return (int) ((cm * dpi) / 2.54);
	}

	/**
	 * @param logger
	 * @param imageURL
	 * @return
	 * @throws IOException
	 */
	private int[] getResolution(Logger logger, URL imageURL) {
		int[] resolution = new int[] { 96, 96 };

		InputStream in = null;
		try {
			ImageInfo ii = new ImageInfo();
			in = imageURL.openStream();
			ii.setInput(in);
			// check does the actual work, you won't get results
			// before
			// you have called it
			if (!ii.check()) {
				logger.log(
					Level.WARNING,
					"Eines der Bilder hat ein nicht unterstütztes Dateiformat. Bitte verwenden Sie JPEG-Bilder.");
			} else {
				resolution[0] = ii.getPhysicalWidthDpi();
				resolution[1] = ii.getPhysicalHeightDpi();
			}
		} catch (IOException e) {
			logger.log(
					Level.WARNING,
					"Eines der Bilder konnte nicht geöffnet werden, um die Auflösung zu bestimmen. Wir verwenden die Standardauflösung von 96dpi.");
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e1) {
				// IGNORE
			}
		}
		return resolution;
	}

}
