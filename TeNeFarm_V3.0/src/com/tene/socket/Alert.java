package com.tene.socket;

public class Alert {
	private String  farm_cde = null;
	private byte nid = 0;
	private byte sid = 0;
	
	private byte isAlert = 0;
	private int avmin = 0;
	private int avmax = 0;
	
	public Alert() {
		
	}
	
	public Alert(String  farm_cde, byte nid, byte sid, byte isAlert, int avmin, int avmax) {
		this.farm_cde = farm_cde;
		this.nid = nid;
		this.sid = sid;
		this.isAlert = isAlert;
		this.avmin = avmin;
		this.avmax = avmax;
	}
	
	public byte getSid() {
		return sid;
	}
	public void setSid(byte sid) {
		this.sid = sid;
	}
	public byte getIsAlert() {
		return isAlert;
	}
	public void setIsAlert(byte isAlert) {
		this.isAlert = isAlert;
	}
	public int getAvmin() {
		return avmin;
	}
	public void setAvmin(int avmin) {
		this.avmin = avmin;
	}
	public int getAvmax() {
		return avmax;
	}
	public void setAvmax(int avmax) {
		this.avmax = avmax;
	}
	public byte getNid() {
		return nid;
	}
	public void setNid(byte nid) {
		this.nid = nid;
	}
	
}
