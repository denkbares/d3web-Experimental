/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.utils;

import de.d3web.proket.utils.SystemLoggerUtils;
import java.io.File;
import java.io.IOException;
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

    public static File checkCreateDir(String dirpath) {

        File pathF = null;
        try {
            pathF = new File(dirpath);
            if (!pathF.exists()) {
                pathF.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        }

        return pathF;
    }

    public static File checkCreateFile(String filepath) {
        System.err.println(filepath);
        File fileF = null;
        try {
            fileF = new File(filepath);
            if (!fileF.exists()) {
                fileF.createNewFile();
            }
        } catch (IOException ioe) {
            SystemLoggerUtils.getExceptionLoggerStream().println("CheckCreateFile: filepath= " + filepath + "\n" );
            ioe.printStackTrace(SystemLoggerUtils.getExceptionLoggerStream());
        }
        return fileF;
    }
}
