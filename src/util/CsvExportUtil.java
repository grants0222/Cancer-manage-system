package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lab09.model.Cancer;

import java.text.SimpleDateFormat;

public class CsvExportUtil {

    // 將資料匯出成 CSV
	public static boolean exportCancerData(List<Cancer> list, String fileName) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
	        pw.println("patientId,stage,race,vitalStatus,update_date,followDays");
	        for (Cancer c : list) {
	            StringBuilder sb = new StringBuilder();
	            sb.append(escapeCsv(c.getPatientId())).append(",");
	            sb.append(escapeCsv(c.getStage())).append(",");
	            sb.append(escapeCsv(c.getRace())).append(",");
	            sb.append(escapeCsv(c.getVitalStatus())).append(",");
	            sb.append(c.getUpdatetime() == null ? "" : sdf.format(c.getUpdatetime())).append(",");
	            sb.append(c.getFollowDays());
	            pw.println(sb.toString());
	        }
	        return true;  // 成功
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;  // 失敗
	    }
	}
	
	public static boolean exportCancerDataToJson(List<Cancer> list, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()  // 美化格式，方便閱讀
                    .setDateFormat("yyyy-MM-dd")  // 日期格式
                    .create();

            gson.toJson(list, writer);  // 將 list 寫入 JSON
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    // 簡單處理 CSV 字串：如果包含逗號或雙引號，就用雙引號包起來，且內部雙引號用兩個雙引號取代
    private static String escapeCsv(String input) {
        if (input == null) {
            return "";
        }
        boolean hasSpecial = input.contains(",") || input.contains("\"") || input.contains("\n") || input.contains("\r");
        if (hasSpecial) {
            input = input.replace("\"", "\"\"");
            return "\"" + input + "\"";
        } else {
            return input;
        }
    }
}



