package com.tene.controller.house.actgrp.actknd;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;


import com.tene.constant.EnumActknd;
import com.tene.constant.EnumActknd.ACT_KIND;
import com.pi4j.io.i2c.I2CDevice;
import com.tene.Interfaces.IActGroup;
import com.tene.Interfaces.IActKnd;
import com.tene.Interfaces.IActLayer;
import com.tene.Interfaces.IActOnOff;
import com.tene.controller.house.actgrp.actknd.actlayer.ActLayer;
import com.tene.controller.house.actgrp.actonoff.ActOnOff;
import com.tene.controller.relay.HC595;



public class ActKnd implements IActKnd {
	private IActGroup parent = null;	
	private byte id = 0;
	private byte disp_ord = 0;
	private String  name = null;
	private EnumActknd.ACT_KIND kind = EnumActknd.ACT_KIND.SIDESCREEN;
	private Map<Byte, IActLayer> mapActLayer = new HashMap<Byte, IActLayer>();
	private Map<Byte,IActOnOff> mapActOnOff = new HashMap<Byte,IActOnOff>();
	
	Map<Byte, Boolean> layerSafisfy = new HashMap<Byte, Boolean>();
	
	
	
	//private IActOnOff pan = null;
	
	
	public ActKnd(IActGroup actGroup, JSONObject jobject,  HC595 hc595, I2CDevice I2CFeather,
			boolean isAuto) throws Exception {
		this.parent = actGroup;
		//this.isAuto = actGroup.isAuto();
		
		this.id = (byte) jobject.getInt("key");
		this.disp_ord = (byte) jobject.getInt("disp_ord");
		this.name = jobject.getString("nam");
		this.kind = EnumActknd.setActKind(jobject.getString("knd"));
		
		//System.out.println("this.kind " + this.kind);
		
		if (jobject.has("act_layer")) {
			JSONArray jlayer = jobject.getJSONArray("act_layer");		
			for (int ai=0; ai<jlayer.length();ai++) {
				JSONObject each = (JSONObject) jlayer.get(ai);
				byte layerid = (byte) each.getInt("key");	
				ActLayer actlayer = new ActLayer(this, each, hc595);
				mapActLayer.put(layerid, actlayer);
			}			
		}
		
		if (jobject.has("act_onoff")) {
			JSONArray info_act_onoff = jobject.getJSONArray("act_onoff");		
			for (int ai=0; ai<info_act_onoff.length();ai++) {
				JSONObject act_each = (JSONObject) info_act_onoff.get(ai);
				byte onoffid = (byte) act_each.getInt("key");	
				ActOnOff actOnOff = new ActOnOff(this, act_each, hc595, this.kind, I2CFeather, isAuto);
				mapActOnOff.put(onoffid, actOnOff);
			}
			
			//pan = mapActOnOff.get((byte)1);
		}
	}
	
	
	
	

	@Override
	public byte getId() {
		return id;
	}

	@Override
	public void setOppReset() {
		for (Entry<Byte, IActOnOff> ent : mapActOnOff.entrySet()) {
			IActOnOff act = ent.getValue();	
			act.setOppReset();
		}
		
	}

	@Override
	public void setLayerSubStop(byte layerid, byte layersubid) {
		mapActLayer.get(layerid).setOppNormal(layersubid,(byte)1);
	}
	
	@Override
	public void stopLayer() {
		for (Entry<Byte, IActLayer> ent : mapActLayer.entrySet()) {
			IActLayer act = ent.getValue();	
			act.stopLayer();
		}
	}
	

	@Override
	public void setOppFit(byte layer, byte key, byte orderfit) {
		mapActLayer.get(layer).setOppFit(key, orderfit);		
	}
	
	@Override
	public void setOnOff( byte key, byte onoff) {
		if (mapActOnOff.get(key) != null) {
			mapActOnOff.get(key).setOpp( onoff);
		}
	}
	
	@Override
	public void setOnOff( byte onoff) {
		for (Entry<Byte, IActOnOff> ent : mapActOnOff.entrySet()) {
			IActOnOff Act = ent.getValue();
			Act.setOpp(onoff);
		}
	}

