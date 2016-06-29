/*
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

package de.knowwe.lexiconinterface.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL Connector for OpenThesaurus mysql dump
 *
 * Created by Daniel Knöll on 20.11.2014.
 */
public class MySQLConnector {

    private final static Logger log = LoggerFactory.getLogger(MySQLConnector.class);

    Connection connection = null;

    public MySQLConnector() {
    }

    public void connect() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("MySQL JDBC Driver not found");
        }
        log.info("Connecting to MySQL database");
        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/openthesaurus", "root", "test123");

        } catch (SQLException e) {
            throw new SQLException("Connection to MySQL database failed");
        }

        if (connection != null) {
            log.info("Connection to the MySQL database established");
        } else {
            log.error("Failed to connect to the database!");
        }



    }

    public void disconnect() throws SQLException {
        if (!connection.isClosed()){
            try {
                connection.close();
                log.info("Connection to the MySQL database was closed");
            } catch (SQLException e) {
                throw new SQLException("Connection to MySQL database can not be closed");
            }
        }else{
            log.debug("Connection to the MySQL database was already closed");
        }
    }

    public List<String> getSynonyms(String word){
        List<Integer> synsetids = new ArrayList<>();
       // int synsetid = 0;
        ArrayList<String> synonyms = new ArrayList<>();
        try {
            connect();
            Statement stmt = connection.createStatement();

            //get the id of the synsets
            ResultSet rs = stmt.executeQuery( "SELECT synset_id FROM term WHERE word LIKE '"+word+"'" );
            while ( rs.next() ) {
                synsetids.add(Integer.parseInt(rs.getString(1)));
            }

            //find all other words with the same synset id
            for (int synsetid:synsetids) {
                rs = stmt.executeQuery("SELECT * FROM term WHERE synset_id = " + synsetid);
                while (rs.next()) {
                    if (!rs.getString(11).equals(word)) {
                        synonyms.add(rs.getString(11));
                    }
                }
            }
            rs.close();

            stmt.close();
            disconnect();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return synonyms;


    }


    public List<String> getAntonyms(String word) {
        List<Integer> ids = new ArrayList<>();
        List<Integer> antonymIds = new ArrayList<>();
        ArrayList<String> antonyms = new ArrayList<>();
        try {
            connect();
            Statement stmt = connection.createStatement();

            //get the ids of the term
            ResultSet rs = stmt.executeQuery("SELECT id FROM term WHERE word LIKE '" + word + "'");
            while ( rs.next() ) {
                ids.add(Integer.parseInt(rs.getString(1)));
            }

            for (int id: ids) {
                //get ids of the antonyms as term id
                rs = stmt.executeQuery("SELECT term_id FROM term_link WHERE link_type_id = 1 AND target_term_id = " + id);
                while (rs.next()) {
                    if (!antonymIds.contains(rs.getString(1))) {
                        antonymIds.add(Integer.parseInt(rs.getString(1)));
                    }
                }

                //get ids of the antonyms as target term id
                rs = stmt.executeQuery("SELECT target_term_id FROM term_link WHERE link_type_id = 1 AND term_id = " + id);
                while (rs.next()) {
                    if (!antonymIds.contains(rs.getString(1))) {
                        antonymIds.add(Integer.parseInt(rs.getString(1)));
                    }
                }
            }

            //get words from id
            for (int id : antonymIds) {
                rs = stmt.executeQuery("SELECT word FROM term WHERE id = " + id);
                while ( rs.next() ) {
                    antonyms.add(rs.getString(1));
                }

            }

            rs.close();
            stmt.close();
            disconnect();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return antonyms;
    }


    public List<String> getAllCategories() {
        ArrayList<String> categories = new ArrayList<>();
        try {
            connect();
            Statement stmt = connection.createStatement();

            //get all categories
            ResultSet rs = stmt.executeQuery("SELECT category_name FROM category");
            while (rs.next()) {
                categories.add(rs.getString(1));
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

}
