package de.d3web.proket.d3web.output.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import de.d3web.core.knowledge.Resource;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.utils.FileUtils;

public class ImageHandler {

	public static Resource getResource(String resourceName) {
		Resource resource = null;
		D3webConnector d3wc = D3webConnector.getInstance();
		if (d3wc.getKb() != null) {
			resource = d3wc.getKb().getResource(resourceName);
			if (resource != null) {
				System.out.println(resource.getPathName());

			}
		}
		return resource;
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
}
