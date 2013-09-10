/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.medicine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbConnection {

	private Connection connect = null;

	public DbConnection(String server, String database, String username, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Setup the connection with the DB
		try {
			if (password.equals("")) {
				connect = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database
						+ "?"
						+ "user=" + username);
			}
			else {
				connect = DriverManager
						.getConnection("jdbc:mysql://" + server + "/" + database + "?" + "user="
								+ username + "&password=" + password);
			}
			connect.setAutoCommit(true);

		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void write(String sql) throws SQLException {
		// This will load the MySQL driver, each DB has its own driver

		PreparedStatement s;
		s = connect.prepareStatement(sql);
		s.execute();

	}

	public ResultSet readDatabase(String query) throws SQLException {
		return connect.createStatement().executeQuery(query);
	}

	public void close() {
		try {
			connect.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
