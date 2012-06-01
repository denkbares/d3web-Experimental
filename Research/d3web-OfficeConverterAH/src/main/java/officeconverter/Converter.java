package officeconverter;

import officeconverter.filetypes.DocumentFile;
import officeconverter.filetypes.FileType;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Converter {
	
	private static URL getResource(String file) {
		URL resource = null;

		if (file.startsWith("file:/") || file.startsWith("http://"))
			try {
				resource = new URL(file);
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			}
		else
			resource = Converter.class.getResource(file);
		return resource;
	}
	
	public static void convertFile2File(File file, File toFile, Config config) {
		convertFile2File(file, toFile, null, config);
	}

	private static void convertFile2File(File file, File toFile, FileType fileType, Config config) {
		convertFile(
			file,
			toFile.getParentFile().getAbsoluteFile(),
			toFile.getName(),
			fileType,
			config
		);
	}
	
	private static void convertFile2Dir(File file, File dir) {
		convertFile2Dir(file, dir, null, new Config(null, null, null, null));
	}

	private static void convertFile2Dir(File file, File dir, FileType fileType, Config config) {
		convertFile(file, dir, file.getName() + config.getHtmlSuffix(), fileType, config);
	}

	private static void convertFile(File file, File toDir, String filename, FileType fileType, Config config) {
		DocumentFile caseFile = createDocumentFile(file, fileType, config);
		caseFile.saveAsHtml(toDir, filename, config);
	}

	private static DocumentFile createDocumentFile(File file, FileType fileType, Config c) {
		DocumentFile caseFile = null;
		try {
			String fileS = "file:///" + file.getAbsolutePath();
			if (fileType == null)
				fileType = FileType.getFileType(fileS);
			URL resource = getResource(fileS);
			caseFile = fileType.createFile(resource, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return caseFile;
	}

	public static void main(String[] args) {

		File file, toDir;

		if (args.length == 0) {
			System.out.println("usage: ConverterTester file [toDir]");
			System.exit(0);
		}

		file = new File(args[0]);
		if (!file.exists() || !file.canRead()) {
			System.err.println("can't read file '" + file + "'!");
			System.exit(-1);
		}

		if (args.length == 2) {
			toDir = new File(args[1]);
			if (!toDir.exists() || !toDir.canRead() || !toDir.isDirectory()) {
				System.err.println("can't open dir '" + toDir + "'!");
				System.exit(-1);
			}
		} else {
			toDir = file.getParentFile();
			if (!toDir.canRead()) {
				System.err.println("can't open dir '" + toDir + "'!");
				System.exit(-1);
			}
		}

		if (args.length > 2) {
			System.out.println("usage: ConverterTester file [toDir]");
			System.exit(-1);
		}

		convertFile2Dir(file, toDir);
	}

}
