package com.tene.rules;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.pi4j.io.i2c.I2CDevice;
import com.tene.Interfaces.IActKnd;
import com.tene.Interfaces.IActLayer;
import com.tene.Interfaces.IActLayerSub;
import com.tene.Interfaces.IOperationPattern;
import com.tene.constant.ConstOperationKnd;
import com.tene.constant.EnumActknd;
import com.tene.controller.house.actgrp.actknd.actlayer.ActLayer;
import com.tene.controller.house.actgrp.period.ActGroupPeriodMap;


public class OP_SIDESCREEN implements IOperationPattern {
	
	private int houseDir = 0;
	private ActGroupPeriodMap priorityMap = null;
	
	//private byte preStep = 10;
	//private float preFitValue = -1;
	
	private boolean isUseWindDir;
	private boolean isUseWindSpeed;
	private boolean isUseRainDrop;
	private byte winddir = -1;
	private boolean overWindSpeed = false;
	private boolean overRainDrop = false;
	
	
	
	private IActLayer layer_side = null;
	private IActLayer layer_top = null;
	
	private IActLayerSub sideWindMatchSubLayer = null;
	private IActLayerSub topWindMatchSubLayer = null;
	
	private IActLayer sideNonMathSubLayers = null;
	private IActLayer topNonMathSubLayers = null;
	
	private float outTemp = 0;
	private float sensorValue = 0;
	private float prevSensorValue = -999;
	private int stepvalue01 = 0;
	
	private byte opp = -1;
	private byte prevOpp = -1;
	
	private byte checkCnt = 0;
	
	
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
		
	private boolean oppWindDir( int fanValue) {
		
		
		boolean ret = true;
		byte opp = -1;
		if (Math.abs(sensorValue - stepvalue01) < 1) {
			opp = ConstOperationKnd.STOP;			
			if (!( (isUseRainDrop && overRainDrop) ||
				 (isUseWindSpeed && overWindSpeed) )) {
				layer_side.stopLayer();
			}				
			ret = false;
		} else {
			opp = (byte) ((sensorValue > stepvalue01) ? ConstOperationKnd.OPEN : ConstOperationKnd.CLOSE);
		}
		return ret;
	}
	
