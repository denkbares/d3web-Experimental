/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 * 
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.rdf2go;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.impl.AbstractModelFactory;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.URI;

import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.util.GraphUtil;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigSchema;
import org.openrdf.repository.config.RepositoryConfigUtil;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;

public class RepositoryModelFactory extends AbstractModelFactory {

	public Model createModel(Properties properties)
			throws ModelRuntimeException {
		return new RepositoryModel(createRepository(properties));
	}

	public Model createModel(URI contextURI)
			throws ModelRuntimeException {
		return new RepositoryModel(contextURI, createRepository(null));
	}

//	public ModelSet createModelSet(Properties properties)
//			throws ModelRuntimeException {
//		return new RepositoryModelSet(createRepository(properties));
//	}

	private Repository createRepository(Properties properties)
			throws ModelRuntimeException {
		// find out if we need reasoning
		String reasoningProperty = properties == null ? null : properties.getProperty(REASONING);
		boolean owlimReasoning = Reasoning.owl.toString().equalsIgnoreCase(reasoningProperty);

		// create a Sail stack		
		Repository repository = null;

		if (!owlimReasoning) {
			Sail sail = new MemoryStore();

			boolean rdfsReasoning = Reasoning.rdfs.toString().equalsIgnoreCase(reasoningProperty);

			if (rdfsReasoning) {
				sail = new ForwardChainingRDFSInferencer((MemoryStore) sail);
			}
			// create a Repository
			repository = new SailRepository(sail);
			try {
				repository.initialize();
			}
			catch (RepositoryException e) {
				throw new ModelRuntimeException(e);
			}
		}
		else {
			HashMap<String, String> settings = new HashMap<String, String>();
			
			String path = KnowWEEnvironment.getInstance().getKnowWEExtensionPath();
			String ontfile = path + File.separatorChar + "knowwe_base.owl";
			settings.put("ontfile", ontfile);
			String reppath = System.getProperty("java.io.tmpdir") + File.separatorChar
					+ "repository" + (new Date()).toString().hashCode();

			settings.put("reppath", reppath);
			String config_file = path + File.separatorChar + "owlim.ttl";
			settings.put("config_file", config_file);
			File rfile = new File(reppath);
			delete(rfile);
			rfile.mkdir();

			settings.put("basens", Rdf2GoCore.basens);
			
			if (!settings.containsKey("ontfile")
					|| !settings.containsKey("reppath")
					|| !settings.containsKey("basens")
					|| !settings.containsKey("config_file")) {
				return null;
			}
			ontfile = null;
			
			// +"0" to not use the same path as the old semantic core plugin
			reppath = settings.get("reppath");//+"0";
			config_file = null;
			try {
				ontfile = new File(settings.get("ontfile")).getCanonicalPath();
				config_file = new File(settings.get("config_file")).getCanonicalPath();
			}
			catch (IOException e) {
				System.out.println(settings.get("ontfile"));
				e.printStackTrace();
			}
			String basens = settings.get("basens");
			File file = new File(ontfile);

			try {

				Repository systemRepo = null;

				RepositoryManager man = new LocalRepositoryManager(new File(reppath));
				man.initialize();
				systemRepo = man.getSystemRepository();
				ValueFactory vf = systemRepo.getValueFactory();
				Graph graph = new GraphImpl(vf);

				RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE, vf);
				rdfParser.setRDFHandler(new StatementCollector(graph));
				rdfParser.parse(new FileReader(config_file),
						RepositoryConfigSchema.NAMESPACE);

				Resource repositoryNode = GraphUtil.getUniqueSubject(graph,
						RDF.TYPE, RepositoryConfigSchema.REPOSITORY);
				RepositoryConfig repConfig = RepositoryConfig.create(graph,
						repositoryNode);

				repConfig.validate();
				RepositoryConfigUtil.updateRepositoryConfigs(systemRepo, repConfig);
				Literal _id = GraphUtil.getUniqueObjectLiteral(graph,
						repositoryNode, RepositoryConfigSchema.REPOSITORYID);
				repository = man.getRepository(_id.getLabel());
				RepositoryConnection repositoryConn = repository.getConnection();
				repositoryConn.setAutoCommit(true);
				BNode context = repositoryConn.getValueFactory().createBNode(
						"rootontology");
				repositoryConn.add(file, basens, RDFFormat.RDFXML, context);
				repositoryConn.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return repository;
	}
	
	private void delete(File f) {
		File[] list = f.listFiles();
		if (list != null) {
			for (File c : list) {
				if (c.isDirectory()) {
					delete(c);
					c.delete();
				}
				else {
					boolean r = c.delete();
					if (!r) {
						// error
					}
				}
			}
		}
	}

	@Override
	public ModelSet createModelSet(Properties p) throws ModelRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}
}
