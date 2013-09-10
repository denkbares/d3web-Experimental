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
public class CaseCreationComparator implements Comparator<File> {

    @Override
    public int compare(File f1, File f2) {
        Date d1 = getCreationDateOfFileFromD3webCase(f1);
        Date d2 = getCreationDateOfFileFromD3webCase(f2);
       
        if (d1.before(d2)) {
            return -1;
        }
        if (d1.after(d2)) {
            return 1;
        }
        return 0;
    }

    /**
     * Retrieve the original creation date from the d3web file.
     * @param casefile the file containing the stored d3web case
     * @return Date the original creation date
     */
    
    private Date getCreationDateOfFileFromD3webCase(File casefile) {
        Session session = null;
        session = PersistenceD3webUtils.loadCase(casefile.getName());
        
	return session.getCreationDate();
        //return session.getLastChangeDate();
    }
}
