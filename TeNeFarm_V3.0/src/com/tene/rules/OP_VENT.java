package com.tene.rules;


import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import com.pi4j.io.i2c.I2CDevice;
import com.tene.Interfaces.IActKnd;
import com.tene.Interfaces.IActOnOff;
import com.tene.Interfaces.IOperationPattern;
import com.tene.constant.ConstOperationKnd;
import com.tene.constant.EnumActknd;
import com.tene.controller.house.actgrp.period.ActGroupPeriodMap;


public class OP_VENT implements IOperationPattern {
	
	private float sensorValue = 0;
	//private float outTemp = 0;
	private float outHum = 0;	
	private ActGroupPeriodMap priorityMap = null;
	private Map<Byte, IActOnOff> onofflist = null;	
	private byte opp = -1;
	private byte prevOpp = -1;	
	
	private boolean isUseIndoorTemp = false;
	private boolean overIndoorTemp = false;
	
	private int gabHum = 25;
	
	@Override
	public boolean isRuning(Date now, int sunriseH, int sunriseM, int sunsetH, int sunsetM) {
		return false;		
	}
	
	private boolean oppNormal() {
		boolean ret = true;		
		int stepvalue = priorityMap.getStepValue((byte)1);
		//
		if ((this.outHum-gabHum)  <= sensorValue) {
			if (stepvalue < sensorValue) {
				this.opp = ConstOperationKnd.RUN;
			} else {
				this.opp = ConstOperationKnd.STOP;
			}
		} else {
			this.opp = ConstOperationKnd.STOP;
		}
		
		if (isUseIndoorTemp) {
			if (overIndoorTemp) {
				if (this.opp == ConstOperationKnd.STOP) {
					this.opp = ConstOperationKnd.RUN;
				}
			} else {
				
			}
		}
		
		
		if (this.prevOpp != this.opp) {
			ret = true;
			this.prevOpp = this.opp;
		} else {
			ret = false;
		}
		
		//System.out.println("VENT : " + stepvalue + "  " + sensorValue + "  " + this.outHum + "  " + this.opp);
		
		return ret;
	}
	
	
	@Override
	public boolean isRuning(float sensorValue, float outTemp, float outHum) {
		//System.out.println("VENT : " + isUseIndoorTemp + "  " + overIndoorTemp);
		this.sensorValue = sensorValue;
		//this.outTemp = outTemp;
		this.outHum = outHum;
		return oppNormal(); 
	}
	
	
	@Override
	public void run() {	
		for (Entry<Byte, IActOnOff> ent : onofflist.entrySet()) {
			IActOnOff onoff = ent.getValue();					
			onoff.setAutoOpp(this.opp);			
		}
	}
	
	@Override
	public void setActKind(Map<Byte,IActKnd> mapActKind) {
		for (Entry<Byte, IActKnd> ent : mapActKind.entrySet()) {
			IActKnd actKnd = ent.getValue();	
			if (actKnd.getKind().equals(EnumActknd.ACT_KIND.VENT_PAN)) {
				onofflist = actKnd.getOnOffInfo();
				//System.out.println(onofflist.size());
			} 				
		}
	}
	
	@Override
	public boolean getSatisfy() {
		return false;
	}
	
	@Override
	public void stop() {
		this.opp = ConstOperationKnd.STOP;
		this.prevOpp = ConstOperationKnd.STOP;
	}
	
	@Override
	public void setPriorityRangeMap(ActGroupPeriodMap priorityMap) {
		this.priorityMap = priorityMap;
	}
	
	@Override
	public void setisUseIndoorTemp(boolean isUse) {
		this.isUseIndoorTemp = isUse;
		
	}
	
	@Override
	public void setOverIndoorTemp(boolean overIndoorTemp) {
		this.overIndoorTemp = overIndoorTemp;
	}
	
	@Override
	public boolean isRuning(float sensorValue, float outHum) {		
		return false;
	}
	
	@Override
	public void seti2c(I2CDevice I2CFeather) {}
	
	@Override
	public void setisUseWindDir(boolean isUse) {}
	
	@Override
	public void setisUseWindSpeed(boolean isUse) {}
	
	@Override
	public void setisUseRainDrop(boolean isUse) {}
	
	@Override
	public void setPreStep(byte step) {}
	
	@Override
	public void setComplet(boolean complete) {}	

	@Override
	public void setHouseDir(int houseDir) {}		

	@Override
	public void setWindDir(int windDir) {}

	@Override
	public void setOverWindSpeed(boolean overWindSpeed) {}

	@Override
	public void setOverRainDrop(boolean overRainDrop) {}

	

	

	
	
	
}
