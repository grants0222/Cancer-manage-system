package lab09.model;

import javafx.scene.control.TextField;

import java.util.Optional;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


public class CancerDialo {
	public class CancerDialog {
		public static Cancer show(Cancer old) {
		    Dialog<Cancer> dialog = new Dialog<>();
		    
		    GridPane grid = new GridPane();
		    grid.setHgap(10);
		    grid.setVgap(10);
		    
		    TextField idField = new TextField();
		    TextField stageField = new TextField();
		    TextField raceField = new TextField();
		    TextField vitalField = new TextField();
		    TextField followField = new TextField();
		    
		    grid.add(new Label("病患ID:"), 0, 0);
		    grid.add(idField, 1, 0);

		    grid.add(new Label("Stage:"), 0, 1);
		    grid.add(stageField, 1, 1);

		    grid.add(new Label("Race:"), 0, 2);
		    grid.add(raceField, 1, 2);

		    grid.add(new Label("Vital Status:"), 0, 3);
		    grid.add(vitalField, 1, 3);

		    grid.add(new Label("Follow Days:"), 0, 4);
		    grid.add(followField, 1, 4);

		    if (old != null) {
		        idField.setText(old.getPatientId());
		        idField.setEditable(false);
		        stageField.setText(old.getStage());
		        raceField.setText(old.getRace());
		        vitalField.setText(old.getVitalStatus());
		        followField.setText(String.valueOf(old.getFollowDays()));
		    }

		    dialog.setTitle(old == null ? "新增病患資料" : "修改病患資料");
		    dialog.getDialogPane().setContent(grid);
		    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		    dialog.setResultConverter(btn -> {
		        if (btn == ButtonType.OK) {
		            try {
		                String pid = idField.getText().trim();
		                String stage = stageField.getText().trim();
		                String race = raceField.getText().trim();
		                String vital = vitalField.getText().trim();
		                int followDays = Integer.parseInt(followField.getText().trim());
		                return new Cancer(pid, stage, followDays, race, vital, java.sql.Date.valueOf(java.time.LocalDate.now()));
		            } catch (Exception e) {
		                return null;
		            }
		        }
		        return null;
		    });

		    Optional<Cancer> result = dialog.showAndWait();
		    return result.orElse(null);
		}
	}
}