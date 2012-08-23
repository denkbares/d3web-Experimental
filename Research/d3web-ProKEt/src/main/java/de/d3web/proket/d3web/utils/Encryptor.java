/**
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.proket.d3web.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class for some basic encryption / decryption functionalities
 * 
 * @author Martina Freiberg
 * @date 23/08/2012
 */
public class Encryptor {
    
    // add some spice to the encryption ;-)
    public static final String SALT = 
            "Randomly%Chosen$SaltyValue#WithSpecialCharacters12ß@$0@4&#%^$*8";
    
    /**
     * Return an anonymized Filename for a given clearname
     * 
     * @param clearFilename The clearname
     * @return The anonymized Filename
     */
    public static String getAnonymizedFilename(String clearFilename) {
       
        // add the spice to the clearname
        String toencrypt = clearFilename + SALT;
        byte[] bytesOfClearname = null;
        String encrypted = null;

        // get byte representation of input String = clearname of file
        try {
            bytesOfClearname = toencrypt.getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
        if(bytesOfClearname==null){
            bytesOfClearname = toencrypt.getBytes();
        }
        
        try {
            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(bytesOfClearname, 0, toencrypt.length());

            //Converts message digest value in base 16 (hex) 
            encrypted = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        return encrypted;
    }
}
