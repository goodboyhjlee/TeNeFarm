package com.tene.sensor;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tene.Interfaces.ISensor;
import com.tene.Interfaces.ISensorNode;


public class SenerNode_RF implements ISensorNode {
	private byte nid = 0;
	private boolean share = false;
	private String kind = null;
	private boolean isChange = true;
	private Map<Byte, ISensor> mapSensor = new HashMap<Byte, ISensor>();
	
	private byte battery = 0;
	private int receiveCnt = 0;
	private float ntemp = 0f;
	private float nhum = 0f;
	private float hum_value = 0;
	
	
	public SenerNode_RF(JSONObject jsensornode, String owner_nam, float rad) throws Exception {
		//System.out.println(jsensornode);
		
		this.nid = (byte) jsensornode.getInt("key");
		this.share = jsensornode.getString("isshare").equals("Y") ? true : false;
		this.kind = jsensornode.getString("knd");
		this.ntemp = (float) jsensornode.getDouble("ntemp");
		this.nhum = (float) jsensornode.getDouble("nhum");
		
		
		
		if (nid != (byte)51) {
			JSONArray jSensor = jsensornode.getJSONArray("sensor");
			int sCnt = jSensor.length();
			if (sCnt > 0)
			for (int i=0; i<sCnt; i++) {
				JSONObject sensor = jSensor.getJSONObject(i);
				byte sid = (byte) sensor.getInt("key");
				mapSensor.put(sid, new Sensor_RF(sensor, owner_nam, rad));
			}
		}	
	}
	
	private static float normalizeHum(float value, float min, float max, float con) {	
		float aa = (float) ((Math.pow(value, 4)));
		float x = (3 * aa) / 40f;
		return ((x - min) / (max - min)) * con;
	}
	
	
	
	@Override
	public void setSensorValue( byte sid,  byte high, byte low) {
		receiveCnt = 0;
		byte[] arr = {0,0,high,low};	
		ByteBuffer wrapped = ByteBuffer.wrap(arr);
		int sValue = wrapped.getInt();
		float value = 0;
		
		switch (sid) {	
		case 1 : //temperature
			
			int val = (  (high << 8) | (low & 0x00FF) ) ;
			int isMinus = ((high >> 7) == 1) ? -1 : 1;
		    if (isMinus < 0) {
		    	int rev = ~val;
		      val = rev+1;
		    }
		    value = (float) ((val*isMinus) / 10.0);
			
			//value = value - normalizeHum(value,0,180000,ntemp);
			//System.out.println("ST : " + value);
			//System.out.println("nor : " + value);
			break;
		case 2 : //humidity
			value = (float) ((sValue) / 10.0);
			if (value > 99) {
				value = hum_value;
			} else {
				hum_value = value;
			}
			//System.out.println("SH : " + value);
			break;
		case 3 : //co2
			value = (float) ((sValue) / 10.0);
			//System.out.println("C : " + value);
			break;
		case 4 : //radiation
			value = (float) ((sValue) / 10.0);
			//System.out.println("R : " + value);
			break;
		case 5 : //soil temperature
			value = (float) ((sValue) / 10.0);
			break;
		case 6 : //soil moisture
			value = (float) ((sValue) / 10.0);
			break;
			/*
		case 41 : 
			value = sValue / 10;
			break;
			*/
		case 9 : //measure battery remain
			battery = (byte) sValue;
			break;
		case  0 :
			break;
		
		
		}
		
		//System.out.println(sid + " : " + value);
		
		//if (this.nid == 2) {
		//	System.out.println(sid + " : " + value);
		//}
		
		ISensor sensor = mapSensor.get(sid);
		if (sensor != null) {
			sensor.setValue(value);
		} else if (sid != 9) {
			//System.out.println("sssss");
			//for (Entry<Byte, ISensor> ent : mapSensor.entrySet()) {
			//	ent.getValue().setValue(-99);
			//}
		}
	}
	
	/*
	@Override
	public void setSensorValue( byte[] data) {
		receiveCnt = 0;
		//int sensorCnt = ((data.length-3) / 3);
		int sensorCnt = data[2];
		for (int sidx=0; sidx<sensorCnt;sidx++) {
			//byte sid = data[sidx * 3];
			byte sid = data[ (sidx * 3) + 3];
			byte[] arr = {data[(sidx * 3) + 4],data[(sidx * 3) + 5]};	
			ByteBuffer wrapped = ByteBuffer.wrap(arr);
			short sValue = wrapped.getShort();
			float value = 0;
			//System.out.println("sensorid : " + sensorCnt + "  " + nid + "  " + sid + "  " + value);
			//float nvalue = 0;
			
			switch (sid) {	
			case 1 : //temperature
				value = (float) (sValue / 10.0);
				value = value - normalizeHum(value,0,180000,ntemp);
				//System.out.println("Temp : " + value + " " + nvalue);
				break;
			case 2 : //humidity
				value = (float) ((sValue) / 10.0);
				value = value - normalizeHum(value,0,7300000,nhum);
				//System.out.println("Hum : " + value + " " + nvalue);
				break;
			case 3 : //co2
				value = sValue;
				break;
			case 4 : //radiation
				value = sValue;
				//System.out.println("sensorid : " + sensorCnt + "  " + nid + "  " + sid + "  " + value);
				//stacked radiation
				//ISensor sensorRad = mapSensor.get((byte)5);
				//sensorRad.setValue(value);
				break;
			case 6 : //soil temperature
				value = (float) ((sValue) / 10.0);;
				break;
			case 7 : //soil moisture
				value = (float) ((sValue) / 10.0);;;
				break;
				/*
			case 41 : 
				value = sValue / 10;
				break;
			
			case 9 : //measure battery remain
				battery = (byte) sValue;
				break;
			
			}
			
			
   			ISensor sensor = mapSensor.get(sid);
   			if (sensor != null)
   				sensor.setValue(value);
   			
		}
	}
	*/
	
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
