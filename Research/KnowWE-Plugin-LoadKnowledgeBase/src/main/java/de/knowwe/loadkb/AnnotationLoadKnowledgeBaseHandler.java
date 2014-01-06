package de.knowwe.loadkb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.we.knowledgebase.D3webCompiler;
import de.d3web.we.knowledgebase.KnowledgeBaseType;
import de.d3web.we.reviseHandler.D3webHandler;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.core.wikiConnector.WikiConnector;

public class AnnotationLoadKnowledgeBaseHandler extends D3webHandler<KnowledgeBaseType> {

	private static final String TMP_LOADED_KB_PATH = "/tmp/loadedKB";
	public static final String ANNOTATION_LOAD = "load";
	private final Collection<Message> messages = new LinkedList<Message>();

	@Override
	public Collection<Message> create(D3webCompiler compiler, Section<KnowledgeBaseType> section) {

		String[] annotations = KnowledgeBaseType.getAnnotations(
				section,
				AnnotationLoadKnowledgeBaseHandler.ANNOTATION_LOAD);

		messages.clear();

		if (annotations.length == 0) {
			// messages.add(new Message(Message.Type.ERROR,
			// "You did not specify a file!"));
			return messages;
		}

		if (annotations.length > 1) {
			messages.add(new Message(Message.Type.WARNING,
					"You used @load twice. Only the first one will be executed!"));
		}

		String filename = annotations[0];

		try {
			KnowledgeBase kb = loadKnowledgeBaseFromFile(compiler, section, filename);

			if (kb != null) {
				D3webUtils.getD3webCompiler(section).setKnowledgeBase(kb);
			}
		}

		catch (FileNotFoundException e2) {
			messages.add(new Message(Message.Type.WARNING,
					"The file you tried to load does not exist!"));

		}

		catch (IOException e) {
			messages.add(new Message(Message.Type.ERROR, e.getStackTrace().toString()));
		}

		return messages;
	}

	private KnowledgeBase loadKnowledgeBaseFromFile(D3webCompiler compiler, Section<?> section, String filename) throws IOException, FileNotFoundException {
		WikiAttachment attachment = KnowWEUtils.getAttachment(section.getTitle(),
				filename);

		if (attachment == null) {
			messages.add(new Message(Message.Type.WARNING,
					"The file you tried to load does not exist!"));
			return null;
		}

		WikiConnector wc = Environment.getInstance().getWikiConnector();

		// (temporary) save location for a file because pm.load(arg) expects a
		// file instead of an attachment
		String pathname = wc.getKnowWEExtensionPath() + TMP_LOADED_KB_PATH;
		if (!new File(pathname).isDirectory()) {
			boolean directoryCreated = new File(pathname).mkdir();
			if (!directoryCreated) {
				// System.err.println("Directory wasn't created");
			}
		}

		File kbFile = createKBFile(attachment, pathname);

		PersistenceManager pm = PersistenceManager.getInstance();

		KnowledgeBase kb = pm.load(kbFile);

		kbFile.delete();

		return kb;
	}

	private File createKBFile(WikiAttachment attachment, String pathname) throws IOException, FileNotFoundException {
		File kbFile = new File(pathname,
				UUID.randomUUID().toString());

		InputStream inputStream = attachment.getInputStream();
		OutputStream out = new FileOutputStream(kbFile);
		byte buf[] = new byte[1024];
		int len;
		while ((len = inputStream.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();
		inputStream.close();
		return kbFile;
	}
}
