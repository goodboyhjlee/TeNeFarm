package com.tene.controller.house.actgrp.etc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class ActGroupEtcMap {
	private Map<Byte,ActGroupEtc> mapEtc = new HashMap<Byte,ActGroupEtc>();
	private boolean isUseWindDir = false;
	private boolean isUseWindSpeed = false;
	private boolean isUseRainDrop = false;
	
	private boolean isUseIndoorTemp = false;
	
	
	private void calculate() {
		for (Entry<Byte, ActGroupEtc> entEtc : mapEtc.entrySet()) {
			ActGroupEtc etc = entEtc.getValue();
			String sk = etc.getSk();							
			if (sk.equals("07")) {
				isUseWindDir = etc.isUse();
			} else if (sk.equals("02")) {
				isUseWindSpeed = etc.isUse();
			} else if (sk.equals("01")) {
				isUseRainDrop = etc.isUse();
			} else if (sk.equals("03")) {
				isUseIndoorTemp = etc.isUse();
			}
				
		}
		
		//System.out.println("isUseIndoorTemp : " + isUseIndoorTemp);
	}
			
	
	public void put(byte etcId,byte val,  String sk, boolean isuse) {
		mapEtc.put(etcId, new ActGroupEtc(etcId,val,sk,isuse));	
		calculate();
	}
	
	public void update(byte etcId,byte val,byte isuse) {
		mapEtc.get(etcId).setVal(val);
		mapEtc.get(etcId).setUse((isuse==1));
		calculate();
	}

	public boolean isUseWindDir() {
		return isUseWindDir;
	}
	
	public boolean isUseWindSpeed() {
		return isUseWindSpeed;
	}
	
	public boolean isUseRainDrop() {
		return isUseRainDrop;
	}
	
	public boolean isUseIndoorTemp() {
		return isUseIndoorTemp;
	}

	
	public boolean isOverWindDirOutTemp(float sensorValue) {
		boolean isOverWindDir = false;
		for (Entry<Byte, ActGroupEtc> entEtc : mapEtc.entrySet()) {
			ActGroupEtc etc = entEtc.getValue();
			String sk = etc.getSk();	
			if (sk.equals("07")) {				
				if ( (etc.getVal() > sensorValue) && (etc.isUse()) ){
					isOverWindDir = true;
				}
			}
		}
		return isOverWindDir;
	}

	public boolean isOverWind(float sensorValue) {
		boolean isOverWind = false;
		for (Entry<Byte, ActGroupEtc> entEtc : mapEtc.entrySet()) {
			ActGroupEtc etc = entEtc.getValue();
			String sk = etc.getSk();	
			if (sk.equals("02")) {
				
				if ( (etc.getVal() < sensorValue) && (etc.isUse()) ){
					isOverWind = true;
				}
			}
		}
		return isOverWind;
	}


	public boolean isOverRain(float sensorValue) {
		boolean isOverRain = false;
		for (Entry<Byte, ActGroupEtc> entEtc : mapEtc.entrySet()) {
			ActGroupEtc etc = entEtc.getValue();
			String sk = etc.getSk();	
			if (sk.equals("01")) {
				//System.out.println(sensorValue + "  " + etc.isUse());
				//if ( (etc.getVal() < sensorValue) && (etc.isUse()) ) {
				if ( (0 < sensorValue) && (etc.isUse()) ) {
					isOverRain = true;
				}
			}
		}
		return isOverRain;
	}
	
	public boolean isOverIndoorTemp(float sensorValue) {
		boolean isOverIndoorTemp = false;
		for (Entry<Byte, ActGroupEtc> entEtc : mapEtc.entrySet()) {
			ActGroupEtc etc = entEtc.getValue();
			String sk = etc.getSk();	
			if (sk.equals("03")) {				
				if ( (etc.getVal() < sensorValue) && (etc.isUse()) ){
					isOverIndoorTemp = true;
				}
			}
		}
		return isOverIndoorTemp;
	}
}
