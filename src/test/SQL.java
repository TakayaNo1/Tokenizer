package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL{
	protected Connection con;
	protected Statement st;
	protected String url = "jdbc:mysql://localhost:3306/dictionary";
	protected String user = "root";         // データベース作成ユーザ名
	protected String password = "tkymkt0819";     // データベース作成ユーザパスワード
	
	public SQL(boolean autoCommit) {
		SQLinitialize(autoCommit);
	}
	
	private void SQLinitialize(boolean autoCommit){
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			con=DriverManager.getConnection(url,user,password);
			con.setAutoCommit(autoCommit);
			st=con.createStatement();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("mySQLでエラー発生");
			e.printStackTrace();
		}
	}
	
	public int executeAndGetGeneratedKey(String exe) {
		try {
//			System.out.println("SQL CONSOLE : "+exe);
			st.executeUpdate(exe, Statement.RETURN_GENERATED_KEYS);
			ResultSet result=st.getGeneratedKeys();
			result.next();
			return result.getInt(1);
		} catch (SQLException e) {
			return -1;
		}
	}
	public int execute(String exe){
		try {
//			System.out.println("SQL CONSOLE : "+exe);
			return st.executeUpdate(exe);
		} catch (SQLException e) {
			return -1;
		}
	}
	
	public ResultSet result(String rs) throws SQLException{
//		System.out.println("SQL CONSOLE : "+rs);
		return st.executeQuery(rs);
	}
	
	public void commit() throws SQLException {
		con.commit();
	}
	
	public void rollback() throws SQLException {
		con.rollback();
	}
	
	public void close(){
		try {
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws SQLException, InterruptedException{
		SQL sql=new SQL(false);
		String str="\\\"";
		int key=sql.st.executeUpdate("insert into test(word) values(\""+str+"\")", Statement.RETURN_GENERATED_KEYS);
		ResultSet set=sql.st.getGeneratedKeys();
		System.out.println(key);
		while(set.next()) {
			System.out.println(set.getInt(1));
		}
		sql.rollback();
//		System.out.println(SQL.execute("select*from test where word=\"hello\""));
//		ResultSet set=SQL.result("select*from test");
//		ResultSet set=SQL.result("select*from test where word=\"hello\"");
//		System.out.println(set.next()^set.next());
//		SQL.execute("insert into test(word) value(\"Halo\")");
//		SQL.close();
	}
}
