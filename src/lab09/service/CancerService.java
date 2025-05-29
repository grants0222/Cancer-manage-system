package lab09.service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

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

    /**
     * 將 ISO 8601 格式字串轉成 java.sql.Date，只取日期部分
     */
    private static Date convertToSqlDate(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isEmpty()|| isoDateTime.equalsIgnoreCase("NA")) {
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





