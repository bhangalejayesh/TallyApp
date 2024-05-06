package com.example.RestApi.entity;

public class CLParameters {
	private String company;
	private String fromDate;
	private String toDate;
	boolean updateMasters;
	private String[] dataTypes;
	public String getCompany() {
		return company;
	}
	
	
	public void setCompany(String company) {
		this.company = company;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public boolean isUpdateMasters() {
		return updateMasters;
	}
	public void setUpdateMasters(boolean updateMasters) {
		this.updateMasters = updateMasters;
	}
	public String[] getDataTypes() {
		return dataTypes;
	}
	public void setDataTypes(String[] dataTypes) {
		this.dataTypes = dataTypes;
	}


	public boolean isDummyMode() {
		// TODO Auto-generated method stub
		return true;
	}

}
