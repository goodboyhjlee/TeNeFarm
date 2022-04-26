package com.tene.Interfaces;

import java.util.BitSet;
import java.util.Map;

import org.json.JSONArray;



public interface IActOnOff {
	public byte getId();
	public void setOppReset();
	public void setOpp( byte opp);
	public void setRunStd( byte run, byte std, byte it_use);
	public void setLED( int run);
	
		
	public void sendToApp(byte cmd, byte layerid, byte layersubid, byte[] data, Map<String, Object> updateMap);
	
	public void setAutoManual( boolean isAuto);
	public byte get595Run();
	
	public byte getStatus();
	
	public boolean isAutoStandby();
	public void setAutoOpp(byte opp);
	
	//public void updateConditionRule(JSONArray data);
	
	//public void setIndex(byte k, BitSet ref);
	
	
}
