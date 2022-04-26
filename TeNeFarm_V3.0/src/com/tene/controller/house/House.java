package com.tene.controller.house;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;

import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.spi.SpiDevice;
import com.tene.Interfaces.IActGroup;
import com.tene.Interfaces.IDigitalRelay;
import com.tene.Interfaces.ISensor;
import com.tene.Interfaces.ISensorNode;
import com.tene.controller.Control;
import com.tene.controller.SensorSendData;
import com.tene.controller.house.actgrp.ActGroup;
import com.tene.controller.relay.HC595;
import com.tene.sensor.SenerNode_ModBus;
import com.tene.sensor.SenerNode_RF;
import com.tene.utils.ByteUtils;
import com.tene.utils.GetBit;

public class House {
	private Control control = null;
	
	private byte isBatch = 1;
	private long batchValue = 0;
	
	byte ledNo = 0;
	byte apin_l = 0;
	byte apin_r = 0;
	private String house_nam = null;
	private byte house_cde = 0;
	private int house_direction = 90;
	private boolean isuse = false;
	private Map<Byte,ISensorNode> mapRFSensorNode = new HashMap<Byte,ISensorNode>();
	private Map<Byte,IActGroup> mapActGroup = new HashMap<Byte,IActGroup>();
	
	//private boolean isStart = true;
	
	public int getSunRiseH() {
		return control.getSunRiseH() ;
	}
	
	public int getSunRiseM() {
		return control.getSunRiseM() ;
	}
	
	public int getSunSetH() {
		return control.getSunSetH() ;
	}
	
	public int getSunSetM() {
		return control.getSunSetM() ;
	}
	
	
	public House(Control control, JSONObject jHouse, HC595 hc595, I2CDevice I2CFeather) throws Exception {		
		this.isuse = jHouse.getString("iu").equals("Y") ? true : false;
		
		
		if (this.isuse) {
			this.control = control;
			this.house_cde = (byte) jHouse.getInt("key");
			this.house_nam =  jHouse.getString("nam");			
			this.house_direction = jHouse.getInt("hd");					
			
			this.ledNo = (byte) jHouse.getInt("npin");
			this.apin_l = (byte) jHouse.getInt("apin_l");
			this.apin_r = (byte) jHouse.getInt("apin_r");
			
			
			
			float rad = 0;
			if (jHouse.has("radstack")) {
				if (!jHouse.isNull("radstack")) {
					JSONObject jSensorNode = jHouse.getJSONObject("radstack");
					//System.out.println(jSensorNode);
					if (!jSensorNode.isNull("rad")) {
						rad = (float) jSensorNode.getDouble("rad");
						//System.out.println(rad);
					}
				}
			}
			
			
			
			if (jHouse.has("sensornode")) {
				JSONArray jSensorNode = jHouse.getJSONArray("sensornode");
				int snCnt = jSensorNode.length();
				if (snCnt > 0)
				for (int i=0; i<snCnt; i++) {
					JSONObject sensornode = jSensorNode.getJSONObject(i);
					String snKnd = sensornode.getString("knd");
					byte nid = (byte) sensornode.getInt("key");
					boolean snIsUse = sensornode.getString("iu").equals("Y") ? true : false;
					if (snIsUse) {
						if (snKnd.equals("01")) {
							
						} else if (snKnd.equals("02")) {
							mapRFSensorNode.put(nid, new SenerNode_RF(sensornode,  this.house_nam, rad));							
						} else if (snKnd.equals("03")) {
							mapRFSensorNode.put(nid, new SenerNode_ModBus(sensornode,  this.house_nam));	
						} else if (snKnd.equals("04")) {
							//mapSensorNode.put(nid, new SenerNode_GPIO(sensornode,  this.house_nam));	
						} else if (snKnd.equals("05")) {
							//mapSensorNode.put(nid, new SenerNode_Forecast(sensornode,  this.house_nam));	
						} else if (snKnd.equals("99")) {
							//WSSensorNode = new SenerNode_TCP(sensornode, this.house_nam);
							//mapSensorNode.put(nid, new SenerNode_TCP(sensornode, this.house_nam));
						}						
					}
				}	
			}
			
			
			if (jHouse.has("actgroup")) {				
				JSONArray jActGroup = jHouse.getJSONArray("actgroup");
				int agCnt = jActGroup.length();
				if (agCnt > 0)
				for (int i=0; i<agCnt; i++) {
					JSONObject actGroup = jActGroup.getJSONObject(i);
					byte agid = (byte) actGroup.getInt("key");
					
					ActGroup actGrp = new ActGroup(this, actGroup, mapRFSensorNode, hc595, I2CFeather);					
					mapActGroup.put(agid, actGrp);
				}					
			}	
			
			
		}
	}
	
	
	
	
	public void incReceiveCnt() {
		for (Entry<Byte, ISensorNode> ent_sensornode : mapRFSensorNode.entrySet()) {
			ISensorNode sensornode = ent_sensornode.getValue();
			sensornode.incReceiveCnt();
		}
	}
	
