package com.tene.socket;


import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

//import java.util.ArrayList;
//import java.util.List;

import com.tene.Interfaces.IBroadCast;
import com.tene.constant.PushMessageHeader;
import com.tene.database.DBOpp;
import com.tene.exe.Run;
import com.tene.utils.ByteUtils;

public class TeneBroadcsstLocal implements IBroadCast  {
	
	private byte rainDrop = 0;
	private float outTemp = 0;
	private float outHum = 0;
	private float outRad = 0;
	private int winddir = 0;
	private float windspeed = 0;
	
	private Map<Byte,Alert> mapAlert = new HashMap<Byte,Alert>();
	private Map<String,Object> mapSendDataUpdate = new HashMap<String,Object>();
	
	private final int sendWaitTime = 60;
	private int sendCurrentTime = 0;
	
	private boolean sendop = false;
	
	public TeneBroadcsstLocal(JSONArray jSensorNode) {
		super();
		try {
			int snCnt = jSensorNode.length();
			for (int i=0; i<snCnt; i++) {
				JSONObject sensornode = jSensorNode.getJSONObject(i);
				//String snKnd = sensornode.getString("knd");
				byte nid = (byte) sensornode.getInt("key");
				
				boolean snIsUse = sensornode.getString("iu").equals("Y") ? true : false;
				
				//System.out.println("nid : " + nid + "  " + snIsUse + "  " + snKnd);
				if (snIsUse) {
					//if (snKnd.equals("05")) {
						this.sendop = true;
						JSONArray jSensor = sensornode.getJSONArray("sensor");
						int sCnt = jSensor.length();
						//System.out.println("sCnt : " + sCnt);
						for (int si=0; si<sCnt; si++) {
							JSONObject sensor = jSensor.getJSONObject(si);
							byte sid =  (byte)sensor.getInt("key");
							String sk =  sensor.getString("sk");
							String ia =  sensor.getString("ia");
							byte isAlert = (byte) (ia.equals("Y")? 1 : 0);
							int al =  sensor.getInt("al");
							int ah =  sensor.getInt("ah");
							//System.out.println("sk : " + sk);
							mapAlert.put(sid, new Alert(Run.farm_cde,
									nid,sid, isAlert,al,ah  ));
						}
					//} 					
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		/*
		try {
			ds = new DatagramSocket(40000);
			start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
	
	public TeneBroadcsstLocal() {
		super();
		
	}
	
	@Override
	public float getTemperature() {
		return outTemp;
	}
	
	@Override
	public float getHumidity() {
		return outHum;
	}
	
	@Override
	public float getRadiation() {
		return outRad;
	}
	
	@Override
	public byte getRainDrop() {
		//System.out.println("getRainDrop : " + this.rainDrop);
		return rainDrop;
	}
	
	@Override
	public int getWindDir() {
		return winddir;
	}
	
	@Override
	public float getWindSpeed() {
		return windspeed;
	}
	
	

	@Override
	public byte[] getSensorValue() {
		
		//System.out.println("sfsfsfdf");
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		//byte dataSize = 0;
		try {
			int value = 0 ;
			buf.write((byte) 1); // sid outTemp			
			value = Math.round(outTemp*10);			
			buf.write(ByteUtils.intToByteArray16(value));
			
			buf.write((byte) 2); // sid outHum
			value = Math.round(outHum*10);
			buf.write(ByteUtils.intToByteArray16(value));
									
			buf.write((byte) 3); // sid rainDrop
			value = Math.round(rainDrop);
			//System.out.println(value);
			//System.out.println(ByteUtils.intToByteArray16(value));
			buf.write(ByteUtils.intToByteArray16(value));
			
			buf.write((byte) 4); // sid windspeed
			value = Math.round(windspeed*10);
			buf.write(ByteUtils.intToByteArray16(value));
			
			buf.write((byte) 5); // sid winddir
			value = Math.round(winddir*10);
			buf.write(ByteUtils.intToByteArray16(value));
			
			
			
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return buf.toByteArray();
	}
	
	private void sendMessage(byte sid, String kind, float value) {
		if (this.sendop) {
			byte iaAlert = this.mapAlert.get(sid).getIsAlert();
			
			if ( iaAlert == 1) {
				
				int avmin = this.mapAlert.get(sid).getAvmin();
				int avmax = this.mapAlert.get(sid).getAvmax();
				//float value = outTemp;
				if ( (value < avmin) ||  (value > avmax) ) {
					sendCurrentTime++;
					//System.out.println(sendCurrentTime);
					if (sendWaitTime < sendCurrentTime) {
						sendCurrentTime = 0;
						String params = "key=AGGhings5131220b&";
						if  (value < avmin) {
							params += "ud=0&";
						}
						if  (value > avmax) {
							params += "ud=1&";
						}
						params += "k=" + PushMessageHeader.OUTOFRANGE + "&";
						params += "sk=" + kind + "&";
						params += "v=" + value;
						params += "&usr_id=" + Run.usr_id;
						params += "&hm=" + "±â»ó";
						
						if (kind.equals("01")) {
							if (value > 0) {
								DBOpp.sendPushMsg(params);
							}
						} else {
							DBOpp.sendPushMsg(params);
						}
						
						//pushTerm++;
					}
				}
			}
		}
		
	}
	
	private void put(String key, Object value) {
		mapSendDataUpdate.put(key, value);
	}
	
	private void update() {
		try {
			//System.out.println("mapSendDataUpdate : " + mapSendDataUpdate);
			DBOpp.update(mapSendDataUpdate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setIsAlert(String farm_cde, byte nid,  byte sid, byte isalert, int avmin, int avmax) {
		this.mapAlert.get(sid).setIsAlert(isalert);
		this.mapAlert.get(sid).setAvmin(avmin);
		this.mapAlert.get(sid).setAvmax(avmax);
		
		
		put("id","updateisalert_ws");
		
		put("farm_cde",farm_cde);	
		put("nid",nid);		
		put("sid",sid);
		put("isalert",( (isalert ==  (byte)1) ? "Y" : "N"));
		put("alert_low",avmin);		
		put("alert_high",avmax);
		
		
		update();
	}

	@Override
	public void setTemperature(float value) {
		sendMessage((byte)1, "08", value);	
		this.outTemp = value;
	}

	@Override
	public void setHumidity(float value) {
		sendMessage((byte)2, "09", value);	
		this.outHum = value;
	}

	@Override
	public void setRadiation(float value) {
		this.outRad = value;
	}

	@Override
	public void setRainDrop(byte value) {
		sendMessage((byte)3, "01", value);	
		this.rainDrop =  value;
	}

	@Override
	public void setWindDir(int value) {
		
		this.winddir = value;
	}

	@Override
	public void setWindSpeed(float value) {
		sendMessage((byte)4, "02", value);	
		this.windspeed = value;
	}

	
	

}