	private boolean oppNormal(  int fanValue) {
		boolean ret = true;
		if ( (isUseRainDrop && overRainDrop) ||
			 (isUseWindSpeed && overWindSpeed) ) {
			opp = ConstOperationKnd.CLOSE;				
			ret = !layer_side.isAllClosed();	
			ret = ret || !layer_top.isAllClosed();
		} else {
			if (Math.abs(sensorValue - stepvalue01) <= 0.5) {
				opp = ConstOperationKnd.STOP;	
				prevSensorValue = -999;
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
		//System.out.println("ret " + ret);
		
		//ret = ret && (Math.abs(this.prevSensorValue - this.sensorValue) > 0);
		return ret;
	}
	
	@Override
	public boolean isRuning(float sensorValue, float outTemp) {	
		this.outTemp = outTemp;
		this.sensorValue = sensorValue;
		//checkCnt++;
		
		
		
		//System.out.println("outtemp " + outTemp);
		//System.out.println("sensorValue " + sensorValue);
		stepvalue01 = priorityMap.getStepValue((byte)1);
		int fanValue = priorityMap.getFanValue((byte)1);		
		return oppNormal(fanValue);
	}
	
	@Override
	public void run() {
		//System.out.println("isUseWindDir " + isUseWindDir);
		if ( (this.isUseWindDir) ) {			
			 //if ( (this.sensorValue - this.outTemp ) > 10) {
			if ( this.outTemp  < stepvalue01) {
				if (this.prevOpp != this.opp) {
					if (opp == ConstOperationKnd.OPEN) {	
						sideNonMathSubLayers.allOpen();						
						if (sideNonMathSubLayers.isAllOpend())
							sideWindMatchSubLayer.setOppNormal(ConstOperationKnd.OPEN);
						
						if ((sideNonMathSubLayers.isAllOpend()) && 
								(sideWindMatchSubLayer.isOpend())) {
							topNonMathSubLayers.allOpen();						
							if (topNonMathSubLayers.isAllOpend())
								topWindMatchSubLayer.setOppNormal(ConstOperationKnd.OPEN);
							
						}
					} else if (opp == ConstOperationKnd.CLOSE) {
						sideWindMatchSubLayer.setOppNormal(ConstOperationKnd.CLOSE);					
						if (sideWindMatchSubLayer.isClosed())
							sideNonMathSubLayers.allClose();	
						
						if ((sideNonMathSubLayers.isAllClosed()) && 
								(sideWindMatchSubLayer.isClosed())) {
							topWindMatchSubLayer.setOppNormal(ConstOperationKnd.CLOSE);					
							if (topWindMatchSubLayer.isClosed())
								topNonMathSubLayers.allClose();	
							
						}
						
					} else if (opp == ConstOperationKnd.STOP) {			
						sideNonMathSubLayers.stopLayer();	
						sideWindMatchSubLayer.setOppNormal(ConstOperationKnd.STOP);
					}
					this.prevOpp = this.opp;					
				}
			}
			
		} else {
			checkCnt++;
			if  (checkCnt == 3) {
				this.prevSensorValue = sensorValue;
				checkCnt = 0;
			}
			//System.out.println("prevSensorValue " + prevSensorValue + "  " + sensorValue);
			//System.out.println("opp " + opp);
			if (this.prevOpp != this.opp) {
				if (opp == ConstOperationKnd.OPEN) {			
					layer_side.allOpen();	
					if (layer_side.isAllOpend())
						layer_top.allOpen();	
				} else if (opp == ConstOperationKnd.CLOSE) {
					layer_top.allClose();	
					if (layer_top.isAllClosed())
						layer_side.allClose();	
					
				} else if (opp == ConstOperationKnd.STOP) {			
					layer_side.stopLayer();	
					layer_top.stopLayer();	
				}
				this.prevOpp = this.opp;
			}
		}
		
	}
	
	@Override
	public void stop() {
		if ((isUseRainDrop && overRainDrop) ||
			 (isUseWindSpeed && overWindSpeed) ) {
			opp = ConstOperationKnd.CLOSE;
		} else {
			opp = ConstOperationKnd.STOP;
			prevSensorValue = -999;
		}		
		run();		
	}
	
	@Override
	public void setActKind(Map<Byte,IActKnd> mapActKind) {
		for (Entry<Byte, IActKnd> ent : mapActKind.entrySet()) {
			IActKnd actKnd = ent.getValue();	
			if (actKnd.getKind().equals(EnumActknd.ACT_KIND.SIDESCREEN)) {				
				layer_side = actKnd.getLayerInfo().get((ConstOperationKnd.LAYER01));
				
			} else if (actKnd.getKind().equals(EnumActknd.ACT_KIND.TOPSCREEN)) {
				layer_top = actKnd.getLayerInfo().get((ConstOperationKnd.LAYER01));
			}		
		}
		
		try {
			sideNonMathSubLayers = new ActLayer(0);
			topNonMathSubLayers = new ActLayer(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	}

	@Override
	public void setPriorityRangeMap(ActGroupPeriodMap priorityMap) {
		this.priorityMap = priorityMap;
		
		
		
	}

	@Override
	/*
	 * 하우스의 오른쪽을 기준으로 .....
	 * 
	 * 
	 */
	public void setWindDir(int windDir) {
		
		if ( (this.isUseWindDir) && ( (this.sensorValue - this.outTemp ) > 10) ) {
			if ( (windDir>=315) || (windDir<45) ) {
				this.winddir = 0;
			} else if ( (windDir>=45) && (windDir<135) ) {
				this.winddir = 1;
			} else if ( (windDir>=135) && (windDir<225) ) {
				this.winddir = 2;
			} else if ( (windDir>=225) && (windDir<315) ) {
				this.winddir = 3;
			}
			
			for (Entry<Byte, IActLayerSub> ent : layer_side.getSubLayerInfo().entrySet()) {
				IActLayerSub usbLayer = ent.getValue();
				if (usbLayer.isMatchSide(this.winddir)) {
					sideWindMatchSubLayer = usbLayer;
				} else {
					sideNonMathSubLayers.getSubLayerInfo().put(usbLayer.getId(), usbLayer);
				}
			}
			
			for (Entry<Byte, IActLayerSub> ent : layer_top.getSubLayerInfo().entrySet()) {
				IActLayerSub usbLayer = ent.getValue();
				if (usbLayer.isMatchSide(this.winddir)) {
					topWindMatchSubLayer = usbLayer;
				} else {
					topNonMathSubLayers.getSubLayerInfo().put(usbLayer.getId(), usbLayer);
				}
			}
		}
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
