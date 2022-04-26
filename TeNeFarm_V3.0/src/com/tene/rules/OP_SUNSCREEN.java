package com.tene.rules;

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


public class OP_SUNSCREEN implements IOperationPattern {
	
	private int houseDir = 0;
	private ActGroupPeriodMap priorityMap = null;
	
	//private byte preStep = 10;
	//private float preFitValue = -1;
	
	private boolean isUseWindDir;
	private boolean isUseWindSpeed;
	private boolean isUseRainDrop;
	//private byte winddir = -1;
	private boolean overWindSpeed = false;
	private boolean overRainDrop = false;
	
	//private float preStepFitValue_01 = 0;
	//private float preStepFitValue_02 = 0;
	//private float preStepFitValue_03 = 0;
	
	private IActLayer layer_side = null;
	private IActLayer layer_top = null;
	
	
	/*
	private byte orderfit_left = 0;
	private byte orderfit_right = 0;
	private byte orderfit_back = 0;
	
	
	private byte prev_orderfit_left = 0;
	private byte prev_orderfit_right = 0;
	private byte prev_orderfit_back = 0;
	*/
	
	private byte opp = -1;
	private byte prevOpp = -1;
	
	
	@Override
	public void setisUseWindDir(boolean isUse) {
		this.isUseWindDir = isUse;
	}
	
	@Override
	public void setisUseWindSpeed(boolean isUse) {
		this.isUseWindSpeed = isUse;
	}
	
	@Override
	public void setisUseRainDrop(boolean isUse) {
		this.isUseRainDrop = isUse;
	}
	
	@Override
	public void setPreStep(byte step) {
		//this.preStep = step;
		
	}
	
	
	
	private byte calRate(float sensorValue, float outSideValue) {
		//System.out.println(layer01.getOpenRate());
		//byte curOR =  layer01.getSubLayerInfo().get(side).getOpenRate();
		byte ret = 5;
		
		float gapValue = Math.abs(sensorValue - outSideValue);
		if (gapValue > 10)
			gapValue = 10;
		ret = (byte) (15 - gapValue);
		return  ret;
	}
	
	
	
	private boolean oppWindDir(float sensorValue, int stepvalue01, int fanValue) {
		
		
		boolean ret = true;
		byte opp = -1;
		if (Math.abs(sensorValue - stepvalue01) < 1) {
			opp = ConstOperationKnd.STOP;
			//if (!overRainDrop && !overWindSpeed)
			if (!( (isUseRainDrop && overRainDrop) ||
				 (isUseWindSpeed && overWindSpeed) )) {
				layer_side.stopLayer();
			}
			//if (!overRainDrop )
				
			ret = false;
		} else {
			opp = (byte) ((sensorValue > stepvalue01) ? ConstOperationKnd.OPEN : ConstOperationKnd.CLOSE);
						
			/*
			if (opp == ConstOperationKnd.OPEN) {
				//if (this.winddir == this.houseDir) {
				//	orderfit_left = (byte) (0);
				//	orderfit_right = (byte) (100);
				//} else {
					orderfit_left = (byte) (100);
					orderfit_right = (byte) (100);
					orderfit_back = (byte) (100);
				//}
				//orderfit_left = (byte) (100);
				//orderfit_right = (byte) (100);
			} else {
				orderfit_left = (byte) (0);
				orderfit_right = (byte) (0);
				orderfit_back = (byte) (0);
			}
			*/
		}
		return ret;
	}
	
	private boolean oppNormal(float sensorValue, int stepvalue01, int fanValue) {
		//System.out.println(sensorValue + "  " + stepvalue01 + "  " + fanValue);
		boolean ret = true;
		if ( (isUseRainDrop && overRainDrop) ||
			 (isUseWindSpeed && overWindSpeed) ) {
			opp = ConstOperationKnd.CLOSE;				
			ret = !layer_side.isAllClosed();			
		} else {
			if (Math.abs(sensorValue - stepvalue01) < 0.5) {
				opp = ConstOperationKnd.STOP;			
				ret = false;			
			} else {			 
				opp = (byte) ((sensorValue > stepvalue01) ? ConstOperationKnd.OPEN : ConstOperationKnd.CLOSE);						
				
				if (opp == ConstOperationKnd.OPEN) {
					ret = !layer_side.isAllOpend();
					ret = ret || !layer_top.isAllOpend();
				} else if (opp == ConstOperationKnd.CLOSE) {
					ret = !layer_side.isAllClosed();
					ret = ret || !layer_top.isAllClosed();
				}		
			}
		}
		return ret;
	}
	
