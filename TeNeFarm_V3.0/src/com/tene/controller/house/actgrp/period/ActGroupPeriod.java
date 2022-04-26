package com.tene.controller.house.actgrp.period;


public class ActGroupPeriod {
	private int begin_hm = 0;
	private int begin_val = 0;
	private int end_hm = 0;
	private int end_val = 0;
	
	private byte tknd = 0;
	private byte tinc = 0;
	private byte sval = 0;
	private byte hm = 0;
	
	public ActGroupPeriod(int begin_hm, int begin_val, int end_hm, int end_val ) throws Exception {
		this.begin_hm = begin_hm;
		this.begin_val = begin_val;
		this.end_hm = end_hm;
		this.end_val = end_val;
	}
	
	public ActGroupPeriod(byte tknd, byte tinc, byte sval, byte hm ) throws Exception {
		this.tknd = tknd;
		this.tinc = tinc;
		this.sval = sval;
		this.hm = hm;
	}
	
	public byte getTKnd() {
		return this.tknd;
	}
	
	public byte getTinc() {
		return this.tinc;
	}
	
	public byte getSval() {
		return this.sval;
	}
	
	public byte getHm() {
		return this.hm;
	}
	
	public int getSenorValue() {
		return this.begin_val;
	}
	
	public int getFanValue() {
		return this.begin_hm;
	}
	
	public boolean isMyTurn(int hm) {
		//sdfsdf
		boolean ret = false;
		if ( (end_hm - begin_hm) > 0 ) {
			if ( (hm >= begin_hm) && (hm < end_hm) ) {
				ret = ( (hm >= begin_hm) && (hm < end_hm) );
			}			
		} else {
			if ((hm - end_hm) > 0 ) { //befor 24
				ret = (hm >= begin_hm);
				//overRate = (curTime - beginHm) / ( ((24*60) - beginHm) + endHm);
			} else { // over 24
				ret = (hm < end_hm);
				//overRate = ( ((24*60) - beginHm) + curTime) / ( ((24*60) - beginHm) + endHm);
			}
		}
		return ret;
	}
	
	public void getCurValue(int hm, float sensorValue, CurValues refValues) {	
		
		//System.out.println(end_hm + "  " + begin_hm);
		
		if ( (end_hm - begin_hm) > 0 ) {
			int inghm = hm - begin_hm;
			float rate = (float) ((inghm*1.0) / (end_hm - begin_hm));			
			refValues.setValue(begin_val + (rate * (end_val - begin_val)));		
		} else {
			float rate = 0;
			if ((hm - end_hm) > 0 ) { //befor 24
				rate = (float) (((hm*1.0) - begin_hm) / ( ((24*60) - begin_hm) + end_hm));
			} else { // over 24				
				rate = (float)( ((24*60) - begin_hm) + (hm*1.0)) / ( ((24*60) - begin_hm) + end_hm);
			}
			refValues.setValue(begin_val + (rate * (end_val - begin_val)));
		}
		
		
		
		
		//float overrate = 0;
		float overrate = refValues.getOutTemp() - refValues.getSensorValue();
		
		
		/*
		float maxValue = Math.max(begin_val, end_val);
		float minValue = Math.min(begin_val, end_val);
		
		if ( (minValue <= sensorValue) && (maxValue >= sensorValue   ) ) {
			//normal
		} else {			
			//out of range
			overrate = sensorValue - maxValue;
			
		}
		*/
		
		refValues.setOverrate(overrate);	
		
		
		
		//System.out.println(hm + "  " + begin_hm + "  " + end_hm + "  " + inghm + "  "  + rate);
		//return begin_val + (rate * (end_val - begin_val));
	}
	
	
	
	
	/*
	public float getPeriodCurValue(int rate) {
		return  (float) (((val_max - val_min) * (rate*0.01)) + val_min);
		//corValue = curValue+corVar;
		//return corValue;
	}
	*/
	
	
	public boolean run(boolean upDepthSatisfy) {
		boolean satisfy = true;
		return satisfy;
	}

	public void setConditionValue(int hm, int val) {
		this.begin_hm = hm;
		this.begin_val = val;
		//this.hour_s = control_h_s;
		//this.hour_e = control_h_e;
		//this.val_min = control_v_s;
		//this.val_max = control_v_e;
		
		//System.out.println(hour_s + " " + hour_e + "  " + val_min + " " + val_max);
		
	}
	
	
	

	

	
}
