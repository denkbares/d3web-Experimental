/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.utils;

import de.d3web.core.session.Session;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;

/**
 * Tailored Comparator Class for comparing two d3web case file objects as to
 * which one was created earlier by using the creation date as stored within the
 * d3web case file
 *
 * @author Martina Freiberg @date Dec 2012
 */
public class FileNameComparator implements Comparator<File> {

    @Override
    public int compare(File f1, File f2) {
        return f1.getName().compareTo(f2.getName());
    }

}
