package lab09.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lab09.model.Cancer;
import util.JDBCutil;

public class CancerDao {

//	匯入所有資料
	public void saveCancer(List<Cancer> list) {

        String deletesql = "DELETE FROM CancerTable;";
        String insertsql = "INSERT INTO CancerTable(patientId, stage, race, vitalStatus, update_date, followDays) VALUES (?,?,?,?,?,?);";

        try (Connection conn = JDBCutil.getConnection();
             PreparedStatement deletesqltable = conn.prepareStatement(deletesql);
        	 PreparedStatement ps = conn.prepareStatement(insertsql)) {

            
            deletesqltable.executeUpdate();
            for (Cancer c : list) {             
                ps.setString(1, c.getPatientId());
                ps.setString(2, c.getStage());
                ps.setString(3, c.getRace());
                ps.setString(4, c.getVitalStatus());
                ps.setDate  (5, c.getUpdatetime());

                if (c.getFollowDays() != null) {
                    ps.setInt(6, c.getFollowDays());
                } else {
                    ps.setNull(6, java.sql.Types.INTEGER);
                }
                ps.addBatch();
            }

            ps.executeBatch();                 


            System.out.println("讀取資料完成，共 " + list.size() + " 筆");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	//新增病患資料
	public void saveOnePatientData(Cancer cancer) {
		String sql= "INSERT INTO CancerTable(patientId, stage, race, vitalStatus, update_date, followDays) VALUES (?,?,?,?,?,?);";
		try (Connection conn = JDBCutil.getConnection();
			PreparedStatement preparedStatement=conn.prepareStatement(sql);){
			preparedStatement.setString(1,cancer.getPatientId());
			preparedStatement.setString(2, cancer.getStage());
			preparedStatement.setString(3, cancer.getRace());
			preparedStatement.setString(4,cancer.getVitalStatus());
			LocalDate now = LocalDate.now();
			preparedStatement.setDate(5,  java.sql.Date.valueOf(now));
			preparedStatement.setInt(6, cancer.getFollowDays());
			
			
			
			preparedStatement.execute();
			System.out.println("新增資料完成");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
    //刪除天數資料
	public void  deleteByFollowDaysLessThan(int days) {
		String sql = "DELETE FROM CancerTable WHERE followDays < ?";
		try (Connection connection = JDBCutil.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql);){
			preparedStatement.setInt(1,days);
			int row = preparedStatement.executeUpdate();
			System.out.println("已刪除筆數:"+row+"筆");
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	
	 //刪除病患資料
		public Cancer  deletePatientData(Cancer cancer) {
			String sql = "DELETE FROM CancerTable WHERE patientId = ?";
			try (Connection connection = JDBCutil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);){
				preparedStatement.setString(1,cancer.getPatientId());
				int row = preparedStatement.executeUpdate();
				System.out.println("已刪除筆數:"+row+"筆");
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			return cancer;
		}
	//修改資料
	public void  updatePatientData(Cancer cancer) {
		String sql = "UPDATE CancerTable SET stage = ?, race = ?, vitalStatus = ?, update_date = ?, followDays = ? "
				+ "WHERE patientId = ?";
		try (Connection connection = JDBCutil.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql);){
			preparedStatement.setString(1, cancer.getStage());
			preparedStatement.setString(2, cancer.getRace());
			preparedStatement.setString(3, cancer.getVitalStatus());
			preparedStatement.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
			preparedStatement.setObject(5, cancer.getFollowDays(), java.sql.Types.INTEGER);
			preparedStatement.setString(6, cancer.getPatientId());  // patientId 當作查詢條件
			int rows = preparedStatement.executeUpdate();
			if (rows > 0) {
				System.out.println("修改資料完成");
			} else {
				System.out.println("找不到此病患 ID，無法修改");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	//查詢患者資料
	public Cancer findPatientId(String id) {
	    String sql = "SELECT * FROM CancerTable WHERE patientId = ?";
	    Cancer cancer = null;

	    try (Connection connection = JDBCutil.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	        
	        preparedStatement.setString(1, id);  // 設定 patientId 查詢條件
	        try (ResultSet rs = preparedStatement.executeQuery()) {
	            if (rs.next()) {
	                String patientId = rs.getString("patientId");
	                String stage = rs.getString("stage");
	                String race = rs.getString("race");
	                String vitalStatus = rs.getString("vitalStatus");
	                Date updatetime = rs.getDate("update_date");
	                int followDays = rs.getInt("followDays");

	                cancer = new Cancer(patientId, stage, followDays, race, vitalStatus, updatetime);
	                System.out.println("查到病患：" + cancer);
	            } else {
	                System.out.println("找不到病患 ID：" + id);
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return cancer;
	}
	
	
	//查詢患者資料期別
	public List<Cancer> findStage(String stageString) {
	    List<Cancer> cancerList = new ArrayList<>();
	    String sql = "SELECT * FROM CancerTable WHERE stage = ? OR stage = ? OR stage = ? OR stage = ?";
	    

	    try (Connection connection = JDBCutil.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

	        // 加上百分比符號以模擬「開頭是 stageString」
	    	preparedStatement.setString(1, stageString);
		    preparedStatement.setString(2, stageString + "A");
		    preparedStatement.setString(3, stageString + "B");
		    preparedStatement.setString(4, stageString + "C");

	        ResultSet rs = preparedStatement.executeQuery();
		    
		    	while(rs.next()) {
		    		String patientId = rs.getString("patientId");
		    	String stage = rs.getString("stage");
		    	String race = rs.getString("race");
		    	String vitalStatus = rs.getString("vitalStatus");
		    	Date updatetime = rs.getDate("update_date");
		    	int followDays = rs.getInt("followDays");
		    	Cancer cancer = new Cancer(patientId, stage, followDays, race, vitalStatus,updatetime);
		    	cancerList.add(cancer);
		    	System.out.println(cancer.toString());
		    	};		    	  	
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }

		    return cancerList;
		}
		
		
		//查詢患者資料
				public List<Cancer> findAllPatient() {
					ArrayList<Cancer> cancerList = new ArrayList<Cancer>();
				    String sql = "SELECT * FROM CancerTable";

				    try (Connection connection = JDBCutil.getConnection();
				    		PreparedStatement preparedStatement = connection.prepareStatement(sql);
				         ResultSet rs = preparedStatement.executeQuery()) {
				    	while(rs.next()) {
				    		String patientId = rs.getString("patientId");
				    	String stage = rs.getString("stage");
				    	String race = rs.getString("race");
				    	String vitalStatus = rs.getString("vitalStatus");
				    	Date updatetime = rs.getDate("update_date");
				    	int followDays = rs.getInt("followDays");
				    	Cancer cancer = new Cancer(patientId, stage, followDays, race, vitalStatus,updatetime);
				    	cancerList.add(cancer);
				    	};
				    	
				    	
				    	  	
				    } catch (SQLException e) {
				        e.printStackTrace();
				    }

				    return cancerList;
				}
			
	
	//查詢資料內所有筆數
	public int countRemainingRows() {
	    String sql = "SELECT COUNT(*) FROM CancerTable";
	    int count = 0;

	    try (Connection connection = JDBCutil.getConnection();
	    		PreparedStatement preparedStatement = connection.prepareStatement(sql);
	         ResultSet rs = preparedStatement.executeQuery()) {

	        if (rs.next()) {
	            count = rs.getInt(1);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return count;
	}
	
	public boolean existsPatientId(String patientId) {
	    String sql = "SELECT 1 FROM CancerTable WHERE patientId = ?";
	    try (Connection conn = JDBCutil.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, patientId);
	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next(); // 如果有資料代表存在
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // 查詢失敗，當成不存在
	    }
	}
	
	
	
	
	
}
