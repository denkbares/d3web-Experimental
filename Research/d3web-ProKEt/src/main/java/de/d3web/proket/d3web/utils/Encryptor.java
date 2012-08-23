/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    
    public static final String SALT = 
            "Randomly%Chosen$SaltyValue#WithSpecialCharacters12ß@$0@4&#%^$*8";
    
    public static String getAnonymizedFilename(String clearFilename) {
       
        String toencrypt = clearFilename + SALT;
        String anonFilename = "";
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
