package com.tene.Interfaces;

import java.util.Map;


public interface ISensorNode {

	//public byte getHouse_cde();
	public byte getNid();
	public void setIsAlert(byte con_cde, byte house_cde,byte sid, byte isalert, int avmin, int avmax);
	//public void setAlertValue(byte con_cde, byte house_cde,byte sid, int avmin, int avmax);
	public Map<Byte, ISensor> getMapSensor();
	public boolean getIsShare();
	public String getKind();
	
	public boolean isChange();
	public void setChange(boolean change);
	//public void setSensorValue(byte[] data);
	public void setSensorValue( byte sid, byte low, byte high);
	public byte getBatteryStatus();
	
	public void incReceiveCnt();
	public int getReceiveCnt();
	
	public void reset();
}
