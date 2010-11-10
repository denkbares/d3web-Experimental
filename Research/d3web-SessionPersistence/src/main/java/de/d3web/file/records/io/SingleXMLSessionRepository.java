/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.file.records.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.records.DefaultSessionRepository;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.records.io.SessionPersistenceManager;

/**
 * This implementation of the SessionRepositoryPersistenceHandler interface can
 * handle exactly one XML file. This XML file has to contain the whole
 * SessionRepository.
 * 
 * @author Sebastian Furth (denkbares GmbH)
 * 
 */
public class SingleXMLSessionRepository extends DefaultSessionRepository {

	public void load(File file) throws IOException {
		this.load(file, new DummyProgressListener());
	}

	public void load(File file, ProgressListener listener) throws IOException {
		if (file == null) {
			throw new NullPointerException("File is null. Unable to load SessionRepository.");
		}
		if (!file.exists() || file.isDirectory()) {
			throw new IllegalArgumentException(
					"File doesn't exist or is a directory. Unable to load SessionRepository.");
		}
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		Collection<SessionRecord> records = spm.loadSessions(file, listener);
		for (SessionRecord sr : records) {
			add(sr);
		}
	}

	public void save(File file) throws IOException {
		this.save(file, new DummyProgressListener());
	}

	public void save(File file, ProgressListener listener) throws IOException {
		if (file == null) throw new NullPointerException(
				"File is null. Unable to save SessionRepository.");
		if (file.isDirectory()) {
			throw new IllegalArgumentException(
					"File is a directory. Unable to save SessionRepository.");
		}
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		spm.saveSessions(file, sessionRecords.values(), listener);
	}

}
