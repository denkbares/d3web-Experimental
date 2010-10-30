/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.wisec.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.wisec.converter.WISECExcelConverter;
import de.d3web.wisec.model.SourceList;
import de.d3web.wisec.model.SubstanceList;
import de.d3web.wisec.model.WISECModel;

public class SourceListWriter extends WISECWriter {

	private static String filePraefix = WISECExcelConverter.FILE_PRAEFIX + "SOL+";
	private static Map<String, String> listID2fileName = new HashMap<String, String>();

	private static String[] OVERVIEW_ATTR = new String[] {
			"ID", "Name" };

	public SourceListWriter(WISECModel model, String outputDirectory) {
		super(model, outputDirectory);
	}

	@Override
	public void write() throws IOException {
		int counter = 0;
		for (SourceList list : model.sourceLists) {
			counter++;
			list.filename = filePraefix + counter;
			listID2fileName.put(list.getId(), list.filename);
			Writer writer = ConverterUtils.createWriter(this.outputDirectory + list.filename
					+ ".txt");
			write(list, writer);
			writer.close();
		}
	}

	private void write(SourceList list, Writer w) throws IOException {
		writeBreadcrumb(w, list);

		w.write("!!! " + list.getName() + "\n\n");

		// w.write("!! Attributes \n\n");
		for (String attribute : list.getAttributes()) {
			String value = list.get(attribute);
			if (value != null && value.length() > 0) {
				w.write("|| " + attribute + " | " + ConverterUtils.clean(value) + " \n");
			}
		}

		Collection<String> lists = model.getListsWithSource(list.getId());
		if (!lists.isEmpty()) {
			w.write("!! Lists\n\n");
			// w.write(SubstanceListsOverviewWriter.writeTableHeader() + "\n");
			// write header
			w.write("%%zebra-table\n%%sortable\n");
			for (String headerName : OVERVIEW_ATTR) {
				w.write("|| " + headerName + " ");
			}
			w.write("|| Count\n");
			List<String> sortedLists = new ArrayList<String>(lists);
			Collections.sort(sortedLists);
			for (String listname : sortedLists) {
				SubstanceList substancelist = model.getListWithName(listname);
				if (substancelist != null && substancelist.getId() != null
						&& !substancelist.getId().isEmpty()) {
					String line = SubstanceListsOverviewWriter.generateOverviewLineFor(
							substancelist,
							OVERVIEW_ATTR)
							+ " | " + substancelist.substances.size()
							+ "\n";
					w.write(line);
				}
				else {
					System.err.println("Substance lists with name " + listname + " not found.");
				}
			}
			w.write("/%\n/%\n");

			w.write("[(+) Add list|Dummy]\n");
		}
	}

	protected void writeBreadcrumb(Writer writer, SourceList list) throws IOException {
		super.writeBreadcrumb(writer);
		writer.append(" > [Index of Sources|"
				+ ConverterUtils.cleanWikiLinkSpaces(SourceListOverviewWriter.FILENAME) +
				"] > " + list.getName());
		writer.append("\n\n");
	}

	public static String getWikiFilename(String idOfList) {
		return listID2fileName.get(idOfList);
	}
}
