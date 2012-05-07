/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.utils;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.proket.d3web.input.D3webUtils;
import java.io.File;
import java.io.IOException;

import de.d3web.plugin.JPFPluginManager;

/**
 *
 * @author mafre
 */
public class TestKBLoadingSaving {
 
    
    public static void main(String[] args) throws IOException{
       
        File kbFile;
        File libPath;
        // Paths here are relative to the WEB-INF/classes folder!!!
        // from the /specs/d3web folder
        kbFile = new File("/Users/mafre/TestKB/herniaNoTimeDB.d3web");
        // from the /lib folder
        libPath = new File("/Users/mafre/TestKB/lib");

        // initialize PluginManager
        File[] files = null;
        files = D3webUtils.getAllJPFPlugins(libPath);
        JPFPluginManager.init(files);
        PersistenceManager persistenceManager = PersistenceManager.getInstance();

        KnowledgeBase kb = persistenceManager.load(kbFile);
        persistenceManager.save(kb, new File("Users/mafre/TestKB/HALLO.d3web"));
    }
}
