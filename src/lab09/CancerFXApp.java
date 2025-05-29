package lab09;

import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lab09.dao.CancerDao;
import lab09.model.Cancer;
import lab09.model.CancerDialo.CancerDialog;
import lab09.service.CancerService;
import util.CsvExportUtil;

import java.io.File;
import java.sql.Date;
import java.util.List;

public class CancerFXApp extends Application {

	private CancerDao dao = new CancerDao();
	private TableView<Cancer> tableView = new TableView<>();
	private ObservableList<Cancer> currentData = FXCollections.observableArrayList();

	@Override
	public void start(Stage primaryStage) {
	    primaryStage.setTitle("癌症病患管理系統 (JavaFX)");

	    // 建立欄位
	    TableColumn<Cancer, String> idCol = new TableColumn<>("Patient ID");
	    idCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));

	    TableColumn<Cancer, String> stageCol = new TableColumn<>("Stage");
	    stageCol.setCellValueFactory(new PropertyValueFactory<>("stage"));

	    TableColumn<Cancer, String> raceCol = new TableColumn<>("Race");
	    raceCol.setCellValueFactory(new PropertyValueFactory<>("race"));

	    TableColumn<Cancer, String> vitalCol = new TableColumn<>("Vital Status");
	    vitalCol.setCellValueFactory(new PropertyValueFactory<>("vitalStatus"));

	    TableColumn<Cancer, Date> updateCol = new TableColumn<>("Update Date");
	    updateCol.setCellValueFactory(new PropertyValueFactory<>("updatetime"));

	    TableColumn<Cancer, Integer> daysCol = new TableColumn<>("Follow Days");
	    daysCol.setCellValueFactory(new PropertyValueFactory<>("followDays"));

	    TableColumn<Cancer, Void> editCol = new TableColumn<>("修改");
	    editCol.setCellFactory(col -> new TableCell<>() {
	        private final Button editBtn = new Button("修改");
	        {
	            editBtn.setOnAction(e -> {
	                Cancer c = getTableView().getItems().get(getIndex());
	                Cancer updated = CancerDialog.show(c);
	                if (updated != null) {
	                    dao.updatePatientData(updated);
	                    refreshTable();
	                    showInfo("修改完成");
	                }
	            });
	        }
	        @Override
	        protected void updateItem(Void item, boolean empty) {
	            super.updateItem(item, empty);
	            setGraphic(empty ? null : editBtn);
	        }
	    });

	    TableColumn<Cancer, Void> deleteCol = new TableColumn<>("刪除");
	    deleteCol.setCellFactory(col -> new TableCell<>() {
	        private final Button deleteBtn = new Button("刪除");
	        {
	            deleteBtn.setOnAction(e -> {
	                Cancer c = getTableView().getItems().get(getIndex());
	                dao.deletePatientData(c);
	                refreshTable();
	                showInfo("刪除完成");
	            });
	        }
	        @Override
	        protected void updateItem(Void item, boolean empty) {
	            super.updateItem(item, empty);
	            setGraphic(empty ? null : deleteBtn);
	        }
	    });

	    tableView.getColumns().addAll(idCol, stageCol, raceCol, vitalCol, updateCol, daysCol, editCol, deleteCol);
	    tableView.setItems(currentData);
	    refreshTable();

	    // 查詢介面
	    ComboBox<String> queryTypeBox = new ComboBox<>();
	    queryTypeBox.getItems().addAll("依病患ID查詢", "依Stage查詢", "查詢所有");
	    queryTypeBox.setValue("依病患ID查詢");

	    TextField queryField = new TextField();
	    queryField.setPromptText("請輸入查詢內容");

	    Button doQueryBtn = new Button("查詢");
	    doQueryBtn.setOnAction(e -> {
	        String option = queryTypeBox.getValue();
	        String input = queryField.getText().trim();

	        switch (option) {
	            case "依病患ID查詢":
	                if (input.isEmpty()) {
	                    showError("請輸入病患ID");
	                    return;
	                }
	                Cancer result = dao.findPatientId(input);
	                if (result != null) {
	                    currentData.setAll(result);
	                    tableView.setItems(currentData);
	                } else {
	                    showError("查無此病患ID");
	                }
	                break;
	            case "依Stage查詢":
	                if (input.isEmpty()) {
	                    showError("請輸入Stage");
	                    return;
	                }
	                List<Cancer> resultList = dao.findStage(input);
	                currentData.setAll(resultList);
	                tableView.setItems(currentData);
	                showInfo("查詢到 " + resultList.size() + " 筆資料");
	                break;
	            case "查詢所有":
	                refreshTable();
	                showInfo("查詢到 " + currentData.size() + " 筆資料");
	                break;
	        }
	    });

	    // 建立操作按鈕列
	    Button importBtn = new Button("匯入 CSV");
	    Button addBtn = new Button("新增資料");
	    Button exportBtn = new Button("匯出 CSV");
	    Button countBtn = new Button("資料筆數");
	    Button exitBtn = new Button("離開");

	    HBox queryBox = new HBox(10, queryTypeBox, queryField, doQueryBtn);
	    queryBox.setPadding(new Insets(10));

	    HBox buttonBox = new HBox(10, importBtn, addBtn, exportBtn, countBtn, exitBtn);
	    buttonBox.setPadding(new Insets(10));

	    VBox root = new VBox(10,buttonBox, queryBox , tableView);
	    root.setPadding(new Insets(10));

	    Scene scene = new Scene(root, 1000, 600);
	    primaryStage.setScene(scene);
	    primaryStage.show();

	    // 匯入 CSV
	    importBtn.setOnAction(e -> {
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("請選擇CSV檔案");
	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
	        File file = fileChooser.showOpenDialog(primaryStage);
	        if (file != null) {
	            List<Cancer> list = service.readCsv(file.getAbsolutePath());
	            currentData.setAll(list);
	            refreshTable();
	            showInfo("匯入完成，共 " + list.size() + " 筆資料。\n請執行查詢以查看資料。");
	        }
	    });

	    // 新增資料
	    addBtn.setOnAction(e -> {
	        Cancer input = CancerDialog.show(null);
	        if (input != null) {
	            if (dao.existsPatientId(input.getPatientId())) {
	                showError("病患ID已存在，新增失敗");
	                return;
	            }
	            dao.saveOnePatientData(input);
	            refreshTable();
	            showInfo("新增完成");
	        }
	    });

	    // 匯出 CSV
	    exportBtn.setOnAction(e -> {
	        if (currentData.isEmpty()) {
	            showError("目前無資料可匯出");
	            return;
	        }
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("請選擇匯出CSV的路徑");
	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
	        File file = fileChooser.showSaveDialog(primaryStage);
	        if (file != null) {
	            boolean success = CsvExportUtil.exportCancerData(currentData, file.getAbsolutePath());
	            if (success) {
	                showInfo("匯出成功");
	            } else {
	                showError("匯出失敗");
	            }
	        }
	    });

	    // 資料筆數
	    countBtn.setOnAction(e -> {
	        int count = dao.countRemainingRows();
	        showInfo("資料表中目前筆數：" + count);
	    });

	    // 離開
	    exitBtn.setOnAction(e -> primaryStage.close());
	}

	private void refreshTable() {
	    List<Cancer> all = dao.findAllPatient();
	    currentData.setAll(all);
	    tableView.setItems(currentData);
	}

	private void showInfo(String message) {
	    Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
	    alert.showAndWait();
	}

	private void showError(String message) {
	    Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
	    alert.showAndWait();
	}

	private CancerService service = new CancerService();

	public static void main(String[] args) {
	    launch(args);
	}
}