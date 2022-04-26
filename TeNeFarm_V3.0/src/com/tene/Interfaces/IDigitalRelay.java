package com.tene.Interfaces;


public interface IDigitalRelay {
	public void operation(String cmd);
	//public boolean isConnected();
	public void resetFromActLimit();
	
	public void connectSave();
	//public String getType();
	//public void makeStreamOut();
}
