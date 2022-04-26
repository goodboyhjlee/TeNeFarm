package com.tene.controller;

public class SensorSendData {
	//private byte dataSize = 0;
	private byte[] data = null;
	
	public SensorSendData() {
		
	}
	
	/*
	public SensorSendData(byte dataSize,byte[] data ) {
		//this.dataSize = dataSize;
		this.data = data;
		
	}
	*/
	
	

	

	public byte[] getData() {
		return data;
	}	

	public void setData(byte[] data) {
		this.data = data;
	}
	
	
	public byte getDataSize() {
		byte ret = 0;
		if (this.data != null) {
			ret = (byte) this.data.length;
		} 
		return ret;
	}
	/*
	public void setDataSize(byte dataSize) {
		this.dataSize = dataSize;
	}
	*/
}
