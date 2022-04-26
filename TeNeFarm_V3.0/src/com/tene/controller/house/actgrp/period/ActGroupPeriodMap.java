package com.tene.controller.house.actgrp.period;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tene.constant.ConstOperationKnd;
import com.tene.utils.GetCurrentTime;


public class ActGroupPeriodMap {
	private boolean isUse = true;
	private float oppRange = (float) 0.5;
	//private float corVar = 0;
	//private float curValue = 0;
	private float corValue = 0;
	
	private int timeRate = 0;
	
	//private Calendar today = Calendar.getInstance();
	
	//private Map<Byte,ActGroupPriority> mapPriority = new HashMap<Byte,ActGroupPriority>();
	private Map<Byte,ActGroupPeriod> mapActGroupPeriod = new HashMap<Byte,ActGroupPeriod>();
	
	private ActGroupPeriod curPeriod = null;
	
	
	
	public ActGroupPeriodMap(JSONArray periods) {
		try {
			//System.out.println(periods);
			byte len = (byte) periods.length();
			for (byte i=0; i<len;i++) {
				JSONObject begin_period = (JSONObject) periods.get(i);
				byte step = (byte) begin_period.getInt("mkey");
				int begin_hm = begin_period.getInt("hm");
				int begin_val = begin_period.getInt("sval");
				
				//byte tknd = (byte)begin_period.getInt("hm");
				//byte tinc = (byte)begin_period.getInt("sval");
				//byte sval = (byte)begin_period.getInt("hm");
				//byte hm = (byte)begin_period.getInt("sval");
				
				JSONObject end_period = null;
				if (periods.isNull(i+1)) {
					end_period = (JSONObject) periods.get(0);
				} else {
					end_period = (JSONObject) periods.get(i+1);
				}				
				int end_hm = end_period.getInt("hm");
				int end_val = end_period.getInt("sval");
				
				mapActGroupPeriod.put(step, new ActGroupPeriod(begin_hm,begin_val,end_hm,end_val ));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ActGroupPeriodMap(JSONArray periods, byte kk){
		try {
			//System.out.println(periods);
			byte len = (byte) periods.length();
			for (byte i=0; i<len;i++) {
				JSONObject begin_period = (JSONObject) periods.get(i);
				byte step = (byte) begin_period.getInt("mkey");				
				byte tknd = (byte)begin_period.getInt("tknd");
				byte tinc = (byte)begin_period.getInt("tinc");
				byte sval = (byte)begin_period.getInt("sval");
				byte hm = (byte)begin_period.getInt("hm");
				
				
				
				mapActGroupPeriod.put(step, new ActGroupPeriod(tknd,tinc,sval,hm ));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte getTknd(byte step) {
		return mapActGroupPeriod.get(step).getTKnd();
	}
	
	public byte getTinc(byte step) {
		return mapActGroupPeriod.get(step).getTinc();
	}
	
	public byte getSval(byte step) {
		return mapActGroupPeriod.get(step).getSval();
	}
	
	public byte getHm(byte step) {
		return mapActGroupPeriod.get(step).getHm();
	}
	
	public int getStepValue(byte step) {
		return mapActGroupPeriod.get(step).getSenorValue();
	}
	
	public int getFanValue(byte step) {
		return mapActGroupPeriod.get(step).getFanValue();
	}
	
	public void decision(  CurValues refValues) {
		float sensorValue = refValues.getSensorValue();
		byte opp = ConstOperationKnd.STOP;
		
		int curHM = GetCurrentTime.getCurrentHM();
		curPeriod = mapActGroupPeriod.get(mapActGroupPeriod.size()-1);
		for(Entry<Byte, ActGroupPeriod> ent : mapActGroupPeriod.entrySet()) {
			ActGroupPeriod period = ent.getValue();
			if (period.isMyTurn(curHM)) {
				curPeriod = period;
				break;
			}
		}
		curPeriod.getCurValue(curHM, sensorValue, refValues);
		float targetValue = refValues.getValue();
		
		float minV = (float) (targetValue - 0.5);
		float maxV = (float) (targetValue + 0.5);
		
		if (sensorValue > maxV)
			opp = ConstOperationKnd.OPEN;	
		else if (sensorValue < minV)
			opp = ConstOperationKnd.CLOSE;
		else
			opp = ConstOperationKnd.STOP;
		
		
		
		refValues.setOpp(opp);
	}
	
	
	
	
	public float getCorValue() {
		return corValue;
	}
	
	
	public void setPriorityUse(byte periodID, byte sub_ord, boolean isuse) {
		this.isUse = isuse;
	}
	
	public void updateConditionRule(JSONArray periods) {
		//mapActGroupPeriod.clear();
		
		try {
			byte len = (byte) periods.length();
			for (byte i=0; i<len;i++) {
				
				JSONObject begin_period = (JSONObject) periods.get(i);
				byte step = (byte) begin_period.getInt("mkey");
				int begin_hm = begin_period.getInt("hm");
				int begin_val = begin_period.getInt("sval");
				
				JSONObject end_period = null;
				if (periods.isNull(i+1)) {
					end_period = (JSONObject) periods.get(0);
				} else {
					end_period = (JSONObject) periods.get(i+1);
				}				
				int end_hm = end_period.getInt("hm");
				int end_val = end_period.getInt("sval");
				
				mapActGroupPeriod.put(step, new ActGroupPeriod(begin_hm,begin_val,end_hm,end_val ));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 
	}
	
	public void updateConditionRule(JSONArray periods, byte kk) {
		//mapActGroupPeriod.clear();
		
		try {
			byte len = (byte) periods.length();
			for (byte i=0; i<len;i++) {
				
				JSONObject begin_period = (JSONObject) periods.get(i);
				
				//System.out.println(begin_period);
				byte step = (byte) begin_period.getInt("mkey");
				byte tknd = (byte)begin_period.getInt("tknd");
				byte tinc = (byte)begin_period.getInt("tinc");
				byte sval = (byte)begin_period.getInt("sval");
				byte hm = (byte)begin_period.getInt("hm");
				
				mapActGroupPeriod.put(step, new ActGroupPeriod(tknd,tinc,sval,hm ));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 
	}
	
	public void insertConditionRule(byte priority_ord,
			byte priority_sub_ord) {
		
		try {
			JSONObject jsub = new JSONObject();
			jsub.put("ord", priority_ord);
			jsub.put("sub_ord", priority_sub_ord);
			jsub.put("val_min", 0);
			jsub.put("val_max", 0);
			
			jsub.put("rt_hs", 0);
			jsub.put("rt_ms", 0);
			jsub.put("rt_h", 0);
			jsub.put("rt_m", 0);
			
			
			
			ActGroupPriorityRange range = new ActGroupPriorityRange(jsub);
			range.setTim_knd((byte) 4);
			range.setTim_inc((byte) 0);
			range.setRt_h((byte) 0);
			range.setRt_m((byte) 0);
			range.setVal_min(0);
			range.setVal_max(0);
			
			
			//mapActGroupPriorityRange.put(priority_sub_ord, range);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void deleteConditionRule(
			 byte priority_sub_ord,
			byte next_sub_ord, int next_val_min) {
		//mapActGroupPriorityRange.remove(priority_sub_ord);
		//mapActGroupPriorityRange.get(next_sub_ord).setVal_min(next_val_min);
	}
}