	public int getReceiveCnts(byte nid) {
		return mapRFSensorNode.get(nid).getReceiveCnt();
	}
	
	public void getCurrentValuesAppFirstConnected(SensorSendData sensorSendData) {
		try {
			byte nodeCnt = (byte) mapRFSensorNode.size();
			//System.out.println("nodeCnt : " + nodeCnt);
			if (nodeCnt > 0) {
				//ByteBuffer buf = ByteBuffer.allocate( (nodeCnt*2) + (nodeCnt*3*3*3));
				ByteBuffer buf = ByteBuffer.allocate(100);
				buf.put(nodeCnt); // node cnt
				for (Entry<Byte, ISensorNode> ent_sensornode : mapRFSensorNode.entrySet()) {
					ISensorNode sensornode = ent_sensornode.getValue();
					int receiveCnt = sensornode.getReceiveCnt();
					if (receiveCnt <= 1) {
						Map<Byte, ISensor> mapSensor = sensornode.getMapSensor();
						byte nid = sensornode.getNid();
						byte sensorCnt = (byte)mapSensor.size();
						//System.out.println("sensorCnt : " + sensorCnt);
						byte battery = sensornode.getBatteryStatus();
						buf.put((byte) ((nid << 4) + sensorCnt));
						buf.put(battery);
						for (Entry<Byte, ISensor> ent_sensor : mapSensor.entrySet()) {				
							ISensor sensor = ent_sensor.getValue();
							int times = 10;
							//if (sensor.getKind().equals("14")) {
							//	times = 100;
							//}
							int value = Math.round(sensor.getValue()*times);
							//System.out.println("getCurrentValuesAppFirstConnected : " + sensor.getSid() + " " + value);
							
							buf.put(sensor.getSid());
							buf.put(ByteUtils.intToByteArray16(value));
							
							//if (sensor.getKind().equals("14")) {
							//	byte[] aa = ByteUtils.intToByteArray16(value);
							//	System.out.println("sss : " + aa.length +  " " + value);
							//}
						}
					} else {
						//System.out.println("not receive : " + this.house_nam + "  " + sensornode.getNid());
						nodeCnt--;
						//sensornode.reset();
						buf.put(0, nodeCnt);
						
					}
					
				}
				
				byte[] sendData = null;
				
				if (nodeCnt > 0) {
					int dataSize = buf.position();
					sendData = new byte[dataSize];
					System.arraycopy(buf.array(), 0, sendData, 0, dataSize);
					
					sensorSendData.setData(sendData);
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
			
	public void getCurrentValues(SensorSendData sensorSendData) {
		byte nodeCnt = (byte) mapRFSensorNode.size();
		if (nodeCnt > 0) {
			//ByteBuffer buf = ByteBuffer.allocate( (nodeCnt*2) + (nodeCnt*3*3*3));
			ByteBuffer buf = ByteBuffer.allocate( 100);
			buf.put(nodeCnt); // node cnt
			for (Entry<Byte, ISensorNode> ent_sensornode : mapRFSensorNode.entrySet()) {
				ISensorNode sensornode = ent_sensornode.getValue();			
				//int receiveCnt = sensornode.getReceiveCnt();
				//if (receiveCnt <= 1) {			
					if (!(ent_sensornode.getKey().equals("99"))) {
						Map<Byte, ISensor> mapSensor = sensornode.getMapSensor();
						byte nid = sensornode.getNid();
						byte sensorCnt = (byte)mapSensor.size();
						byte battery = sensornode.getBatteryStatus();
						//System.out.println("sensorCnt : " + nid + "  " + sensorCnt + "  " + battery);
						buf.put((byte) ((nid << 4) + sensorCnt));
						//System.out.println("nid scnt : " + (byte) ((nid << 4) + sensorCnt));
						buf.put(battery);
						//System.out.println("sensorCnt : " + nid + "  " + battery);
						
						for (Entry<Byte, ISensor> ent_sensor : mapSensor.entrySet()) {				
							ISensor sensor = ent_sensor.getValue();
							int times = 10;
							if (sensor.getKind().equals("14")) {
								times = 100;
							} else if (sensor.getKind().equals("06")) {
								times = 10;
							}
							
							int value = Math.round(sensor.getValue()*times);
							//System.out.println("value : " +ByteUtils.intToByteArray16(value).length);
							buf.put(sensor.getSid());
							
							//System.out.println("sensor : " + sensor.getSid() + "  " + value);
							
							buf.put(ByteUtils.intToByteArray16(value));
							
							
							
							
						}
						
						if (sensorCnt == 0) {
							nodeCnt--;
							buf.put(0, nodeCnt);
						}
					}
					/*
				} else {
					//System.out.println("not receive : " + this.house_nam + "  " + sensornode.getNid());
					nodeCnt--;
					sensornode.reset();
					buf.put(0, nodeCnt);
					//this.control.i2cReset();
				}
				*/
				
				
			}
			
			//System.out.println("nodeCnt : " + nodeCnt);
			byte[] sendData = null;
			
			if (nodeCnt > 0) {
				int dataSize = buf.position();
				sendData = new byte[dataSize];
				System.arraycopy(buf.array(), 0, sendData, 0, dataSize);
				
				sensorSendData.setData(sendData);
			}
			
			//if (dataSize>0) {
			//	sensorSendData.setDataSize(dataSize);;
			//	sensorSendData.setData(buf.toByteArray());
			//}
			
			//buf.reset();
			buf = null;
		}
		
		
	}
	
	public byte[] getPeriodConditionValues() {	
		int grpcnt = mapActGroup.size();
		int dataSize = (grpcnt*5) + 1;
		byte[] sendData = new byte[dataSize];
		int pos = 0;
		sendData[0] = (byte) grpcnt;
		pos = 1;
		
		for (Entry<Byte, IActGroup> ent : mapActGroup.entrySet()) {
			IActGroup actgrp = ent.getValue();				
			sendData[pos] = actgrp.getId();
			pos += 1;
			//int valueCon = Math.round(actgrp.getConValue()*10);
			//System.arraycopy(ByteUtils.intToByteArray16(valueCon), 0, sendData, pos, 2);	
			//pos += 2;			
			int valueCor =  Math.round(actgrp.getCorValue()*10);
			System.arraycopy(ByteUtils.intToByteArray16(valueCor), 0, sendData, pos, 2);	
			pos += 2;
		}
		return sendData;
	}
	
	
	public void setSensorValue(byte nid, byte sid,  byte high, byte low) {
		//System.out.println("nid : " + nid + "  " + sid);
		ISensorNode sensorNode = mapRFSensorNode.get(nid);
		if (sensorNode != null) {
			sensorNode.setSensorValue(sid,high,low);
		}
		
	}
	
	/*
	private void setSensorValued(byte nid, byte[] data) {
		//System.out.println("nid : " + nid + "  " + data.length);
		ISensorNode sensorNode = mapRFSensorNode.get(nid);
		if (sensorNode != null) {
			sensorNode.setSensorValue(data);
		} else {
			//System.out.println("error nid " + nid);
		}
	}
	*/
	
	
	public void setIsAlert(byte con_cde, byte nid, byte sid, byte isalert, int avmin, int avmax) {
		mapRFSensorNode.get(nid).setIsAlert(con_cde, house_cde, sid, isalert,avmin,avmax);
	}
	
	
	
	public void setOppReset() {
		for (Entry<Byte, IActGroup> ent : mapActGroup.entrySet()) {
			IActGroup actgrp = ent.getValue();	
			actgrp.setOppReset();
		}
	}

	public void setLayerSubStop(  byte agid, byte akid, byte layer, byte key) {
		mapActGroup.get(agid).setLayerSubStop(akid, layer, key);
	}
	
	public void setOppFit( byte agid, byte akid, byte layer, byte key, byte orderfit) {
		mapActGroup.get(agid).setOppFit(akid, layer, key, orderfit);
		
	}
	
	public void setOnOff( byte agis, byte akid, byte key, byte onoff) {
		mapActGroup.get(agis).setOnOff(akid,  key, onoff);
	}
	
	public void setAutoManual(byte agid, byte isAuto) {
		mapActGroup.get(agid).setAutoManual(isAuto);
	}
	
	
	
	public void setPriorityUse(byte agid,byte prioID,byte isUse) {
		//mapActGroup.get(agid).setPriorityUse(prioID, isUse);
	}
	
	public void setConditionValue(byte agis, byte ord, byte sub_ord, 
			byte control_h_s, byte control_h_e, int control_v_s, int control_v_e) {
		//mapActGroup.get(agis).setConditionValue(ord,sub_ord,
		//		control_h_s,control_h_e,control_v_s,control_v_e );
		
	}
	
	public void resetCurRule() {
		for (Entry<Byte, IActGroup> ent : mapActGroup.entrySet()) {
			IActGroup actgrp = ent.getValue();	
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//actgrp.resetCurRule();
		}
	}
	
	public void setAutoInterval(byte agis, int oppTime, int waitTime) {
		//mapActGroup.get(agis).setAutoInterval(oppTime, waitTime);
	}
	
	public void updateConditionRule(byte agid, JSONArray data) {
			
		mapActGroup.get(agid).updateConditionRule(data);
	}
	
	public void updateEtc(byte agid, byte etcId,byte val,byte isuse, String sk) {
		mapActGroup.get(agid).updateEtc(etcId,val,isuse);
	}
	
	public void updateRoll(byte agid, byte akid,byte layerid, byte isuse, byte high, byte crate) {
		mapActGroup.get(agid).updateRoll(akid,layerid,isuse,high,crate);
	}
	
	public void insertConditionRule(
			byte agid, byte priority_ord,byte priority_sub_ord) {
		mapActGroup.get(agid).insertConditionRule(priority_ord,priority_sub_ord);
	}
	
	public void deleteConditionRule(
			byte agid, byte priority_ord,byte priority_sub_ord,
			byte next_sub_ord, int next_val_min) {
		mapActGroup.get(agid).deleteConditionRule(priority_ord,priority_sub_ord,next_sub_ord,next_val_min);
	}
	
	
	public void updateCorection(
			byte agid, byte corId,byte corVal, int cor_min, int cor_max, byte isuse) {
		mapActGroup.get(agid).updateCorection(corId,corVal,cor_min,cor_max, isuse);
	}
	
	
	
	
	
	public void setLimitValue(byte agid, byte actkndid, byte actlayerid, byte actlayersubid, int limit_open, int limit_close, int limit_max) {
		mapActGroup.get(agid).setLimitValue(actkndid,actlayerid,actlayersubid,limit_open,limit_close,limit_max);
	}
	
	public void setOpenRateReset(byte agid, byte actkndid, byte actlayerid, byte rate) {
		mapActGroup.get(agid).setOpenRateReset(actkndid,actlayerid,rate);
	}
	
	public void setRunStd(byte agid, byte actkndid, byte onoffid, byte run, byte std, byte it_use) {
		mapActGroup.get(agid).setRunStd(actkndid,onoffid,run,std, it_use);	
	}
	
	public void setLED(byte agid, byte actkndid, byte onoffid, int run) {
		mapActGroup.get(agid).setLED(actkndid,onoffid,run);	
	}
	
	

	public Map<Byte, ISensorNode> getMapSensorNode() {
		return mapRFSensorNode;
	}

	public byte getHouse_cde() {
		return house_cde;
	}

	public boolean isIsuse() {
		return isuse;
	}
	
	
	public void sendToApp(byte cmd, byte agid, byte akid, byte layerid, byte layersubid, byte[] data, Map<String, Object> updateMap) {
		this.control.sendToApp(cmd, this.house_cde, agid, akid, layerid, layersubid, data, updateMap);
	}
	
	public void sendToApp(byte cmd, byte agid, byte akid, byte onoffid,  byte[] data, Map<String, Object> updateMap) {
		this.control.sendToApp(cmd, this.house_cde, agid, akid, onoffid, data, updateMap);
	}
	
	public void sendToApp(byte cmd, byte agid, byte akid, byte onoffid,  byte[] data) {
		this.control.sendToApp(cmd, this.house_cde, agid, akid, onoffid, data);
	}
	
	
	
	public byte[] getOpenRate() {		
		ByteBuffer buf = ByteBuffer.allocate(100);
		buf.put(this.house_cde);
		byte grpCnt = 0;
		for (Entry<Byte, IActGroup> entActGrp : mapActGroup.entrySet()) {
			IActGroup actgrp = entActGrp.getValue();
			byte[] dataActGrp = actgrp.getOpenRate();
			if (dataActGrp != null) {
				buf.put((byte)dataActGrp.length);
				buf.put(dataActGrp);
				grpCnt++;
				buf.put(0, (byte) ( (this.house_cde << 4) + grpCnt));
			}
		}
		
		byte[] sendData = null;
		int dataSize = buf.position();
		//System.out.println("house : " + dataSize);
		if (dataSize > 1) {
			sendData = new byte[dataSize];
			System.arraycopy(buf.array(), 0, sendData, 0, dataSize);
		}
		
		return sendData;
	}

	public int getHouse_direction() {
		return house_direction;
	}
	
	public float getOutTemp() {
		return control.getOutTemp();
	}
	
	public float getOutHum() {
		return control.getOutHum();
	}
	
	public float getWindSpeed() {
		return control.getWindSpeed();
	}
	
	public byte getRainDrop() {
		//System.out.println("house : " + control.getRainDrop());
		return control.getRainDrop();
	}
	
	public int getWindDir() {
		return control.getWindDir();
	}
	
	
	
	public Map<Byte,IActGroup>  getMapActGroup() {
		return mapActGroup;
	}
	
	
	
	
}
