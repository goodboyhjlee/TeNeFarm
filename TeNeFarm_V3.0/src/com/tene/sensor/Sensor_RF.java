package com.tene.sensor;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.tene.Interfaces.ISensor;
import com.tene.constant.PushMessageHeader;
import com.tene.database.DBOpp;
import com.tene.exe.Run;
import com.tene.utils.GetCurrentTime;

public class Sensor_RF implements ISensor {
	private String fldNam = null;
	private String kind = null;
	private String owner_nam = null;
	
	private byte sid = 0;
	private float value = 0;
	private boolean isChange = true;
	private byte isAlert = 0;
	private int avmin = 0;
	private int avmax = 0;
	private final int sendWaitTime = 60;
	private int sendCurrentTime = 0;
	private byte nid = 0x02;	
	
	//private float toDayRad = 0;
	
	private Date preTime = null;
	
	//private int pushTerm = 0;
	
	//private String farm_cde = Run.farm_cde;
	//private boolean isFirstReceive = true;
	private Map<String,Object> mapSendDataUpdate = new HashMap<String,Object>();
	
	public Sensor_RF(JSONObject jsensor, String owner_nam, float rad) throws JSONException {
		
		this.owner_nam = owner_nam;
		this.sid = (byte) jsensor.getInt("key");
		this.nid = (byte) jsensor.getInt("nid");
		String ia = jsensor.getString("ia");
		//this.farm_cde = jsensor.getString("fc");
		this.isAlert = (byte) ((ia.equals("Y")) ? 1 : 0);
		this.avmin = jsensor.getInt("al");
		this.avmax = jsensor.getInt("ah");		
		this.kind = jsensor.getString("sk");
		//this.fldNam = "f_" + this.kind;
		this.fldNam = "f_" + this.nid + "_" + this.kind;
		
		if (this.kind.equals("14")) {
			this.value = rad;
		}
		
		//System.out.println("kind : " + this.kind);
		
		mapSendDataUpdate.put("farm_cde", Run.farm_cde);
		
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

	public byte getSid() {
		return sid;
	}
	
	public byte getNid() {
		return nid;
	}

	public byte getPos() {
		return (byte) ((this.sid-1) * 5);
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		if (this.value == value) {
			this.isChange = false;
		} else {
			this.isChange = true;
		}		
		
		
		
		//stacked radiation
		if (this.kind.equals("14")) {
			Date now =  new Date();
			int curHour = now.getHours();
			if ( (value <= 0) && (curHour > 21)) {
				this.value = 0;
			} else {
				int delayTime = 10; // 10 second
				if (preTime == null) {
					delayTime = 10;
				} else {
					delayTime = GetCurrentTime.getIncSecond(preTime, now);
				}
				
				float jullValue = value * delayTime;
				float mjValue = jullValue / 1000000;
				this.value += mjValue;
				//System.out.println("rad : " + value + " " + curHour + "  " + delayTime + "  " + this.value);
				
				preTime = new Date();
			}
			
			
		} else {
			this.value = value;
		}
		
		if (isAlert == 1) {
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
					params += "sk=" + this.kind + "&";
					params += "v=" + value;
					params += "&usr_id=" + com.tene.exe.Run.usr_id;
					params += "&hm=" + owner_nam;
					
					//System.out.println(params);
					//System.out.println(this.kind);
					
					if (this.kind.equals("01")) {
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

	

	public String getFldNam() {
		return fldNam;
	}

	
	
	@Override
	public void setIsAlert(byte con_cde, byte house_cde, byte isalert, int avmin, int avmax) {
		
		//System.out.println("setIsAlert :" + isalert + "  " + avmin);
		this.isAlert = isalert;
		this.avmin = avmin;
		this.avmax = avmax;
		
		put("id","updateisalert");
		
		put("con_cde",con_cde);		
		put("house_cde",house_cde);
		put("nid",nid);		
		put("sid",sid);
		put("isalert",( (isalert ==  (byte)1) ? "Y" : "N"));
		put("alert_low",avmin);		
		put("alert_high",avmax);
		
		
		update();
	
		
		
	}
	
	/*
	@Override
	public void setAlertValue(byte con_cde, byte house_cde,int avmin, int avmax) {
		this.avmin = avmin;
		this.avmax = avmax;
		
		put("id","updateisalertvalue");
		put("con_cde",con_cde);		
		put("house_cde",house_cde);
		put("nid",nid);		
		put("sid",sid);
		put("alert_low",avmin);
		put("alert_high",avmax);
		update();
		
	}
	*/

	@Override
	public String getKind() {
		// TODO Auto-generated method stub
		return this.kind;
	}

	@Override
	public boolean isChange() {
		return isChange;
	}

	
	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}

	

}
