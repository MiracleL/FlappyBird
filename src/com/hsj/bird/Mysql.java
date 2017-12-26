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

//�������ݿ��

public class Mysql {
	
	private Connection conn;// ���ݿ����Ӷ���

	private PreparedStatement stmt;// Ԥ�������
	
	private CallableStatement  sc;

	private ResultSet rs;// ���������
	
	private String dbDriver = "com.mysql.jdbc.Driver";
	private String dbUrl = "jdbc:mysql://localhost:3306/flappy";
	private String dbUser ="root";
	private String dbPass ="000000";
	
	//�������ݿ�
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
	
	//���ӷ���
	public int addOneUser(int score) throws SQLException {

		// �����û�����ϸ����
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
	
	//��ѯ����
	public ArrayList<Integer> findAll() throws SQLException
	{
		//��ѯ
		ArrayList<Integer> array=new ArrayList<Integer>();
		
		String sql = "select * from scores"; 
		Connection conn = this.getConn();
		Statement stmt = (Statement) conn.createStatement(); //����Statement����
		
		ResultSet rs = (ResultSet) stmt.executeQuery(sql);//�������ݶ���
		
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
