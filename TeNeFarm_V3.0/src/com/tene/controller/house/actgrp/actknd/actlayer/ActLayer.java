package com.tene.controller.house.actgrp.actknd.actlayer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tene.constant.ConstOperationKnd;
import com.tene.Interfaces.IActKnd;
import com.tene.Interfaces.IActLayer;
import com.tene.Interfaces.IActLayerSub;
import com.tene.controller.house.actgrp.actknd.actlayer.actlayersub.ActLayerSub;
import com.tene.controller.relay.HC595;


public class ActLayer implements IActLayer {
	private IActKnd parent = null;
	private byte id = 0;
	private byte layer = 0;
	private boolean risuse = false;
	private byte high = 0;
	private byte crate = 0;
	private Map<Byte, IActLayerSub> mapChildren = new HashMap<Byte, IActLayerSub>();
	
	
	public ActLayer(int i) throws Exception {
				
	}
	
	public ActLayer(IActKnd parent, JSONObject jobject,  HC595 hc595) throws Exception {
		this.parent = parent;	
		
		this.id = (byte) jobject.getInt("key");
		this.layer = (byte) jobject.getInt("layer");
		this.risuse = jobject.getString("risuse").equals("Y") ? true : false;		
		this.high = (byte) jobject.getInt("highsval");
		this.crate = (byte) jobject.getInt("crate");
		
		//System.out.println("this.layer " + this.layer);
		if (jobject.has("act_layer_sub")) {
			JSONArray jlayer = jobject.getJSONArray("act_layer_sub");		
			for (int ai=0; ai<jlayer.length();ai++) {
				JSONObject each = (JSONObject) jlayer.get(ai);
				byte layersubid = (byte) each.getInt("key");	
				ActLayerSub layersub = new ActLayerSub(this, each,  hc595);
				mapChildren.put(layersubid, layersub);
			}
		}		
	}


	@Override
	public byte getId() {
		return id;
	}


	@Override
	public float getCorValue() {
		return 0;
	}


	@Override
	public void setOppReset() {
		
	}
	
	
	@Override
	public void stopLayer() {
		for (Entry<Byte, IActLayerSub> ent : mapChildren.entrySet()) {
			ent.getValue().setOppNormal(ConstOperationKnd.STOP);
		}
	}

	@Override
	public void setOppNormal(byte layersubid,  byte opp) {
		//System.out.println("setOppNormal : " + layersubid + "  " + opp);
		mapChildren.get(layersubid).setOppNormal(opp);
	}


	@Override
	public void setOppFit(byte layersubid, byte orderfit) {
		mapChildren.get(layersubid).setOppFit(orderfit);
	}


	@Override
	public void setAutoManual(boolean isAuto) {
		for (Entry<Byte, IActLayerSub> ent : mapChildren.entrySet()) {
			ent.getValue().setAutoManual(isAuto);;
		}
	}


	@Override
	public void setLimitValue(byte layersubid,  int limit_open, int limit_close, int limit_max) {
		mapChildren.get(layersubid).setLimitValue(limit_open,limit_close, limit_max );		
	}
	
	@Override
	public void setOpenRateReset(  byte rate) {
		for (Entry<Byte, IActLayerSub> ent : mapChildren.entrySet()) {
			ent.getValue().setOpenRateReset(rate);;
		}
		
	}
	
	@Override
	public void updateRoll( byte isuse, byte high, byte crate) {
		this.risuse = isuse == (byte)0 ? false : true;
		this.high = high;
		this.crate = crate;
	}

	@Override
	public void sendToApp(byte cmd, byte layersubid, byte[] data, Map<String, Object> updateMap) {
		this.parent.sendToApp(cmd, this.id, layersubid, data, updateMap);
	}
	
	@Override
	public void sendToArduino(byte cmd, byte layersubid, byte opp) {
		this.parent.sendToArduino(cmd, this.id, layersubid, opp);
	}
	
	


	@Override
	public byte[] getOpenRate() {
		byte[] sendData = null;
		ByteBuffer buf = ByteBuffer.allocate(20);
		buf.put(this.id);
		byte subCnt = 0;
		for (Entry<Byte, IActLayerSub> ent : mapChildren.entrySet()) {
			IActLayerSub actSub = ent.getValue();
			//System.out.println("layer : " + actSub.getOpp());
			if (actSub.getOpp() != ConstOperationKnd.STOP) {
				buf.put( (byte) ((actSub.getId() << 4) + actSub.getOpp() ) ); 
				buf.put(actSub.getOpenRate());					
				subCnt++;
				buf.put(0, (byte) ( (this.id << 4) + subCnt));
			}
		}
		
		int dataSize = buf.position();
		//System.out.println("layer : " + dataSize);
		if (dataSize > 1) {
			sendData = new byte[dataSize];
			System.arraycopy(buf.array(), 0, sendData, 0, dataSize);
		}
		
		return sendData;
	}

	

	@Override
	public byte getLayer() {
		return this.layer;
	}
	
	

	
	
	@Override
	public boolean isAllClosed() {
		boolean ret = true;
		for (Entry<Byte, IActLayerSub> ent : mapChildren.entrySet()) {
			IActLayerSub actLayersub = ent.getValue();
			ret = ret && actLayersub.isClosed();			
		}
		return ret;
	}
	
	@Override
	public void allClose() {
		for (Entry<Byte, IActLayerSub> ent : mapChildren.entrySet()) {
			IActLayerSub actLayersub = ent.getValue();
			actLayersub.setOppNormal(ConstOperationKnd.CLOSE);			
		}
	}
	
	@Override
	public boolean isAllOpend() {
		boolean ret = true;
		for (Entry<Byte, IActLayerSub> ent : mapChildren.entrySet()) {
			IActLayerSub actLayersub = ent.getValue();
			ret = ret && actLayersub.isOpend();			
		}
		return ret;
	}
	
	@Override
	public void allOpen() {
		for (Entry<Byte, IActLayerSub> ent : mapChildren.entrySet()) {
			IActLayerSub actLayersub = ent.getValue();
			actLayersub.setOppNormal(ConstOperationKnd.OPEN);			
		}
	}
	
	@Override
	public byte getOpenState() {
		IActLayerSub actLayerLeft = mapChildren.get(ConstOperationKnd.LEFT);
		IActLayerSub actLayerRight = mapChildren.get(ConstOperationKnd.RIGHT);
		byte left = 1;
		byte right = 1;
		if (actLayerLeft.isClosed())
			left = 2;
		if (actLayerRight.isClosed())
			right = 2;
		if (actLayerLeft.isOpend())
			left = 0;
		if (actLayerRight.isOpend())
			right = 0;
		
		return (byte) ((left << 2) + right );
	}



	@Override
	public Map<Byte, IActLayerSub> getSubLayerInfo() {
		return mapChildren;
	}
	
}
