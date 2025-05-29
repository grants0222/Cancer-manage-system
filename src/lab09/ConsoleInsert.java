package lab09;

import java.util.Scanner;

import lab09.dao.CancerDao;
import lab09.model.Cancer;

public class ConsoleInsert {
	public Cancer insertCancerFromConsole(Scanner scanner) {
	    Cancer cancer = new Cancer();
	    scanner.nextLine(); 

	    System.out.print("請輸入 Patient ID: ");
	    String id = scanner.nextLine().trim();
	    cancer.setPatientId(id.equalsIgnoreCase("null") || id.isEmpty() ? null : id);

	    System.out.print("請輸入 Stage: ");
	    String stage = scanner.nextLine().trim();
	    cancer.setStage(stage.equalsIgnoreCase("null") || stage.isEmpty() ? null : stage);

	    System.out.print("請輸入 Race: ");
	    String race = scanner.nextLine().trim();
	    cancer.setRace(race.equalsIgnoreCase("null") || race.isEmpty() ? null : race);

	    System.out.print("請輸入 Vital Status: ");
	    String vital = scanner.nextLine().trim();
	    cancer.setVitalStatus(vital.equalsIgnoreCase("null") || vital.isEmpty() ? null : vital);

	    System.out.print("請輸入 Follow Days（整數，可輸入 null 表示不填）: ");
	    String daysStr = scanner.nextLine().trim();
	    if (daysStr.equalsIgnoreCase("null") || daysStr.isEmpty()) {
	        cancer.setFollowDays(null);
	    } else {
	        try {
	            cancer.setFollowDays(Integer.parseInt(daysStr));
	        } catch (NumberFormatException e) {
	            System.out.println("輸入錯誤，Follow Days 將設為 null。");
	            cancer.setFollowDays(null);
	        }
	    }

	    // 設定目前系統日期為 update_date
	    cancer.setUpdatetime(new java.sql.Date(System.currentTimeMillis()));

	    return cancer;
	}

	   
	
	public Cancer updateCancerFromConsole(Scanner scanner) {
	    Cancer cancer = new Cancer();
	    scanner.nextLine(); 

	    CancerDao dao = new CancerDao();  // 在此創建

	    System.out.print("請輸入要更新的 Patient ID: ");
	    String id = scanner.nextLine().trim();

	    if (id.equalsIgnoreCase("null") || id.isEmpty()) {
	        System.out.println("Patient ID 不可為空");
	        return null;
	    }

	    if (!dao.existsPatientId(id)) {
	        System.out.println("找不到此病患 ID，無法更新。");
	        return null;
	    }

	    System.out.print("請輸入新的 Stage（可輸入 null 表示不修改）: ");
	    String stage = scanner.nextLine().trim();
	    cancer.setStage(stage.equalsIgnoreCase("null") || stage.isEmpty() ? null : stage);

	    System.out.print("請輸入新的 Race（可輸入 null 表示不修改）: ");
	    String race = scanner.nextLine().trim();
	    cancer.setRace(race.equalsIgnoreCase("null") || race.isEmpty() ? null : race);

	    System.out.print("請輸入新的 Vital Status（可輸入 null 表示不修改）: ");
	    String status = scanner.nextLine().trim();
	    cancer.setVitalStatus(status.equalsIgnoreCase("null") || status.isEmpty() ? null : status);

	    System.out.print("請輸入新的 Follow Days（整數，可輸入 null 表示不修改）: ");
	    String followDaysInput = scanner.nextLine().trim();
	    if (followDaysInput.equalsIgnoreCase("null") || followDaysInput.isEmpty()) {
	        cancer.setFollowDays(null);
	    } else {
	        try {
	            cancer.setFollowDays(Integer.parseInt(followDaysInput));
	        } catch (NumberFormatException e) {
	            System.out.println("輸入的追蹤天數不是有效整數，預設為 null");
	            cancer.setFollowDays(null);
	        }
	    }

	    // 設定系統當前時間為 update_date
	    cancer.setUpdatetime(new java.sql.Date(System.currentTimeMillis()));

	    return cancer;
	}

	   
	}
	
	
	