	@Override
	public boolean isRuning(float sensorValue, float outTemp) {				
		int stepvalue01 = priorityMap.getStepValue((byte)1);
		int fanValue = priorityMap.getFanValue((byte)1);		
		return oppNormal(sensorValue,stepvalue01,fanValue);
	}
	
	@Override
	public void run() {
		if (this.prevOpp != this.opp) {
			if (opp == ConstOperationKnd.OPEN) {			
				layer_top.allOpen();	
				if (layer_top.isAllOpend())
					layer_side.allOpen();	
			} else if (opp == ConstOperationKnd.CLOSE) {
				layer_side.allClose();	
				if (layer_side.isAllClosed())
					layer_top.allClose();	
				
			} else if (opp == ConstOperationKnd.STOP) {			
				layer_side.stopLayer();	
				layer_top.stopLayer();	
			}
			this.prevOpp = this.opp;
		}
	}
	
	@Override
	public void stop() {
		if ((isUseRainDrop && overRainDrop) ||
			 (isUseWindSpeed && overWindSpeed) ) {
			opp = ConstOperationKnd.CLOSE;
		} else {
			opp = ConstOperationKnd.STOP;
		}		
		run();		
	}
	
	
	

	@Override
	public void setActKind(Map<Byte,IActKnd> mapActKind) {
		
		//this.mapActKind = mapActKind;
		for (Entry<Byte, IActKnd> ent : mapActKind.entrySet()) {
			IActKnd actKnd = ent.getValue();
			//System.out.println(actKnd.getName());
			//System.out.println(actKnd.getKind());
			if (actKnd.getKind().equals(EnumActknd.ACT_KIND.WARMSIDESCREEN)) {				
				layer_side = actKnd.getLayerInfo().get((ConstOperationKnd.LAYER01));
				
			} else if (actKnd.getKind().equals(EnumActknd.ACT_KIND.WARMTOPSCREEN)) {
				layer_top = actKnd.getLayerInfo().get((ConstOperationKnd.LAYER01));
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
		this.houseDir = houseDir;
		
		//System.out.println("houseDir : " + houseDir);
		//act01 = mapActuator.get(LAYER01);
		//act02 = mapActuator.get(LAYER02);
		//act03 = mapActuator.get(LAYER03);
	}


	@Override
	public void setPriorityRangeMap(ActGroupPeriodMap priorityMap) {
		this.priorityMap = priorityMap;
		
		
		
	}

	@Override
	public void setWindDir(int windDir) {
		/*
		if (windDir >= 0) {
			if ((windDir > 0) && (windDir < 180)  ) {
				this.winddir = ConstOperationKnd.RIGHT;
			} else {
				this.winddir = ConstOperationKnd.LEFT;
			}
		} else {
			this.winddir = -1;
		}
		*/
		
		
	}

	@Override
	public void setOverWindSpeed(boolean overWindSpeed) {
		this.overWindSpeed = overWindSpeed;
		
	}

	@Override
	public void setOverRainDrop(boolean overRainDrop) {
		this.overRainDrop = overRainDrop;
		//System.out.println("setOverRainDrop : " + this.overRainDrop );
		
	}

	
	@Override
	public boolean isRuning(Date now,int sunriseH, int sunriseM, int sunsetH, int sunsetM) {
		// TODO Auto-generated method stub
		return false;
	}

	

	@Override
	public void seti2c(I2CDevice I2CFeather) {
		// TODO Auto-generated method stub
		
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
