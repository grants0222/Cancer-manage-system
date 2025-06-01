package lab09.service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import lab09.model.Cancer;

public class CancerService{

	public static List<Cancer> readCsv(String filePath) {
        List<Cancer> cancerList = new ArrayList<>();
        try (Reader reader = new FileReader(filePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            for (CSVRecord record : records) {
                Cancer cancer = new Cancer();
                cancer.setPatientId(record.get("submitter_id"));
                cancer.setStage(record.get("ajcc_pathologic_stage"));
                cancer.setRace(record.get("race"));
                cancer.setVitalStatus(record.get("vital_status"));

                // 解析日期時間欄位（例：diagnosis_date）
                String diagnosisDateTimeStr = record.get("updated_datetime");
                cancer.setUpdatetime(convertToSqlDate(diagnosisDateTimeStr));


                // days_to_last_follow_up 例外處理
                String followDaysStr = record.get("days_to_last_follow_up");
                if (followDaysStr != null && !followDaysStr.isEmpty() && !followDaysStr.equalsIgnoreCase("NA")) {
                    cancer.setFollowDays(Integer.valueOf(followDaysStr));
                } else {
                    cancer.setFollowDays(null);
                }

                cancerList.add(cancer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cancerList;
    }

	 
	 public static List<Cancer> readJson(String filePath) {
	        List<Cancer> cancerList = new ArrayList<>();
	        try (FileReader reader = new FileReader(filePath)) {
	            Gson gson = new Gson();
	            Type listType = new TypeToken<List<JsonObject>>() {}.getType();
	            List<JsonObject> jsonList = gson.fromJson(reader, listType);

	            for (JsonObject obj : jsonList) {
	                Cancer cancer = new Cancer();

	                cancer.setPatientId(getAsString(obj, "submitter_id"));
	                cancer.setStage(getAsString(obj, "ajcc_pathologic_stage"));
	                cancer.setRace(getAsString(obj, "race"));
	                cancer.setVitalStatus(getAsString(obj, "vital_status"));

	                String dateStr = getAsString(obj, "updated_datetime");
	                cancer.setUpdatetime(convertToSqlDate(dateStr));

	                String followStr = getAsString(obj, "days_to_last_follow_up");
	                if (followStr != null && !followStr.equalsIgnoreCase("NA")) {
	                    try {
	                        cancer.setFollowDays(Integer.valueOf(followStr));
	                    } catch (NumberFormatException e) {
	                        cancer.setFollowDays(null);
	                    }
	                } else {
	                    cancer.setFollowDays(null);
	                }

	                cancerList.add(cancer);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return cancerList;
	    }

	    private static String getAsString(JsonObject obj, String key) {
	        JsonElement elem = obj.get(key);
	        return (elem != null && !elem.isJsonNull()) ? elem.getAsString() : null;
	    }

	    private static Date convertToSqlDate(String isoDateTime) {
	        if (isoDateTime == null || isoDateTime.isEmpty() || isoDateTime.equalsIgnoreCase("NA")) {
	            return null;
	        }
	        try {
	            OffsetDateTime odt = OffsetDateTime.parse(isoDateTime);
	            LocalDate localDate = odt.toLocalDate();
	            return Date.valueOf(localDate);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	}
