package de.d3web.proket.d3web.output.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import de.d3web.core.knowledge.Resource;
import de.d3web.proket.utils.FileUtils;

public class ImageHandler {

	public static BufferedImage getResourceAsBUI(Resource r) {
		BufferedImage bui = null;
		if (r != null) {

			Image image = null;
			try {
				InputStream in = r.getInputStream();
				image = ImageIO.read(in);
				bui = toBufferedImage(image);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return bui;
	}

	public static void writeImageToFolder(Resource r) throws IOException {
		InputStream is = r.getInputStream();
		try {
			BufferedImage bi = ImageIO.read(is);
			URL resourceUrl = FileUtils.class.getResource("/../../kbImg/");
			File f = new File(resourceUrl.toURI() + "/dummy.txt");
			f.createNewFile();
			ImageIO.write(bi, "jpg", f);
		}
		catch (URISyntaxException e) {
			throw new IOException("internal error accessing ressource", e);
		}
		catch (NullPointerException e) {
			throw new IOException("internal error accessing ressource", e);
		}
		finally {
			is.close();
		}
	}

	public static BufferedImage toBufferedImage(final Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		// if (image instanceof VolatileImage)
		// return ((VolatileImage) image).getSnapshot();
		// loadImage(image);
		final BufferedImage buffImg = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_CUSTOM);
		final Graphics2D g2 = buffImg.createGraphics();
		g2.drawImage(image, null, null);
		g2.dispose();
		return buffImg;
	}
}
