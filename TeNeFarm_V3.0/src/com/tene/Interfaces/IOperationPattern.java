package com.tene.Interfaces;

import java.util.Date;
import java.util.Map;

import com.pi4j.io.i2c.I2CDevice;
import com.tene.controller.house.actgrp.period.ActGroupPeriodMap;

public interface IOperationPattern {
	public void setPriorityRangeMap(ActGroupPeriodMap priorityMap);
	
	public void setActKind(Map<Byte,IActKnd> mapActKind);	
	public void setComplet(boolean complete);	
	public void stop();
	
	
	public void run();
	
	public boolean getSatisfy();
	
	public void setHouseDir(int houseDir);
	
		
	public void setPreStep(byte step);
	
	public void setisUseWindDir(boolean isUse);
	public void setisUseWindSpeed(boolean isUse);
	public void setisUseRainDrop(boolean isUse);
	public void setisUseIndoorTemp(boolean isUse);
	
	
	
	public void setWindDir(int windDir);
	public void setOverWindSpeed(boolean overWindSpeed);
	public void setOverRainDrop(boolean overRainDrop);
	public void setOverIndoorTemp(boolean overIndoorTemp);
	
	public boolean isRuning(float sensorValue, float outTemp);
	public boolean isRuning(float sensorValue, float outTemp, float outHum);
	public boolean isRuning(Date now, int sunriseH, int sunriseM, int sunsetH, int sunsetM);
	
	
	
	public void seti2c(I2CDevice I2CFeather);
	
}
