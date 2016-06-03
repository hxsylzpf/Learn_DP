package mechanisms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class testBench {
	
	public static void main(String[] args) throws Exception {
		
		//TEST
//		HashMap<String, Integer> name_age = readHashMap_Name_Age();
//		double budget = 0.5;
//		double sensitivity = 1;
//		System.out.println(ExponentialMechanism.r(name_age, budget,sensitivity));
		
		//TEST
		//testExpMach();
		
		//TEST
		testLap();
		
		
	}
	
	public static void testLap () throws Exception {
		
		double e = 10;
		double sensitivity = 1;
		
		int stdAndStaffNum = readNumOfForeignStdAndStaff();
		System.out.println(LaplaceMechanism.addLaplaceNoise(stdAndStaffNum,e,sensitivity));
		
		int stdNum = readNumOfForeignStd();
		System.out.println(LaplaceMechanism.addLaplaceNoise(stdNum,e,sensitivity));
		
	}

	public static void testExpMach() throws ClassNotFoundException, SQLException {
		
		HashMap<String, Integer> name_age = readHashMap_Name_Age();
		
		double budget = 0.5;
		double sensitivity = 1;
		
		name_age =  (HashMap<String, Integer>) MapUtil.sortByValueDESC(name_age);
		System.out.println(name_age);
		
		HashMap<String,Double> hm_weight = ExponentialMechanism.getWeights_hm(name_age, budget, sensitivity);
		hm_weight = (HashMap<String, Double>) MapUtil.sortByValueDESC(hm_weight);
		System.out.println(hm_weight);
		
		HashMap<String,Double> hm_weight_prob = ExponentialMechanism.getNormalizedProb_helper_hm_double(hm_weight);
		hm_weight_prob = (HashMap<String, Double>) MapUtil.sortByValueDESC(hm_weight_prob);
		System.out.println(hm_weight_prob);
		
		
		int correctTimes = 0;
		int testTime = 200000;
		for (int i = 0; i < testTime; i++) {
			if (ExponentialMechanism.r(name_age, budget,sensitivity).equals("Masatoshi Yoshikawa")) {
				correctTimes++;
			}
		}
		
		System.out.println((double)correctTimes/testTime);
		
	}

	static HashMap<String, Integer> readHashMap_Name_Age() throws SQLException, ClassNotFoundException {

		HashMap<String, Integer> name_age = new HashMap<String, Integer>();

		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(db_file);

		Statement statement = conn.createStatement();

		String sql_foreigner_std = "select  NAME, \"APRX-AGE\" from ylab16";
		ResultSet rs = statement.executeQuery(sql_foreigner_std);
		while (rs.next()) {
			String name = rs.getString(1);
			int age = rs.getInt(2);
			name_age.put(name, age);

		}

		closeResultSet(rs);
		closeStatement(statement);
		closeConnection(conn);
		return name_age;

	}
	
	

	static String db_file = "jdbc:sqlite:./db/ylab16.sqlite";

	static int readNumOfForeignStd() throws SQLException, ClassNotFoundException {
		int num = 0;

		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(db_file);

		Statement statement = conn.createStatement();

		String sql_foreigner_std = "SELECT count(*) from ylab16 where \"FOREIGNER-OR-NOT\"=1 and POSITION=\"student\"";
		ResultSet rs = statement.executeQuery(sql_foreigner_std);
		num = rs.getInt(1);

		closeResultSet(rs);
		closeStatement(statement);
		closeConnection(conn);

		return num;

	}

	static int readNumOfForeignStdAndStaff() throws SQLException, ClassNotFoundException {
		int num = 0;

		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(db_file);

		Statement statement = conn.createStatement();

		String sql_foreigner_std = "SELECT count(*) from ylab16 where \"FOREIGNER-OR-NOT\"=1";
		ResultSet rs = statement.executeQuery(sql_foreigner_std);
		num = rs.getInt(1);

		closeResultSet(rs);
		closeStatement(statement);
		closeConnection(conn);

		return num;

	}

	ArrayList<Integer> readAgeIntArr() throws SQLException, ClassNotFoundException {
		ArrayList<Integer> ages = new ArrayList<Integer>();

		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(db_file);

		Statement statement = conn.createStatement();

		String sql_foreigner_std = "SELECT count(*) from ylab16 where \"FOREIGNER-OR-NOT\"=1 and POSITION=\"student\"";
		ResultSet rs = statement.executeQuery(sql_foreigner_std);
		while (rs.next()) {

			ages.add(rs.getInt(1));

		}

		closeResultSet(rs);
		closeStatement(statement);
		closeConnection(conn);

		return ages;

	}

	/**
	 * close connection
	 */
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * close Statement
	 */
	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
				stmt = null;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * close PreparedStaement 
	 */
	public static void closePreparedStatement(PreparedStatement pstmt) {
		if (pstmt != null) {
			try {
				pstmt.close();
				pstmt = null;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * close ResultSet
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}
