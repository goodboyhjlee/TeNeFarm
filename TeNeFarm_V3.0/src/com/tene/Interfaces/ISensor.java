package com.tene.Interfaces;

public interface ISensor {
	//public byte[] getReqData();
	public String getKind();
	public float getValue();
	public void setValue(float value);
	public byte getSid();
	public byte getPos();
	public String getFldNam();
	public void setIsAlert(byte con_cde, byte house_cde,byte isalert, int avmin, int avmax);
	/*
	public void setAlertValue(byte con_cde, byte house_cde,int avmin, int avmax);
	*/
	
	public boolean isChange();
	
	public byte getNid();
	
	
	
	
}
