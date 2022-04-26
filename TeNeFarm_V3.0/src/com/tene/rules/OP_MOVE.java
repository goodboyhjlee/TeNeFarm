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


public class OP_MOVE implements IOperationPattern {
	
	private float sensorValue = 0;
	//private float outTemp = 0;
	//private float outHum = 0;	
	private ActGroupPeriodMap priorityMap = null;
	private Map<Byte, IActOnOff> onofflist = null;	
	private byte opp = -1;
	private byte prevOpp = -1;	
	
	@Override
	public boolean isRuning(Date now, int sunriseH, int sunriseM, int sunsetH, int sunsetM) {
		return false;		
	}
	
	private boolean oppNormal() {
		boolean ret = true;		
		int stepvalue = priorityMap.getStepValue((byte)1);
		
		//System.out.println(stepvalue + "  " + sensorValue);
		//if ((this.outTemp-10)  <= sensorValue) {
			if (stepvalue < sensorValue) {
				this.opp = ConstOperationKnd.RUN;
			} else {
				this.opp = ConstOperationKnd.STOP;
			}
		//} else {
		//	this.opp = ConstOperationKnd.STOP;
		//}
		
		if (this.prevOpp != this.opp) {
			ret = true;
			this.prevOpp = this.opp;
		} else {
			ret = false;
		}
		return ret;
	}
	
	
	@Override
	public boolean isRuning(float sensorValue, float outTemp, float outHum) {
		this.sensorValue = sensorValue;
		//this.outTemp = outTemp;
		//this.outHum = outHum;
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
			if (actKnd.getKind().equals(EnumActknd.ACT_KIND.MOVE_FAN)) {
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

	@Override
	public void setisUseIndoorTemp(boolean isUse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOverIndoorTemp(boolean overIndoorTemp) {
		// TODO Auto-generated method stub
		
	}
	
}
