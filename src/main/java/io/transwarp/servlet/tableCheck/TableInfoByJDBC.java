package io.transwarp.servlet.tableCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import io.transwarp.bean.TableBean;
import io.transwarp.connTool.JDBCUtil;
import io.transwarp.information.ClusterInformation;
import io.transwarp.information.Constant;

public class TableInfoByJDBC extends ClusterInformation{

	private static final Logger LOG = Logger.getLogger(TableInfoByJDBC.class);
	private String url;
	private String username;
	private String password;
	private String servicename;
	
	public TableInfoByJDBC(String servicename, String url, String username, String password) {
		this.servicename = servicename;
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public void getTableInfo() {
        LOG.info("get table info of service : " + servicename);
		Connection conn = null;
		try {
			conn = getConnection();
		}catch(Exception e) {
			e.printStackTrace();
			LOG.error("get Connection of jdbc error, connection info is \n url : " + url + 
					", username : " + username + ", password : " + password);
			return;
		}
		Vector<TableBean> tables = new Vector<TableBean>();
		try {
			PreparedStatement pstat = conn.prepareStatement(Constant.QUERY_TABLE_MYSQL);
			ResultSet rs = pstat.executeQuery();
			while(rs.next()) {
				TableBean table = new TableBean();
				table.setDatabase_name(rs.getString("database_name"));
				table.setOwner_name(rs.getString("owner_name"));
				table.setTable_format(rs.getString("table_format"));
				table.setTable_location(rs.getString("table_location"));
				table.setTable_name(rs.getString("table_name"));
				table.setTable_type(rs.getString("table_type"));
				table.setTransactional(rs.getString("transactional"));
				tables.add(table);
			}
			ClusterInformation.tableInfos.put(servicename, tables);
			pstat.close();
			LOG.info("check table number is : " + tables.size());
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	protected Connection getConnection() throws Exception {
		String[] items = url.split("\\?");
		if(items.length == 0 || items[0].startsWith("jdbc") == false) {
			throw new Exception("db connection string error");
		}
		url = items[0];
		Connection conn = JDBCUtil.getConnection(url, username, password);
		return conn;
	}
	
}
