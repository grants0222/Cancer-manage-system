package lab09.model;

import java.sql.Date;

public class Cancer {
	private String patientId;
	private String stage;
	private Integer followDays;
	private String race;
	private String vitalStatus;
	private Date updatetime;
	public Cancer() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Cancer(String patientId, String stage, Integer followDays, String race, String vitalStatus,
			Date updatetime) {
		super();
		this.patientId = patientId;
		this.stage = stage;
		this.followDays = followDays;
		this.race = race;
		this.vitalStatus = vitalStatus;
		this.updatetime = updatetime;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public Integer getFollowDays() {
		return followDays;
	}
	public void setFollowDays(Integer followDays) {
		this.followDays = followDays;
	}
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public String getVitalStatus() {
		return vitalStatus;
	}
	public void setVitalStatus(String vitalStatus) {
		this.vitalStatus = vitalStatus;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	@Override
	public String toString() {
		return "Cancer [patientId=" + patientId + ", stage=" + stage + ", followDays=" + followDays + ", race=" + race
				+ ", vitalStatus=" + vitalStatus + ", updatetime=" + updatetime + "]";
	}
	
	
	
	
}


