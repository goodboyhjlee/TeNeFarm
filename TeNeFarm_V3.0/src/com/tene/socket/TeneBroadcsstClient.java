package com.tene.socket;


//import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tene.Interfaces.IBroadCast;
import com.tene.constant.PushMessageHeader;
import com.tene.database.DBOpp;
import com.tene.exe.Run;
//import com.tene.forecast.Forecast;

//public class TeneBroadcsstClient extends Thread implements IBroadCast  {
public class TeneBroadcsstClient implements IBroadCast  {
	//private DatagramSocket ds = null;
	//private ISensorNode sensorNode = null;
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
	
	//public TeneBroadcsstClient(ISensorNode sensorNode) {
	public TeneBroadcsstClient(JSONArray jSensorNode) {
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
	
	private void sendMessage(byte sid, String kind) {
		byte iaAlert = this.mapAlert.get(sid).getIsAlert();
		
		if ( iaAlert == 1) {
			
			int avmin = this.mapAlert.get(sid).getAvmin();
			int avmax = this.mapAlert.get(sid).getAvmax();
			float value = outTemp;
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
	public void setTemperature(float value) {
		sendMessage((byte)1, "08");	
		this.outTemp = value;
	}

	@Override
	public void setHumidity(float value) {
		sendMessage((byte)2, "09");	
		this.outHum = value;
	}

	@Override
	public void setRadiation(float value) {
		this.outRad = value;
	}

	@Override
	public void setRainDrop(byte value) {
		sendMessage((byte)3, "01");	
		this.rainDrop =  value;
	}

	@Override
	public void setWindDir(int value) {
		
		this.winddir = value;
	}

	@Override
	public void setWindSpeed(float value) {
		sendMessage((byte)4, "02");	
		this.windspeed = value;
	}

	@Override
	public byte[] getSensorValue() {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
