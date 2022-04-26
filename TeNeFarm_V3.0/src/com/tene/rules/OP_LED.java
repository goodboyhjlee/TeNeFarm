package com.tene.rules;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.pi4j.io.i2c.I2CDevice;
import com.tene.Interfaces.IActKnd;
import com.tene.Interfaces.IActLayer;
import com.tene.Interfaces.IActOnOff;
import com.tene.Interfaces.IOperationPattern;
import com.tene.Interfaces.ISensor;
import com.tene.constant.ConstOperationKnd;
import com.tene.constant.EnumActknd;
import com.tene.controller.house.actgrp.period.ActGroupPeriodMap;


public class OP_LED implements IOperationPattern {
	
	private ActGroupPeriodMap priorityMap = null;
	private Map<Byte, IActOnOff> onofflist = null;
	
	private byte opp = -1;
	private byte prevOpp = -1;
	
	private I2CDevice I2CFeather = null;
	
	
	@Override
	public void seti2c(I2CDevice I2CFeather) {
		
		this.I2CFeather = I2CFeather;
	}
	
	@Override
	public void setisUseWindDir(boolean isUse) {
		
	}
	
	@Override
	public void setisUseWindSpeed(boolean isUse) {
		
	}
	
	@Override
	public void setisUseRainDrop(boolean isUse) {
		
	}
	
	@Override
	public void setPreStep(byte step) {
				
	}

	@Override
	public boolean isRuning(float sensorValue, float outTemp) {
		return false;
	}
	

	@Override
	public boolean isRuning(Date now, int sunriseH, int sunriseM, int sunsetH, int sunsetM) {
		
		byte tknd = priorityMap.getTknd((byte)1);
		byte tinc = priorityMap.getTinc((byte)1);
		byte sval = priorityMap.getSval((byte)1);
		byte hm = priorityMap.getHm((byte)1);
		Calendar cal = Calendar.getInstance();
		
		Date condStTime = new Date();
		Date condEndTime = new Date();
		
		
		
		
		/*
		 * condEndTime.setHours(sunsetH);
			condEndTime.setMinutes(sunsetM);
			condEndTime.setSeconds(0);
		 */
		
		if (tknd == 0) {
			condStTime.setHours(sunriseH);
			condStTime.setMinutes(sunriseM);
			condStTime.setSeconds(0);
		} else if (tknd == 3) {
			condStTime.setHours(sunsetH);
			condStTime.setMinutes(sunsetM);
			condStTime.setSeconds(0);
		} else if (tknd == 1) {
			condStTime.setHours(sunriseH);
			condStTime.setMinutes(sunriseM);
			condStTime.setSeconds(0);			
			
			cal.setTime(condStTime);			
			cal.add(Calendar.MINUTE, sval*-1);			
			condStTime = cal.getTime();
		} else if (tknd == 2) {
			condStTime.setHours(sunriseH);
			condStTime.setMinutes(sunriseM);
			condStTime.setSeconds(0);			
			//Calendar cal = Calendar.getInstance();
			cal.setTime(condStTime);						
			cal.add(Calendar.MINUTE, sval);
			condStTime = cal.getTime();
		} else if (tknd == 4) {
			condStTime.setHours(sunsetH);
			condStTime.setMinutes(sunsetM);
			condStTime.setSeconds(0);			
			//Calendar cal = Calendar.getInstance();
			cal.setTime(condStTime);						
			cal.add(Calendar.MINUTE, sval*-1);
			condStTime = cal.getTime();
		} else if (tknd == 5) {
			condStTime.setHours(sunsetH);
			condStTime.setMinutes(sunsetM);
			condStTime.setSeconds(0);			
			//Calendar cal = Calendar.getInstance();
			cal.setTime(condStTime);						
			cal.add(Calendar.MINUTE, sval);
			condStTime = cal.getTime();
		} else if (tknd == 6) {
			condStTime.setHours(sval);
			condStTime.setMinutes(0);
			condStTime.setSeconds(0);			
		}
		
		
		if (tinc == 0) {
			condEndTime.setHours(sunriseH);
			condEndTime.setMinutes(sunriseM);
			condEndTime.setSeconds(0);
			//Calendar cal = Calendar.getInstance();
			cal.setTime(condEndTime);			
			cal.add(Calendar.HOUR, 24);
			condEndTime = cal.getTime();
		} else if (tinc == 3) {
			condEndTime.setHours(sunsetH);
			condEndTime.setMinutes(sunsetM);
			condEndTime.setSeconds(0);
			//Calendar cal = Calendar.getInstance();
			cal.setTime(condEndTime);			
			cal.add(Calendar.HOUR, 24);
			condEndTime = cal.getTime();
		} else if (tinc == 1) {
			condEndTime.setHours(sunriseH);
			condEndTime.setMinutes(sunriseM);
			condEndTime.setSeconds(0);			
			//Calendar cal = Calendar.getInstance();
			cal.setTime(condEndTime);	
			cal.add(Calendar.HOUR, 24);
			cal.add(Calendar.MINUTE, hm*-1);			
			condEndTime = cal.getTime();
		} else if (tinc == 2) {
			condEndTime.setHours(sunriseH);
			condEndTime.setMinutes(sunriseM);
			condEndTime.setSeconds(0);			
			//Calendar cal = Calendar.getInstance();
			cal.setTime(condEndTime);	
			cal.add(Calendar.HOUR, 24);
			cal.add(Calendar.MINUTE, hm);
			condEndTime = cal.getTime();
		} else if (tinc == 4) {
			condEndTime.setHours(sunsetH);
			condEndTime.setMinutes(sunsetM);
			condEndTime.setSeconds(0);			
			//Calendar cal = Calendar.getInstance();
			cal.setTime(condEndTime);	
			cal.add(Calendar.HOUR, 24);
			cal.add(Calendar.MINUTE, hm*-1);
			condEndTime = cal.getTime();
		} else if (tinc == 5) {
			condEndTime.setHours(sunsetH);
			condEndTime.setMinutes(sunsetM);
			condEndTime.setSeconds(0);			
			//Calendar cal = Calendar.getInstance();
			cal.setTime(condEndTime);	
			cal.add(Calendar.HOUR, 24);
			cal.add(Calendar.MINUTE, hm);
			condEndTime = cal.getTime();
		} else if (tinc == 6) {
			condEndTime.setHours(hm);
			condEndTime.setMinutes(0);
			condEndTime.setSeconds(0);	
			//Calendar cal = Calendar.getInstance();
			cal.setTime(condEndTime);	
			cal.add(Calendar.HOUR, 24);
			condEndTime = cal.getTime();
		}
		
		
		//cal.setTime(now);
		
		int beginMinutes = (condStTime.getHours() * 60) + condStTime.getMinutes();
		int endMinutes = (condEndTime.getHours() * 60) + condEndTime.getMinutes();
		int nowMinutes = (now.getHours() * 60) + now.getMinutes();
		
		//System.out.println(beginMinutes + "  "  + endMinutes + " ->  " + nowMinutes);
		//System.out.println("opp : " + this.opp);
		//System.out.println("prevOpp : " + this.prevOpp);
		
		if ( (nowMinutes <= endMinutes) ||
			 (nowMinutes >= beginMinutes) ) {			
			//on
			this.opp = ConstOperationKnd.RUN;
			if (this.opp != this.prevOpp) {
				this.prevOpp = this.opp;
				//System.out.println("on  " + condStTime + " ->  " + condEndTime);
				run();
			}
		} else {
			//off
			this.opp = ConstOperationKnd.STOP;
			if (this.opp != this.prevOpp) {
				this.prevOpp = this.opp;
				//System.out.println("off  " + condStTime + " ->  " + condEndTime);
				run();
			}
		}
		
		/*
		System.out.println("tknd : " + tknd);
		System.out.println("sval : " + sval);
		System.out.println("tinc : " + tinc);
		System.out.println("hm : " + hm);
		
		System.out.println("now : " + now);
		System.out.println("condStTime : " + condStTime);
		System.out.println("condEndTime : " + condEndTime);
	
		System.out.println("after : " + now.after(condStTime));
		System.out.println("before : " + now.before(condEndTime));
			
		*/	
		
	
		/*
		if (now.after(condStTime) && now.before(condEndTime) ) {
			this.opp = ConstOperationKnd.RUN;
			if (this.opp != this.prevOpp) {
				this.prevOpp = this.opp;
				System.out.println("on  " + condStTime + " ->  " + condEndTime);
				run();
			}
			//
		} else {
			this.opp = ConstOperationKnd.STOP;
			if (this.opp != this.prevOpp) {
				this.prevOpp = this.opp;
				System.out.println("off  " + condStTime + " ->  " + condEndTime);
				run();
			}
		}
		*/
		
		
		
		return false;
	}
	
