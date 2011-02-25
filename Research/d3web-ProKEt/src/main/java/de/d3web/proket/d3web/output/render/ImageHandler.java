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

	public static void writeImageToFolder(Resource r) {
		// File f = null;
		InputStream is = null;
		try {
			is = r.getInputStream();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BufferedImage bi = null;
		try {
			bi = ImageIO.read(is);
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		URL resourceUrl = null;
		resourceUrl = FileUtils.class.getResource("/../../kbImg/");
		System.out.println(resourceUrl);
		File f = null;
		try {
			System.out.println(resourceUrl.toURI());

			f = new File(resourceUrl.toURI() + "/dummy.txt");
			System.out.println(f);
		}
		catch (URISyntaxException e) {

		}
		catch (NullPointerException e) {

		}

		try {
			f.createNewFile();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println(f.exists());
		System.out.println(f);

		try {
			ImageIO.write(bi, "jpg", f);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