	@Override
	public void setAutoManual(boolean isAuto) {
		
		
		for (Entry<Byte, IActLayer> ent : mapActLayer.entrySet()) {
			IActLayer Act = ent.getValue();
			Act.setAutoManual(isAuto);
		}
		
		for (Entry<Byte, IActOnOff> ent : mapActOnOff.entrySet()) {
			IActOnOff Act = ent.getValue();
			if (this.kind == EnumActknd.ACT_KIND.LIGHT ) {
				try {
					Thread.sleep(3000);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Act.setAutoManual(isAuto);
		}
	}

	@Override
	public void setLimitValue(byte layerid, byte layersubid, int limit_open, int limit_close, int limit_max) {
		mapActLayer.get(layerid).setLimitValue(layersubid, limit_open, limit_close, limit_max);		
	}
	
	@Override
	public void setOpenRateReset( byte actlayerid, byte rate) {
		mapActLayer.get(actlayerid).setOpenRateReset(rate);
	}
	
	@Override
	public void setRunStd(byte onoffid, byte run, byte std, byte it_use) {
		mapActOnOff.get(onoffid).setRunStd(run,std, it_use);	
	}
	
	@Override
	public void setLED(byte onoffid, int run) {
		mapActOnOff.get(onoffid).setLED(run);	
	}
	
	@Override
	public void updateRoll(byte layerid, byte isuse, byte high, byte crate) {
		mapActLayer.get(layerid).updateRoll(isuse, high, crate);	
	}

	@Override
	public void sendToApp(byte cmd, byte layerid, byte layersubid, byte[] data, Map<String, Object> updateMap) {
		this.parent.sendToApp(cmd, this.id, layerid, layersubid, data, updateMap);
	}
	
	@Override
	public void sendToArduino(byte cmd, byte layerid, byte layersubid, byte opp) {
		this.parent.sendToArduino(cmd, this.id, layerid, layersubid, opp);
	}
	
	@Override
	public void sendToApp(byte cmd, byte onoffid,  byte[] data, Map<String, Object> updateMap) {
		this.parent.sendToApp(cmd, this.id, onoffid,  data, updateMap);
	}
	
	@Override
	public void sendToApp(byte cmd, byte onoffid,  byte[] data) {
		this.parent.sendToApp(cmd, this.id, onoffid,  data);
	}

	@Override
	public byte[] getOpenRate() {
		byte[] sendData = null;
		
		ByteBuffer buf = ByteBuffer.allocate(40);
		buf.put(this.id);
		byte subCnt = 0;
		for (Entry<Byte, IActLayer> ent : mapActLayer.entrySet()) {
			IActLayer actSub = ent.getValue();
			byte[] data = actSub.getOpenRate();
			if (data != null) {
				buf.put((byte)data.length);
				buf.put(data);				
				subCnt++;
				buf.put(0, (byte) ( (this.id << 4) + subCnt));
			}
		}
		
		int dataSize = buf.position();
		//System.out.println("kind : " + dataSize);
		//System.out.println("act : " + this.kind + "  " + dataSize);
		if (dataSize > 1) {
			sendData = new byte[dataSize];
			System.arraycopy(buf.array(), 0, sendData, 0, dataSize);
		}
		return sendData;
	}

	

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public byte getDO() {
		return this.disp_ord;
	}

	
	@Override
	public ACT_KIND getKind() {
		return this.kind;
	}


	
	@Override
	public Map<Byte, IActLayer> getLayerInfo() {
		return mapActLayer;
				
	}
	
	@Override
	public Map<Byte,IActOnOff> getOnOffInfo() {
		return mapActOnOff;
	}
	
	/*
	@Override
	public void updateConditionRule(JSONArray data) {
		for (Entry<Byte, IActOnOff> ent : mapActOnOff.entrySet()) {
			IActOnOff Act = ent.getValue();
			Act.updateConditionRule(data);
		}
	}
	*/
}
