package com.tene.sensor;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tene.Interfaces.ISensor;
import com.tene.Interfaces.ISensorNode;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;


public class SenerNode_ModBus implements ISensorNode {
	private byte nid = 0;
	private boolean share = false;
	private String kind = null;
	private boolean isChange = true;
	private Map<Byte, ISensor> mapSensor = new HashMap<Byte, ISensor>();
	
	private byte battery = 0;
	private int receiveCnt = 0;
	private ModbusClient modbusClient = new ModbusClient();
	private String ip = null;
	
	
	
	public SenerNode_ModBus(JSONObject jsensornode, String owner_nam) throws Exception {
		//System.out.println(jsensornode);
		
		this.nid = (byte) jsensornode.getInt("key");
		this.share = jsensornode.getString("isshare").equals("Y") ? true : false;
		this.kind = jsensornode.getString("knd");
		
		//µþ±â
		if (this.nid == 9) {
			ip = "30.1.6.211";
			
		} else if (this.nid == 6) { //Æ¯¿ë
			ip = "30.1.6.212";
		}		
		
		
		
		modbusClient.Available(100);
		modbusClient.Connect(ip,502);
		
		
		if (nid != (byte)51) {
			JSONArray jSensor = jsensornode.getJSONArray("sensor");
			int sCnt = jSensor.length();
			if (sCnt > 0)
			for (int i=0; i<sCnt; i++) {
				JSONObject sensor = jSensor.getJSONObject(i);
				byte sid = (byte) sensor.getInt("key");
				mapSensor.put(sid, new Sensor_ModBus(sensor, owner_nam));
			}
		}	
	}
	
	
	
	
	
	@Override
	public void setSensorValue( byte sid,  byte high, byte low) {
		try {
			ISensor sensor_ph = mapSensor.get((byte)1);
			ISensor sensor_ec = mapSensor.get((byte)2);
			ISensor sensor_ra = mapSensor.get((byte)3);
			
			
			Thread.sleep(500);			
			int[] soc_ph = modbusClient.ReadHoldingRegisters(0x40002,1);
			Thread.sleep(500);	
			int[] soc_ec = modbusClient.ReadHoldingRegisters(0x40001,1);
			Thread.sleep(500);	
			int[] soc_ra = modbusClient.ReadHoldingRegisters(0x40005,1);
			
			float value_ph = (float) (soc_ph[0] / 100.0);
			float value_ec = (float) (soc_ec[0] / 100.0);
			float value_ra = (float) (soc_ra[0]);
			
			//System.out.println(value_ph + "  " + value_ec + "  " + value_ra);
			
			sensor_ph.setValue(value_ph);
			sensor_ec.setValue(value_ec);
			sensor_ra.setValue(value_ra);
			
		} catch (Exception e) {
			try {
				modbusClient.Disconnect();
				
				modbusClient = null;
				
				modbusClient = new ModbusClient();
				
			
				modbusClient.Available(100);
				modbusClient.Connect(ip,502);
			} catch (Exception em) {
				em.printStackTrace();
			}
			
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 
		
		receiveCnt = 0;
		
		
	}
	
	
	
	@Override
	public void incReceiveCnt() {
		receiveCnt++;
	}
	
	@Override
	public int getReceiveCnt() {
		return this.receiveCnt;
	}
	
	@Override
	public void reset() {
		if (this.receiveCnt > 2)
		for (Entry<Byte, ISensor> ent : mapSensor.entrySet()) {
			ent.getValue().setValue(-99);
		}
	}
	
	@Override
	public byte getBatteryStatus() {
		return this.battery;
	}
	
	@Override
	public boolean getIsShare() {
		return share;
	}
	
	@Override
	public String getKind() {
		return this.kind;
	}

	public Map<Byte, ISensor> getMapSensor() {
		return mapSensor;
	}	
	
	@Override
	public byte getNid() {
		return this.nid;
	}

	@Override
	public void setIsAlert(byte con_cde, byte house_cde, byte sid, byte isalert,int avmin, int avmax) {
		mapSensor.get(sid).setIsAlert(con_cde, house_cde, isalert,avmin,avmax);
	}
	
	/*
	@Override
	public void setAlertValue(byte con_cde, byte house_cde, byte sid, int avmin, int avmax) {
		mapSensor.get(sid).setAlertValue(con_cde, house_cde, avmin, avmax);
	}
	*/

	@Override
	public boolean isChange() {
		// TODO Auto-generated method stub
		return this.isChange;
	}

	

	@Override
	public void setChange(boolean change) {
		this.isChange = change;
		
	}

	
}
