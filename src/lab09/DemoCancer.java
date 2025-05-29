package lab09;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import lab09.dao.CancerDao;
import lab09.model.Cancer;
import lab09.service.CancerService;
import util.CsvExportUtil;

public class DemoCancer {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		CancerDao dao = new CancerDao();
		CancerService service = new CancerService();
		ConsoleInsert consoleInsert = new ConsoleInsert();

		List<Cancer> lastOperationResult = new ArrayList<>();

		while(true) {
		    System.out.println("\n===功能選單===");
		    System.out.println("1. 匯入 CSV 資料");
		    System.out.println("2. 刪除資料");
		    System.out.println("3. 新增資料");
		    System.out.println("4. 修改資料");
		    System.out.println("5. 查詢資料");
		    System.out.println("6. 匯出CSV資料");
		    System.out.println("0. 離開");
		    System.out.print("請輸入選項(EX:1,2,3,0)：");
		    int choice = scanner.nextInt();
		    scanner.nextLine(); // 吃掉換行
		    int queryType;

		    switch(choice) {
		        case 1:
		            String csvPath = "src/resource/BRCA_clinical.csv";
		            List<Cancer> cancers = service.readCsv(csvPath);
		            System.out.println("CSV 讀取筆數：" + cancers.size());
		            dao.saveCancer(cancers);
		            System.out.println("目前資料表中筆數：" + dao.countRemainingRows());
		            lastOperationResult = cancers;
		            break;

		        case 2:
		            System.out.println("請選擇刪除方式：");
		            System.out.println("1. 依病患ID刪除");
		            System.out.println("2. 依 天數 刪除");
		            queryType = scanner.nextInt();
		            scanner.nextLine(); // 吃掉換行

		            if (queryType == 1) {
		                System.out.print("請輸入要刪除的病患 ID：");
		                String inputId = scanner.nextLine().trim();

		                Cancer found = dao.findPatientId(inputId);
		                if (found == null) {
		                    System.out.println("查無此病患 ID，無法刪除。");
		                    lastOperationResult.clear();
		                } else {
		                    dao.deletePatientData(found);
		                    System.out.println("已刪除病患 ID：" + found.getPatientId());
		                    // 刪除後資料已不存在，建議清空匯出結果或更新成剩餘資料
		                    lastOperationResult = dao.findAllPatient();
		                }
		            } else if (queryType == 2) {
		                System.out.print("請輸入欲刪除的追蹤天數門檻值(小於此值會被刪除): ");
		                int days = scanner.nextInt();
		                scanner.nextLine(); // 吃掉換行

		                dao.deleteByFollowDaysLessThan(days);
		                System.out.println("刪除完成。");
		                System.out.println("目前資料表中剩餘筆數：" + dao.countRemainingRows());
		                lastOperationResult = dao.findAllPatient();
		            } else {
		                System.out.println("無效選項！");
		                lastOperationResult.clear();
		            }
		            break;

		        case 3:
		            System.out.println("新增病患資料:");
		            Cancer newCancer = consoleInsert.insertCancerFromConsole(scanner);
		            dao.saveOnePatientData(newCancer);
		            System.out.println("新增完成");
		            System.out.println("目前資料表中筆數：" + dao.countRemainingRows());
		            lastOperationResult = new ArrayList<>();
		            lastOperationResult.add(newCancer);
		            break;

		        case 4:
		            System.out.println("修改病患資料 ");
		            Cancer updatedCancer = consoleInsert.updateCancerFromConsole(scanner);
		            if (updatedCancer != null) {
		                dao.updatePatientData(updatedCancer);
		                System.out.println("修改完成");
		                lastOperationResult = new ArrayList<>();
		                lastOperationResult.add(updatedCancer);
		            } else {
		                System.out.println("更新取消或查無該病患ID");
		                lastOperationResult.clear();
		            }
		            break;

		        case 5:
		            System.out.println("請選擇查詢方式：");
		            System.out.println("1. 依病患ID查詢");
		            System.out.println("2. 依 Stage 查詢");
		            System.out.println("3. 查詢所有病患");

		            queryType = scanner.nextInt();
		            scanner.nextLine(); // 吃掉換行

		            if (queryType == 1) {
		                System.out.print("請輸入病患 ID：");
		                String id = scanner.nextLine();
		                Cancer patient = dao.findPatientId(id);
		                if (patient != null) {
		                    System.out.println(patient);
		                    lastOperationResult = new ArrayList<>();
		                    lastOperationResult.add(patient);
		                } else {
		                    System.out.println("查無此病患 ID");
		                    lastOperationResult.clear();
		                }
		            } else if (queryType == 2) {
		                System.out.print("請輸入 Stage（期別）：");
		                String stage = scanner.nextLine();
		                List<Cancer> list = dao.findStage(stage);
		                list.forEach(System.out::println);
		                System.out.println("查詢到 " + list.size() + " 筆資料。");
		                lastOperationResult = list;
		            } else if (queryType == 3) {
		                List<Cancer> all = dao.findAllPatient();
		                all.forEach(System.out::println);
		                System.out.println("查詢到 " + all.size() + " 筆資料。");
		                lastOperationResult = all;
		            } else {
		                System.out.println("無效選項！");
		                lastOperationResult.clear();
		            }
		            break;

		        case 6:
		            if (lastOperationResult == null || lastOperationResult.isEmpty()) {
		                System.out.println("目前沒有資料可匯出");
		            } else {
		                System.out.print("請輸入匯出檔名(例如: output.csv)：");
		                String fileName = scanner.nextLine().trim();
		                boolean success = CsvExportUtil.exportCancerData(lastOperationResult, fileName);
		                if (success) {
		                    System.out.println("匯出成功！");
		                } else {
		                    System.out.println("匯出失敗！");
		                }
		            }
		            break;

		        case 0:
		            System.out.println("程式結束。");
		            scanner.close();
		            return;

		        default:
		            System.out.println("請輸入有效的選項。");
		            // 不一定要清空 lastOperationResult
		            break;
		    }
		}
            
		}
	}
	
