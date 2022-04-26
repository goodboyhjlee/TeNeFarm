package com.tene.Interfaces;

import java.util.Map;

import org.json.JSONArray;



public interface IActGroup {
	public byte getId();
	//public float getConValue();
	public float getCorValue();
	public void setOppReset();
	public void setLayerSubStop( byte aid, byte subid, byte opp);
	public void setOppFit(byte akid, byte layer, byte key, byte orderRate);
	public void setOnOff( byte akid, byte key, byte onoff) ;
	public void setAutoManual( byte isAuto);
	public void updateConditionRule(JSONArray data);
	public void insertConditionRule(
			byte priority_ord,byte priority_sub_ord);
	public void deleteConditionRule(
			 byte priority_ord,byte priority_sub_ord,
			byte next_sub_ord, int next_val_min);
	public void updateCorection(byte corId,byte corVal, int corMin, int corMax, byte isuse);
	public void updateEtc(byte etcId,byte val,byte isuse);
	public void updateRoll(byte akid,byte layerid, byte isuse, byte high, byte crate);
	public void setLimitValue( byte actkndid, byte actlayerid, byte actlayersubid, int limit_open, int limit_close, int limit_max);
	public void setOpenRateReset(byte actkndid, byte actlayerid, byte rate);
	public void setRunStd(byte actkndid, byte onoffid, byte run, byte std, byte it_use) ;
	public void setLED(byte actkndid, byte onoffid, int run) ;
	
	public void sendToApp(byte cmd, byte akid, byte layerid, byte layersubid,  byte[] data, Map<String, Object> updateMap);
	public void sendToApp(byte cmd, byte akid, byte onoffid, byte[] data, Map<String, Object> updateMap);
	public void sendToApp(byte cmd, byte akid, byte onoffid, byte[] data);
	
	public void sendToArduino(byte cmd, byte akid, byte layerid, byte layersubid, byte opp);
	
	public byte[] getOpenRate();
	
	//public void setCurrentPeriod();
	
	public boolean isAuto();
	
	
	public Map<Byte,IActKnd> getKindInfo();
}
