package lab09;

import lab09.dao.CancerDao;
import lab09.model.Cancer;
import lab09.service.CancerService;
import util.CsvExportUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class CancerSwingApp extends JFrame {
    private CancerDao dao = new CancerDao();
    private CancerService service = new CancerService();
    private List<Cancer> currentData = new ArrayList<>();

    private JTable table;
    private DefaultTableModel tableModel;

    public CancerSwingApp() {
        setTitle("癌症病患管理系統");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 建立表格模型
        tableModel = new DefaultTableModel(new String[]{"Patient ID", "Stage", "Race", "Vital Status", "Update Date", "Follow Days"}, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 17));
        JScrollPane scrollPane = new JScrollPane(table);

        // 按鈕群組
        JPanel buttonPanel = new JPanel();
        //設定按鍵行數 兩列四行 按鍵間隔
        buttonPanel.setLayout(new GridLayout(2, 4, 5, 5));
        
        
        //創造按鍵
        JButton importBtn = new JButton("匯入 CSV");
        JButton addBtn = new JButton("新增資料");
        JButton deleteBtn = new JButton("刪除資料");
        JButton updateBtn = new JButton("修改資料");
        JButton queryBtn = new JButton("查詢資料");
        JButton exportBtn = new JButton("匯出 CSV");
        JButton countBtn = new JButton("資料筆數");
        JButton exitBtn = new JButton("離開");

        buttonPanel.add(importBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(queryBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(countBtn);
        buttonPanel.add(exitBtn);


        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);


        // 匯入 CSV
        importBtn.addActionListener(e -> {
        	//JOptionPane 顯示對話框,this意味著出現在畫面正中央
            String csvPath = JOptionPane.showInputDialog(this, "請輸入CSV路徑 (預設: src/resource/BRCA_clinical.csv)", "匯入CSV", JOptionPane.PLAIN_MESSAGE);
            if (csvPath == null) {
                return;//回到主選單
            }
            if (csvPath.trim().isEmpty()) {
                csvPath = "src/resource/BRCA_clinical.csv";
            }
            List<Cancer> list = service.readCsv(csvPath);
            dao.saveCancer(list);
            currentData = list;
            refreshTable(currentData);//將table匯入了
            JOptionPane.showMessageDialog(this, "匯入完成，共 " + list.size() + " 筆資料。");
        });

        // 新增病患
        addBtn.addActionListener(e -> {
            Cancer newCancer = showCancerInputDialog(null);
            if (newCancer != null) {
                if (dao.existsPatientId(newCancer.getPatientId())) {
                    JOptionPane.showMessageDialog(this, "病患ID已存在，新增失敗");
                    return;
                }
                dao.saveOnePatientData(newCancer);
                JOptionPane.showMessageDialog(this, "新增完成");
                currentData = dao.findAllPatient();
                refreshTable(currentData);
            }
        });

        // 刪除資料
        deleteBtn.addActionListener(e -> {
            String[] options = {"依病患ID刪除", "依追蹤天數刪除"};//設定刪除功能
            //回傳choice值JOptionPane.DEFAULT_OPTION不使用預設，已經自訂選項，null沒有自訂圖示
            int choice = JOptionPane.showOptionDialog(this, "請選擇刪除方式", "刪除資料", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (choice == 0) {
                String id = JOptionPane.showInputDialog(this, "輸入要刪除的病患ID");
                if (id != null && !id.trim().isEmpty()) {
                    Cancer c = (Cancer) dao.findPatientId(id.trim());
                    if (c != null) {
                        dao.deletePatientData(c);
                        JOptionPane.showMessageDialog(this, "刪除完成");
                        currentData = dao.findAllPatient();
                        refreshTable(currentData);
                    } else {
                        JOptionPane.showMessageDialog(this, "查無此病患ID");
                    }
                }
            } else if (choice == 1) {
                String daysStr = JOptionPane.showInputDialog(this, "輸入追蹤天數門檻值（小於此值會被刪除）");
                try {
                    int days = Integer.parseInt(daysStr);
                    dao.deleteByFollowDaysLessThan(days);
                    JOptionPane.showMessageDialog(this, "刪除完成");
                    currentData = dao.findAllPatient();
                    refreshTable(currentData);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "輸入不合法的天數");
                }
            }
        });
        

        // 修改資料
        updateBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "輸入要修改的病患ID");
            if (id != null && !id.trim().isEmpty()) {
                Cancer existing = dao.findPatientId(id.trim());
                if (existing != null) {
                    Cancer updated = showCancerInputDialog(existing);
                    if (updated != null) {
                        dao.updatePatientData(updated);
                        JOptionPane.showMessageDialog(this, "修改完成");
                        currentData = dao.findAllPatient();
                        refreshTable(currentData);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "查無此病患ID");
                }
            }
        });

        // 查詢資料
        queryBtn.addActionListener(e -> {
            String[] options = {"依病患ID查詢", "依Stage查詢", "查詢所有"};
            int choice = JOptionPane.showOptionDialog(this, "請選擇查詢方式", "查詢資料", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (choice == 0) {
                String id = JOptionPane.showInputDialog(this, "輸入病患ID");
                if (id != null && !id.trim().isEmpty()) {
                    Cancer patient = dao.findPatientId(id.trim());
                    if (patient != null) {
                        currentData = new ArrayList<>();
                        currentData.add(patient);
                        refreshTable(currentData);
                    } else {
                        JOptionPane.showMessageDialog(this, "查無此病患ID");
                    }
                }
            } else if (choice == 1) {
                String stage = JOptionPane.showInputDialog(this, "輸入Stage(期別)");
                if (stage != null) {
                    List<Cancer> list = dao.findStage(stage.trim());
                    currentData = list;
                    refreshTable(list);
                    JOptionPane.showMessageDialog(this, "查詢到 " + list.size() + " 筆資料");
                }
            } else if (choice == 2) {
                List<Cancer> all = dao.findAllPatient();
                currentData = all;
                refreshTable(all);
                JOptionPane.showMessageDialog(this, "查詢到 " + all.size() + " 筆資料");
            }
        });

        // 匯出CSV
        exportBtn.addActionListener(e -> {
            if (currentData == null || currentData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "目前無資料可匯出");
                return;
            }
            String fileName = JOptionPane.showInputDialog(this, "輸入匯出檔名(例: output.csv)");
            if (fileName != null && !fileName.trim().isEmpty()) {
                boolean success = CsvExportUtil.exportCancerData(currentData, fileName.trim());
                if (success) {
                    JOptionPane.showMessageDialog(this, "匯出成功");
                } else {
                    JOptionPane.showMessageDialog(this, "匯出失敗");
                }
            }
        });

        // 資料筆數
        countBtn.addActionListener(e -> {
            int count = dao.countRemainingRows();
            JOptionPane.showMessageDialog(this, "資料表中目前筆數：" + count);
        });

        // 離開
        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "確定要離開嗎？", "確認離開", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

    }

    // 輸入對話框: 新增或修改資料
    private Cancer showCancerInputDialog(Cancer old) {
        JTextField patientIdField = new JTextField();
        JTextField stageField = new JTextField();
        JTextField raceField = new JTextField();
        JTextField vitalStatusField = new JTextField();
        JTextField followDaysField = new JTextField();

        if (old != null) {
            patientIdField.setText(old.getPatientId());
            patientIdField.setEditable(false);  // ID 不可修改
            stageField.setText(old.getStage());
            raceField.setText(old.getRace());
            vitalStatusField.setText(old.getVitalStatus());
            followDaysField.setText(String.valueOf(old.getFollowDays()));
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("病患ID:"));
        panel.add(patientIdField);
        panel.add(new JLabel("Stage (期別):"));
        panel.add(stageField);
        panel.add(new JLabel("Race:"));
        panel.add(raceField);
        panel.add(new JLabel("Vital Status:"));
        panel.add(vitalStatusField);
        panel.add(new JLabel("Follow Days:"));
        panel.add(followDaysField);

        int result = JOptionPane.showConfirmDialog(this, panel, old == null ? "新增病患資料" : "修改病患資料",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {//當按下確定鍵
            try {
                String pid = patientIdField.getText().trim();
                String stage = stageField.getText().trim();
                String race = raceField.getText().trim();
                String vital = vitalStatusField.getText().trim();
                int followDays = Integer.parseInt(followDaysField.getText().trim());

                if (pid.isEmpty() || stage.isEmpty() || race.isEmpty() || vital.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "欄位不可空白");
                    return null;
                }

                return new Cancer(pid, stage, followDays, race, vital, java.sql.Date.valueOf(java.time.LocalDate.now()));
            } catch (NumberFormatException ex) {//如果輸入並非數字，
                JOptionPane.showMessageDialog(this, "追蹤天數請輸入數字");
                return null;
            }
        } else {
            return null;
        }
    }

    private void refreshTable(List<Cancer> list) {
        tableModel.setRowCount(0);
        for (Cancer c : list) {
            tableModel.addRow(new Object[]{
                    c.getPatientId(),
                    c.getStage(),
                    c.getRace(),
                    c.getVitalStatus(),
                    c.getUpdatetime(),
                    c.getFollowDays()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	//UIManager.put("Button.font", new Font("Microsoft JhengHei", Font.PLAIN, 18));
            new CancerSwingApp().setVisible(true);
            
        });
    }
}
