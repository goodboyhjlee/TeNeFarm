package com.tene.Interfaces;

import java.util.BitSet;
import java.util.Map;

public interface IAct {
	public void setOpp(byte isOn);
	public void setOpenClose(  byte subid, byte opp);
	public void setOppReset();
	public void setOppFit(byte orderRate);
	public void setOppFit(byte subid, byte orderRate);
	public void setAutoManual( boolean isAuto);
	public void setLimitValue( byte asid, int limit_open, int limit_close, int limit_max);
	public boolean isRCH(byte subid);
	//public boolean isRCH(byte subid, byte openClose);
	public void setOppAutoStop();
	public boolean isAllRCH();
	
	public void sendToApp(byte cmd, byte subid, byte[] data, Map<String, Object> updateMap);
	
	public byte[] getOpenRate();
	
	
	
	
}
