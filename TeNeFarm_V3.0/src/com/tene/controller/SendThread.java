package com.tene.controller;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

public class SendThread extends Thread {
	private DatagramSocket dsoc = null;
	private Queue<byte[]> stackdata = new LinkedList<byte[]>();
	private Map<String,UDPClient> clientList = new HashMap<String,UDPClient>();
	
	private DatagramPacket dpsend = null;
	
	public SendThread() {
		dpsend = new DatagramPacket(new byte[1],1);
	}
	
	public boolean getKey(DatagramPacket dp) {
		
		return (clientList.get(dp.getAddress().getHostAddress().toString() + dp.getPort()) != null);
		
		/*
		
		if (clientList.get(dp.getAddress().getHostAddress().toString() + dp.getPort()) != null) {
			
		}
		
		UDPClient client = clientList.get(dp.getAddress().getHostAddress().toString() + dp.getPort());
		
		client.getKey();
		
		return 1;
		*/
	}
	
	public int getClientCnt() {
		return clientList.size();
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(10);
				byte[] sData = stackdata.poll();
				//System.out.println(sData);
				//
				if (sData != null) {
					//System.out.println("getClientCnt() : " + getClientCnt());
					for (Entry<String, UDPClient> ent : clientList.entrySet()) {
						//System.out.println("ent : " + ent);
						if (ent != null) {	
							dpsend.setData(sData);
							dpsend.setLength(sData.length);
							dpsend.setAddress(ent.getValue().getAddress());
							//dpsend.setPort(ent.getValue().getPort());
							//dpsend.setPort(9998);							
							//dsoc.send(dpsend);
							
							
							dpsend.setPort(ent.getValue().getPort());
							dsoc.send(dpsend);
						}
					}
					//Thread.sleep(20);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addAddress(InetAddress address, int dynPort, int key) {
		try {
			clientList.put(address.getHostAddress().toString() + dynPort, new UDPClient(address, dynPort, key));
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	
	public void addAddress(InetAddress address, int dynPort) {
		try {
			clientList.put(address.getHostAddress().toString() + dynPort, new UDPClient(address, dynPort, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	
	public void addData(byte[] sendData) {
		try {
			//System.out.println("addData : " + sendData[0]);
		//System.out.println("getClientCnt() : " + getClientCnt());
			stackdata.add(sendData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	public void setStreamOut(DatagramSocket dsoc) {
		//System.out.println("dsoc : " + dsoc);
		this.dsoc = dsoc;
	}
	
	public void remotePeer(InetAddress address, int dynPort) {
		clientList.remove(address.getHostAddress()+ dynPort);
		System.out.println("Cur Peer : " + clientList.size());
	}
}
