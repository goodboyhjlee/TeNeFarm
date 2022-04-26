package com.tene.controller;

import java.net.InetAddress;

public class UDPClient {
	private InetAddress address = null;
	private int port = 0;
	
	private int key = -1;
	
	public UDPClient(InetAddress address, int port, int key) {
		this.address = address;
		this.port = port;
		this.key = key;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getKey() {
		return key;
	}
}
