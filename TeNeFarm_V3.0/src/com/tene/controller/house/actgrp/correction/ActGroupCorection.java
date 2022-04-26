package com.tene.controller.house.actgrp.correction;

import com.tene.Interfaces.ISensor;

public class ActGroupCorection {
	private byte corId = 0;
	private byte corVal = 0;
	private int corMin = 0;
	private int corMax = 0;
	
	
	
	private String sk = null;
	private boolean use = false;
	
	private ISensor sensor = null;
	
	public ActGroupCorection(byte corId,byte corVal, int corMin, int corMax, String sk, boolean use, ISensor sensor) {
		this.corId = corId;
		this.corVal = corVal;
		this.corMin = corMin;
		this.corMax = corMax;
		this.use = use;
		this.sk = sk;		
		this.sensor = sensor;
	}
	
	public float getSensorValue() {
		return sensor.getValue();
	}
	
	public byte getCorId() {
		return corId;
	}
	public void setCorId(byte corId) {
		this.corId = corId;
	}
	public byte getCorVal() {
		return corVal;
	}
	public void setCorVal(byte corVal) {
		this.corVal = corVal;
	}
	public int getCorMin() {
		return corMin;
	}
	public void setCorMin(int corMin) {
		this.corMin = corMin;
	}
	public int getCorMax() {
		return corMax;
	}
	public void setCorMax(int corMax) {
		this.corMax = corMax;
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
	

}
