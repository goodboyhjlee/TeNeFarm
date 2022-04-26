package com.tene.Interfaces;

import java.util.Map;

import com.tene.controller.house.actgrp.period.CurValues;


public interface IActLayer {
	public byte getId();
	public byte getLayer();
	public float getCorValue();
	public void setOppReset();
	public void setOppNormal( byte layersubid, byte opp);
	public void setOppFit(byte layersubid,  byte orderfit);
	public void stopLayer();
	public void setAutoManual( boolean isAuto);
	public void setLimitValue( byte layersubid,  int limit_open, int limit_close, int limit_max);	
	public void setOpenRateReset(  byte rate);
	public void updateRoll(byte isuse, byte high, byte crate); 
	public void sendToApp(byte cmd, byte layersubid,  byte[] data, Map<String, Object> updateMap);
	public void sendToArduino(byte cmd, byte layersubid, byte opp);
	public byte[] getOpenRate();
	
	
	
	public boolean isAllClosed();
	public void allClose();
	public boolean isAllOpend();
	public void allOpen();
	
	public byte getOpenState();
	
	
	public Map<Byte, IActLayerSub> getSubLayerInfo();
}
