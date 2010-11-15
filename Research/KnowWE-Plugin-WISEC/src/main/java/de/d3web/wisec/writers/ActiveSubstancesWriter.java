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

import de.d3web.wisec.converter.WISECExcelConverter;
import de.d3web.wisec.model.WISECModel;

public class ActiveSubstancesWriter extends WISECWriter {

	public static final String FILENAME = WISECExcelConverter.FILE_PRAEFIX + "Active+Substances";

	public ActiveSubstancesWriter(WISECModel model, String outputDirectory) {
		super(model, outputDirectory);
	}

	@Override
	public void write() throws IOException {
		Writer writer = ConverterUtils.createWriter(this.outputDirectory + FILENAME + ".txt");
		writeBreadcrumb(writer);

		StringBuffer b = new StringBuffer();
		b.append("!!! Active Substances %%(font-size:small;font-weight:normal)- customizable list of substances for further consideration. For every substance additional informations are available by using the links. \n\n");

		// open the zebra and the sortable table
		b.append("%%zebra-table\n%%sortable\n");

		// write the data
		b.append("|| CAS_No || EC_no || IUPAC_name || Chemical_name  \n");
		for (String substanceName : model.getActiveSubstances()) {
			b.append("| [" + substanceName + " | "
					+ ConverterUtils.cleanWikiLinkSpaces(SubstanceInfoWriter.getWikiFileNameFor(substanceName))
					+ "] | "
					+ ConverterUtils.asStringNoBraces(model.getECNamesFor(substanceName)) + "| "
					+ ConverterUtils.asStringNoBraces(model.getIUPACFor(substanceName)) + " | "
					+ ConverterUtils.asStringNoBraces(model.getChemNamesFor(substanceName)) + "\n");
		}

		writer.write(b.toString());
		// close the zebra and the sortable table
		writer.append("/%\n/%\n");
		writer.close();

	}

	@Override
	protected void writeBreadcrumb(Writer writer) throws IOException {
		super.writeBreadcrumb(writer);
		writer.write(" > [List of Substances|"
				+ ConverterUtils.cleanWikiLinkSpaces(AllSubstancesOverviewWriter.FILENAME) + "] > "
				+ "Active Substances\n\n");
	}

}
