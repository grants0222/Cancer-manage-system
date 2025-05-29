package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



public class GetDataUtil {
	public static List<String> getDate(String dataPath) {
		ArrayList<String> dataList = new ArrayList<String>();
		try (FileInputStream fileInputStream = new FileInputStream(new File(dataPath));
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			String content="";
			while (bufferedReader.ready()) {
			 	content= bufferedReader.readLine();
//			 	System.out.println(content);
			 	dataList.add(content);
			}
//			刪除標頭
			dataList.remove(0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}
}