	@Override
	public void run() {
		for (Entry<Byte, IActOnOff> ent : onofflist.entrySet()) {
			IActOnOff onoff = ent.getValue();	
			try {
				Thread.sleep(3000);
				onoff.setOpp(this.opp);	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
		//return true;
	}
	

	@Override
	public void setActKind(Map<Byte,IActKnd> mapActKind) {
		for (Entry<Byte, IActKnd> ent : mapActKind.entrySet()) {
			IActKnd actKnd = ent.getValue();	
			if (actKnd.getKind().equals(EnumActknd.ACT_KIND.LIGHT)) {
				onofflist = actKnd.getOnOffInfo();
			} 				
		}
	}
	
	
	
	@Override
	public void setComplet(boolean complete) {
		
		
	}

	@Override
	public boolean getSatisfy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setHouseDir(int houseDir) {
		
	}

	
	
	
	
	@Override
	public void stop() {
		this.opp = ConstOperationKnd.STOP;
		this.prevOpp = ConstOperationKnd.STOP;
		
		System.out.println("stop");
		
	}

	
	

	@Override
	public void setPriorityRangeMap(ActGroupPeriodMap priorityMap) {
		this.priorityMap = priorityMap;
		
		
		
	}

	@Override
	public void setWindDir(int windDir) {
		
		
	}

	@Override
	public void setOverWindSpeed(boolean overWindSpeed) {
		//this.overWindSpeed = overWindSpeed;
		
	}

	@Override
	public void setOverRainDrop(boolean overRainDrop) {
		
		//System.out.println("setOverRainDrop : " + this.overRainDrop );
		
	}

	
	@Override
	public boolean isRuning(float sensorValue, float outTemp, float outHum) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setisUseIndoorTemp(boolean isUse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOverIndoorTemp(boolean overIndoorTemp) {
		// TODO Auto-generated method stub
		
	}
	
}
