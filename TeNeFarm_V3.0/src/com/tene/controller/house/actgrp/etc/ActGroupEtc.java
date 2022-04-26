package com.tene.controller.house.actgrp.etc;

import com.tene.Interfaces.ISensor;

public class ActGroupEtc {
	private byte etcId = 0;
	private byte val = 0;
	private String sk = null;
	private boolean use = false;
	
	//private ISensor sensor = null;
	
	public ActGroupEtc(byte etcId,byte val,  String sk, boolean use) {
		this.etcId = etcId;
		this.val = val;		
		this.use = use;
		this.sk = sk;		
		//this.sensor = sensor;
	}
	
	//public float getSensorValue() {
	//	return sensor.getValue();
	//}

	public byte getEtcId() {
		return etcId;
	}

	public void setEtcId(byte etcId) {
		this.etcId = etcId;
	}

	public byte getVal() {
		return val;
	}

	public void setVal(byte val) {
		this.val = val;
	}

	public String getSk() {
		return sk;
	}

	public void setSk(String sk) {
		this.sk = sk;
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	//public ISensor getSensor() {
	//	return sensor;
	//}

	//public void setSensor(ISensor sensor) {
	//	this.sensor = sensor;
	//}
}
