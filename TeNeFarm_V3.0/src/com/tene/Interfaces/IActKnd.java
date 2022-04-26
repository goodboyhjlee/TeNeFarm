package com.tene.Interfaces;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import com.tene.constant.EnumActknd;
import com.tene.controller.house.actgrp.period.CurValues;


public interface IActKnd {
	public byte getId();
	public byte getDO();
	public String getName();
	public EnumActknd.ACT_KIND getKind();
	public void setOppReset();
	public void setLayerSubStop( byte layerid, byte layersubid);
	public void stopLayer();
	
	public void setOppFit(byte layerid, byte key, byte orderRate);
	public void setOnOff( byte key, byte onoff);
	public void setOnOff( byte onoff);
	public void setAutoManual( boolean isAuto);
	public void setLimitValue( byte layerid, byte layersubid, int limit_open, int limit_close, int limit_max);	
	public void setOpenRateReset( byte actlayerid, byte rate);
	public void setRunStd(byte onoffid, byte run, byte std, byte it_use);
	public void setLED(byte onoffid, int run);
	
	public void updateRoll(byte layerid, byte isuse, byte high, byte crate);
	
	public void sendToApp(byte cmd, byte layerid, byte layersubid, byte[] data, Map<String, Object> updateMap);	
	public void sendToApp(byte cmd, byte onoffid, byte[] data, Map<String, Object> updateMap);
	public void sendToApp(byte cmd, byte onoffid,  byte[] data);
	
	public void sendToArduino(byte cmd, byte layerid, byte layersubid, byte opp);
	
	public byte[] getOpenRate();
		
	
	public Map<Byte, IActLayer> getLayerInfo();
	public Map<Byte,IActOnOff> getOnOffInfo();
	
	//public void updateConditionRule(JSONArray data);
	
}
