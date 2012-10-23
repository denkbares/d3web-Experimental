/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.utils;

import de.d3web.proket.utils.GlobalSettings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mafre
 */
public class Utils {
    public static List<File> getFileList(String folderPath) {
        
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files == null) {
            return null;
        }
        List<File> filesList = new ArrayList<File>(files.length);
        for (File file : files) {
            if (file.isFile() && !file.getName().contains("empty")) {
                filesList.add(file);
            }
        }
        return filesList;
    }

}
