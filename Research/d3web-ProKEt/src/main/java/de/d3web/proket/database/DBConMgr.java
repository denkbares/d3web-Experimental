package de.d3web.proket.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.sql.DataSource;

public class DBConMgr {

	private static DBConMgr myInstance = null;
	
	private String datasourceContextName;
	private String datasourceDataSourceName;
	
	private String manualAccessDriver;
	private String manualAccessURL;
	private String manualAccessUser;
	private String manualAccessPass;
	
	static DBConMgr getInstance() {
		if (myInstance == null)
			myInstance = new DBConMgr(Main.getBundle());
		return myInstance;
	}
	
	/**
	 * @param bundleName
	 */
	private DBConMgr(ResourceBundle b) {
		datasourceContextName = b.getString("datasource.context");
		datasourceDataSourceName = b.getString("datasource.datasource");
		
		manualAccessDriver = b.getString("manualaccess.drivername");
		manualAccessURL = b.getString("manualaccess.url");
		manualAccessUser = b.getString("manualaccess.user");
		manualAccessPass = b.getString("manualaccess.pass");
	}
	
	Connection getConnection() {
		Connection res = null;
		int i = 0;
		while (res == null && i < 1000) {
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				con = _getConnection();
				if (con.isClosed())
					throw new Exception();
				stmt = con.createStatement();
				rs = stmt.executeQuery("SELECT SYSDATE();");
				if (rs.next())
					res = con;
			} catch (Exception mye) {
				if (con != null)
					try { con.close(); } catch (SQLException e) { /**/ } finally { con = null; }
			} finally {
				if (rs != null)
					try { rs.close(); } catch (SQLException e) { /**/ } finally { rs = null; }
				if (stmt != null)
					try { stmt.close(); } catch (SQLException e) { /**/ } finally { stmt = null; }
			}
			if (res == null)
				i++;
		}
		if (res == null)
			System.err.println("ERROR: DBConMgr: giving up ...");
		else if (i > 0)
			System.err.println("ERROR: DBConMgr: had to try " + i + " connections ...");
		
		return res;
	}

	private DataSource ds = null;
	
	private Connection _getConnection() {
		try {
			if (ds == null) {
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup(datasourceContextName);
				ds = (DataSource) envCtx.lookup(datasourceDataSourceName);
			}
			return ds.getConnection();
		} catch (NoInitialContextException ex) {
			// this happens with requests outside of tomcat
			// System.err.println(ex);
			try {
				Class.forName(manualAccessDriver).newInstance();
				Connection con =
					DriverManager.getConnection(
							manualAccessURL,
							manualAccessUser,
							manualAccessPass);
				return con;
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} catch (NamingException ex) {
			ex.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
