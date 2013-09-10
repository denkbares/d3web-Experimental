package de.d3web.proket.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// TODO: move to d3web packages
public class DB {

	public static boolean isValidToken(String token, String email) {
		boolean ok = false;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConMgr.getInstance().getConnection();

			stmt = con.prepareStatement(
					"SELECT *" +
							" FROM `tokens`" +
							" WHERE `token` = ?" +
							";");
			((PreparedStatement) stmt).setString(1, token);

			rs = ((PreparedStatement) stmt).executeQuery();
			if (rs.next()) {
				ok = rs.getString("email").equals(email);
			}

		}
		catch (Exception mye) {
			mye.printStackTrace();
		}
		finally {
			if (rs != null) try {
				rs.close();
			}
			catch (SQLException e) { /**/
			}
			finally {
				rs = null;
			}
			if (stmt != null) try {
				stmt.close();
			}
			catch (SQLException e) { /**/
			}
			finally {
				stmt = null;
			}
			if (con != null) try {
				con.close();
			}
			catch (SQLException e) { /**/
			}
			finally {
				con = null;
			}
		}

		return ok;
	}

	static void addToken(String token, String email) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConMgr.getInstance().getConnection();

			stmt = con.prepareStatement(
							"INSERT INTO `tokens`" +
									" (`token`, `email`)" +
									" VALUES(?, ?)" +
									";");
			stmt.setString(1, token);
			stmt.setString(2, email);
			stmt.executeUpdate();

		}
		catch (Exception mye) {
			mye.printStackTrace();
		}
		finally {
			if (rs != null) try {
				rs.close();
			}
			catch (SQLException e) { /**/
			}
			finally {
				rs = null;
			}
			if (stmt != null) try {
				stmt.close();
			}
			catch (SQLException e) { /**/
			}
			finally {
				stmt = null;
			}
			if (con != null) try {
				con.close();
			}
			catch (SQLException e) { /**/
			}
			finally {
				con = null;
			}
		}
	}

	static void removeToken(String token) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConMgr.getInstance().getConnection();

			stmt = con.createStatement();

			stmt.executeUpdate("DELETE FROM `tokens` WHERE `token`='" + token + "';");

		}
		catch (Exception mye) {
			mye.printStackTrace();
		}
		finally {
			if (rs != null) try {
				rs.close();
			}
			catch (SQLException e) { /**/
			}
			finally {
				rs = null;
			}
			if (stmt != null) try {
				stmt.close();
			}
			catch (SQLException e) { /**/
			}
			finally {
				stmt = null;
			}
			if (con != null) try {
				con.close();
			}
			catch (SQLException e) { /**/
			}
			finally {
				con = null;
			}
		}
	}

}
