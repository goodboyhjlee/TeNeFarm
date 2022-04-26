package com.tene.controller.house.actgrp.period;

public class CurValues {
	private float outTemp = 0;
	private float sensorValue = 0;
	private float value = 0;
	private float overrate = 0;
	private byte opp = 0;
	
	private boolean isOverWind = false;
	private boolean isOverRain = false;
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public float getOverrate() {
		return overrate;
	}
	public void setOverrate(float overrate) {
		this.overrate = overrate;
	}
	public byte getOpp() {
		return opp;
	}
	public void setOpp(byte opp) {
		this.opp = opp;
	}
	public float getSensorValue() {
		return sensorValue;
	}
	public void setSensorValue(float sensorValue) {
		this.sensorValue = sensorValue;
	}
	public boolean isOverWind() {
		return isOverWind;
	}
	public void setOverWind(boolean isOverWind) {
		this.isOverWind = isOverWind;
	}
	public boolean isOverRain() {
		return isOverRain;
	}
	public void setOverRain(boolean isOverRain) {
		this.isOverRain = isOverRain;
	}

	public float getOutTemp() {
		return outTemp;
	}

	public void setOutTemp(float outTemp) {
		this.outTemp = outTemp;
	}
}
