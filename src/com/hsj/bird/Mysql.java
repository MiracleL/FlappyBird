package com.hsj.bird;

import java.awt.List;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.CallableStatement;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

//连接数据库的

public class Mysql {
	
	private Connection conn;// 数据库链接对象

	private PreparedStatement stmt;// 预编译对象
	
	private CallableStatement  sc;

	private ResultSet rs;// 结果集对象
	
	private String dbDriver = "com.mysql.jdbc.Driver";
	private String dbUrl = "jdbc:mysql://localhost:3306/flappy";
	private String dbUser ="root";
	private String dbPass ="000000";
	
	//连接数据库
	public Connection getConn()
	{
		Connection conn = null;
		try {
			Class.forName(dbDriver);
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		try {
			 conn = (Connection) DriverManager.getConnection(dbUrl,dbUser,dbPass);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return conn;
	}
	
	//增加分数
	public int addOneUser(int score) throws SQLException {

		// 增加用户的详细操作
		int i = 0;
		String sql = "insert into scores values(0,?)";
		Connection conn = this.getConn();
		try {
			PreparedStatement preStmt = (PreparedStatement) conn.prepareStatement(sql);
			preStmt.setInt(1, score);
			i = preStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		conn.close();
		return i;
	}
	
	//查询数据
	public ArrayList<Integer> findAll() throws SQLException
	{
		//查询
		ArrayList<Integer> array=new ArrayList<Integer>();
		
		String sql = "select * from scores"; 
		Connection conn = this.getConn();
		Statement stmt = (Statement) conn.createStatement(); //创建Statement对象
		
		ResultSet rs = (ResultSet) stmt.executeQuery(sql);//创建数据对象
		
		while (rs.next()){
			array.add(rs.getInt(2));
		}
		
		
		rs.close();
		stmt.close();
		conn.close();
		return array;
		
	}
		
	
//	public static void main(String[] args) throws SQLException
//	{
//		Mysql mysql=new Mysql();
//		mysql.addOneUser(11);
//		ArrayList<Integer> array=mysql.findAll();
//		
//		for(int i=0;i<array.size();i++)
//		{
//			System.out.println(array.get(i));
//		}
//	}
}
