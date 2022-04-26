package com.tene.Interfaces;


public interface IBroadCast {
	public void setTemperature(float value);
	public void setHumidity(float value);
	public void setRadiation(float value);
	public void setRainDrop(byte value); 
	public void setWindDir(int value);
	public void setWindSpeed(float value);
	
	
	public float getTemperature();
	public float getHumidity();
	public float getRadiation();
	public byte getRainDrop(); 
	public int getWindDir();
	public float getWindSpeed();
	
	//public void setSensorValue(byte[] buffer);
	public byte[] getSensorValue();
	
	public void setIsAlert(String farm_cde, byte nid,  byte  sid, byte isalert, int avmin, int avmax);
}
