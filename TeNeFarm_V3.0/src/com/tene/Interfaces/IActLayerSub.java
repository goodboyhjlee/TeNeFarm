package com.tene.Interfaces;

import java.util.BitSet;

public interface IActLayerSub {
	public byte getId();
	
	public void setOppNormal( byte opp);
	public void setOppFit( byte orderfit);
	public byte getOpp();
	public void setAutoManual( boolean isAuto);
	public void setLimitValue(  int limit_open, int limit_close, int limit_max);
	public void setOpenRateReset(  byte rate);
	public byte getOpenRate();
	
	
	public boolean isMatchSide(byte side);
	public boolean isAutoSatisfy(byte operation);
	
	
	public boolean isFit(byte orderfit);
	
	
	public boolean isClosed();
	public boolean isOpend();
	
	
	
	public byte get595Open();
	public byte get595Close();
	
	public void setIndex(byte k, BitSet ref);
}
