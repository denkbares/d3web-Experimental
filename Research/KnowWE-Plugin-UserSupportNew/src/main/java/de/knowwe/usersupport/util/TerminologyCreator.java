/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.usersupport.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 
 * @author Johannes Dienst
 * @created 06.03.2012
 */
public class TerminologyCreator
{


	/**
	 * 
	 * @created 06.03.2012
	 */
	public static void main(String[] args)
	{
		String wordsPath = "C:/Users/ManiaC/Vorlesungen/Diplomarbeit/Fallstudienfiles/words.txt";
		String outPath = "C:/Users/ManiaC/Vorlesungen/Diplomarbeit/Fallstudienfiles/terminologie";

		List<Integer> terminologies = new ArrayList<Integer>();
		//		terminologies.add(10);
		//		terminologies.add(25);
		//		terminologies.add(50);
		//		terminologies.add(100);
		//		terminologies.add(150);
		//		terminologies.add(200);
		//		terminologies.add(300);
		//		terminologies.add(500);
		//		terminologies.add(750);
		terminologies.add(2000);

		try
		{
			File wordsFile = new File(wordsPath);
			FileInputStream in = new FileInputStream(wordsFile);
			List<String> words = TerminologyCreator.readWords(in);
			in.close();

			for (Integer i : terminologies)
			{
				File outFile = new File(outPath+i+".txt");
				TerminologyCreator.createDataSet(words, outFile, i);
			}

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static List<String> readWords(FileInputStream in) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		List<String> words = new ArrayList<String>();

		String s;
		while ( (s=reader.readLine()) != null )
		{
			String[] wordArray = s.split(" ");
			for (String b : wordArray)
			{
				if (b.length() > 3 && !words.contains(b))
				{
					words.add(b);
				}

			}
		}
		return words;
	}

	public static void wordsToFile(List<String> words) throws IOException
	{
		String wordsPathCleaned = "C:/Users/ManiaC/Vorlesungen/Diplomarbeit/Fallstudienfiles/wordsCleaned.txt";
		File outFile = new File(wordsPathCleaned);
		BufferedWriter w = new BufferedWriter(new FileWriter(outFile));
		for (String b : words)
		{
			w.write(b + " ");
			w.newLine();
		}
		w.flush();
		w.close();
	}

	public static void createDataSet(List<String> words, File out, int objCount)
	{
		try
		{
			StringBuilder buildi = new StringBuilder();
			BufferedWriter w = new BufferedWriter(new FileWriter(out));

			w.append("%%Question");
			w.newLine();

			Random rnd = new Random();
			for (int i = 0; i < objCount; i++)
			{
				buildi.append(TerminologyCreator.getTerm(words, rnd) + " [oc]\r\n");
				buildi.append("-" + TerminologyCreator.getTerm(words, rnd) + "\r\n");
				buildi.append("-" + TerminologyCreator.getTerm(words, rnd) + "\r\n");
			}

			w.write(buildi.toString());

			w.newLine();
			w.append("@package: demo");
			w.newLine();
			w.append("%");
			w.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * @created 06.03.2012
	 * @param words
	 * @return
	 */
	private static String getTerm(List<String> words, Random rnd)
	{
		StringBuilder buildi = new StringBuilder();

		// build a phrase ?
		int phrase = rnd.nextInt(2);
		if (phrase == 1)
		{
			int phraseLength = rnd.nextInt(3)+2;
			for (int j = 0; j < phraseLength; j++)
			{
				int k = rnd.nextInt(words.size());
				if (k == words.size()) k--;
				buildi.append(" " + words.get(k));
			}
			buildi.deleteCharAt(0);
			return buildi.toString();
		}

		int i = rnd.nextInt(words.size());
		if (i == words.size()) i--;

		return words.get(i);
	}
}
