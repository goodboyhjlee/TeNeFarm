package com.tene.controller.house.actgrp.period;


import org.json.JSONObject;


public class ActGroupPriorityRange {
	
	//private float curValue = 0;
	//private float corValue = 0;

	private byte ord = 0;
	private byte sub_ord = 0;
	
	private byte tim_knd = 0;
	private byte tim_inc = 0;
	
	private byte rt_hs = 0;
	private byte rt_ms = 0;
	
	private byte rt_h = 0;
	private byte rt_m = 0;
	
	
	
	private int val_min = 0;
	private int val_max = 0;
	
	//private float corVar = 0;
	//private int rate = 0;
	
	
	public ActGroupPriorityRange(JSONObject con_me) throws Exception {
		
		//System.out.println(con_me);
		
		this.ord = (byte) con_me.getInt("ord");
		this.sub_ord =  (byte)con_me.getInt("sub_ord");
		this.val_min = con_me.getInt("val_min");
		this.val_max = con_me.getInt("val_max");
		
		this.rt_hs = con_me.has("rt_hs") ? (byte)con_me.getInt("rt_hs") : 0;
		this.rt_ms = con_me.has("rt_ms") ? (byte)con_me.getInt("rt_ms")  : 0;
		this.rt_h = con_me.has("rt_h") ? (byte)con_me.getInt("rt_h")  : 0;
		this.rt_m = con_me.has("rt_m") ? (byte)con_me.getInt("rt_m")  : 0;
		
		//this.corVar = corVar;
		
		//calculate(corVar);
		
	}
	
	
	
	public float getPeriodCurValue(int rate) {
		return  (float) (((val_max - val_min) * (rate*0.01)) + val_min);
		//corValue = curValue+corVar;
		//return corValue;
	}
	
	
	public boolean run(boolean upDepthSatisfy) {
		boolean satisfy = true;
		return satisfy;
	}

	public void setConditionValue(byte control_h_s, byte control_h_e, int control_v_s, int control_v_e) {
		//this.hour_s = control_h_s;
		//this.hour_e = control_h_e;
		this.val_min = control_v_s;
		this.val_max = control_v_e;
		
		//System.out.println(hour_s + " " + hour_e + "  " + val_min + " " + val_max);
		
	}
	
	
	

	public int getVal_min() {
		return val_min;
	}


	public void setVal_min(int val_min) {
		this.val_min = val_min;
	}


	public int getVal_max() {
		return val_max;
	}


	public void setVal_max(int val_max) {
		this.val_max = val_max;
	}

	public byte getOrd() {
		return ord;
	}

	public void setOrd(byte ord) {
		this.ord = ord;
	}

	

	

	public byte getSub_ord() {
		return sub_ord;
	}

	public byte getTim_knd() {
		return tim_knd;
	}

	public void setTim_knd(byte tim_knd) {
		this.tim_knd = tim_knd;
	}

	public byte getTim_inc() {
		return tim_inc;
	}

	public void setTim_inc(byte tim_inc) {
		this.tim_inc = tim_inc;
	}

	public byte getRt_h() {
		return rt_h;
	}

	public void setRt_h(byte rt_h) {
		this.rt_h = rt_h;
	}

	public byte getRt_m() {
		return rt_m;
	}

	public void setRt_m(byte rt_m) {
		this.rt_m = rt_m;
	}

	public byte getRt_hs() {
		return rt_hs;
	}

	public void setRt_hs(byte rt_hs) {
		this.rt_hs = rt_hs;
	}

	public byte getRt_ms() {
		return rt_ms;
	}

	public void setRt_ms(byte rt_ms) {
		this.rt_ms = rt_ms;
	}

	
}
