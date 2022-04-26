package com.tene.controller.house.actgrp;


import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

import com.pi4j.io.i2c.I2CDevice;
import com.tene.Interfaces.IActGroup;
import com.tene.Interfaces.IActKnd;
import com.tene.Interfaces.IOperationPattern;
import com.tene.Interfaces.ISensor;
import com.tene.Interfaces.ISensorNode;

import com.tene.controller.house.House;
import com.tene.controller.house.actgrp.actknd.ActKnd;
import com.tene.controller.house.actgrp.correction.ActGroupCorectionMap;
import com.tene.controller.house.actgrp.etc.ActGroupEtcMap;
import com.tene.controller.house.actgrp.period.ActGroupPeriodMap;
import com.tene.controller.house.actgrp.period.CurValues;
import com.tene.controller.relay.HC595;

public class ActGroup implements IActGroup {
	private House parent = null;
	private ISensor my_sensor = null;
	private ISensor my_etc_sensor = null;
	private float sensorValue = -100;
	private float outtemp = 0;
	private float outhum = 0;
	private int windDir = -1;
	private float windSpeed = 0;
	
	private byte sid = 0;
	
	private IOperationPattern operationPattern = null;
	private byte id = 0;
	private boolean isAuto = false;	
	
	private String kind = null;
	
	private ActGroupPeriodMap actGroupPeriodMap = null;
	private ActGroupCorectionMap actGroupCorectionMap = new ActGroupCorectionMap();
	private ActGroupEtcMap actGroupEtcMap = new ActGroupEtcMap();
	private Map<Byte,IActKnd> mapActKnd = new HashMap<Byte,IActKnd>();
	
	private boolean isOpp = true;
	
	
	private int oppTimeBasic = 0;
	private int oppTimeInc = 0; //second
	private int waitTime = 20;
	private int waitTimeInc = 0; //second
	//private boolean wait = false;
		
	
	float corVar = 0;;
	//int winddir = -1;
	int house_dir = 0;
	private boolean islocal = false;
	
	
	CurValues refPeriodValues = new CurValues();
	
	
	
	private boolean isAutoStop = false;
	
	
	
	private Timer thread_all = new Timer();
	private TimerTask tt = new TimerTask() {
		@Override
		public void run() {
			//
			if (!islocal) {
				if (isAuto) {
					//System.out.println(kind);
					if (kind.equals("08") ) {
						operationLED();						
					} else if ( (kind.equals("11")) )  {
						operationVent();
					} else if (  (kind.equals("05")) ) {
						operationMoveFan();
					} else {
						outtemp = parent.getOutTemp();		
						sensorValue = my_sensor.getValue();	
						if (sensorValue < outtemp) {
							isOpp = true;
						} else {
							oppTimeBasic = Math.round(sensorValue-outtemp);
							if (oppTimeBasic > 10)
								oppTimeBasic = 10;
							if (isOpp) {
								oppTimeInc++;
								if (oppTimeInc > (waitTime - oppTimeBasic)) {
									oppTimeInc = 0;
									isOpp = false;
								}
							} else {
								waitTimeInc++;
								if (waitTimeInc > (waitTime + oppTimeBasic)) {
									waitTimeInc = 0;
									isOpp = true;
								}
							}
						}
						//System.out.println("oppTimeBasic " + oppTimeBasic);
						//System.out.println(isOpp);
						try  {
							if (isOpp) {
								operationLayer();
								isAutoStop = true;
							} else {
								if (isAutoStop) {
									operationPattern.stop();
									isAutoStop = false;
								}
							}	
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else { // end if auto
					operationPattern.setPreStep((byte)10);
				}
			}
 			
		}
	};
	
	
	private void operationLayer() {
		operationPattern.setisUseWindDir(actGroupEtcMap.isUseWindDir());
		operationPattern.setisUseWindSpeed(actGroupEtcMap.isUseWindSpeed());
		operationPattern.setisUseRainDrop(actGroupEtcMap.isUseRainDrop());
		
		if (actGroupEtcMap.isUseWindDir()) {
			int windDir = this.parent.getWindDir();
			boolean isOverWindDirOutTemp = actGroupEtcMap.isOverWindDirOutTemp(this.parent.getOutTemp());
			if (isOverWindDirOutTemp)
				operationPattern.setWindDir(windDir);
			else 
				operationPattern.setWindDir(-1);
		} else {
			operationPattern.setWindDir(-1);
		}
		
		if (actGroupEtcMap.isUseWindSpeed()) {
			boolean isOverWindSpeed = actGroupEtcMap.isOverWind(this.parent.getWindSpeed());
			operationPattern.setOverWindSpeed(isOverWindSpeed);
		} else {
			operationPattern.setOverWindSpeed(false);
		}
		
		if (actGroupEtcMap.isUseRainDrop()) {
			boolean isOverRainDrop = actGroupEtcMap.isOverRain(this.parent.getRainDrop());
			operationPattern.setOverRainDrop(isOverRainDrop);
		} else {
			operationPattern.setOverRainDrop(false);
		}
		
		
		operationPattern.setPriorityRangeMap(this.actGroupPeriodMap);
		
		if (operationPattern.isRuning(sensorValue,outtemp)) {	
			//System.out.println("outtemp " + outtemp);
			//System.out.println("sensorValue " + sensorValue);
			operationPattern.run();
		}
			
		
	}
	
	private void operationVent() {
		outtemp = parent.getOutTemp();
		outhum = parent.getOutHum();		
		sensorValue = my_sensor.getValue();		
		
		operationPattern.setisUseIndoorTemp(actGroupEtcMap.isUseIndoorTemp());
		
		if (actGroupEtcMap.isUseIndoorTemp()) {
			float etcSensorValue = this.my_etc_sensor.getValue();
			boolean isOverIndoorTemp = actGroupEtcMap.isOverIndoorTemp(etcSensorValue);
			operationPattern.setOverIndoorTemp(isOverIndoorTemp);
		} else {
			operationPattern.setOverIndoorTemp(false);
		}
		
		
		
		operationPattern.setPriorityRangeMap(this.actGroupPeriodMap);		
		
		if (operationPattern.isRuning(sensorValue,outtemp,outhum)) {	
			//System.out.println("operationFan");
			operationPattern.run();
		}
	}
	
	private void operationMoveFan() {
		outtemp = parent.getOutTemp();
		outhum = parent.getOutHum();		
		sensorValue = my_sensor.getValue();		
		
		operationPattern.setPriorityRangeMap(this.actGroupPeriodMap);		
		
		if (operationPattern.isRuning(sensorValue,outtemp,outhum)) {	
			//System.out.println("operationFan");
			operationPattern.run();
		}
	}
	
	private void operationLED() {		
		operationPattern.setPriorityRangeMap(this.actGroupPeriodMap);
		
		//System.out.println("operationLED : " + sid);
		
		if (operationPattern.isRuning(new Date(),
				parent.getSunRiseH(),
				parent.getSunRiseM(),
				parent.getSunSetH(),
				parent.getSunSetM()) ) {			
			//operationPattern.run();
		}
			
		
	}
	
	/*
	private void stopLayer() {
		operationPattern.stop();
		for (Entry<Byte, IActKnd> entActKnd : mapActKnd.entrySet()) {
			IActKnd actKnd = entActKnd.getValue();
			actKnd.stopLayer();
		}
		
		
		
	}
	*/
	
	
	
	private void setRFSensor(String sensorKnd, Map<Byte,ISensorNode> mapSensorNode) {
		for (Entry<Byte, ISensorNode> ent_node : mapSensorNode.entrySet()) {
			ISensorNode sensorNode = ent_node.getValue();
			Map<Byte, ISensor> mapSensor = sensorNode.getMapSensor();
			for (Entry<Byte, ISensor> ent_sensor : mapSensor.entrySet()) {
				ISensor sensor = ent_sensor.getValue();
				
				if (sensor.getKind().equals(sensorKnd)) {
					this.my_sensor = sensor;
				}
				
				if (actGroupEtcMap.isUseIndoorTemp()) {
					if (sensor.getKind().equals("03")) {
						this.my_etc_sensor = sensor;
						//System.out.println(this.my_etc_sensor);
					}
				}
				
				/*
				
				*/
			}			
		}
	}
	
	
	
	public ActGroup(House parent, JSONObject jactGroup, 
			Map<Byte,ISensorNode> mapEFSensorNode, 
			HC595 hc595, I2CDevice I2CFeather) throws Exception {
		
		
		//this.arduSPI = arduSPI;
		
		
		//System.out.println(jactGroup);
		this.parent = parent;
		this.house_dir = parent.getHouse_direction();
		this.id = (byte) jactGroup.getInt("key");	
		
		this.isAuto = jactGroup.getString("ia").equals("Y") ? true : false;
		this.kind = jactGroup.getString("knd");	
		//byte rel_cde = (byte) jactGroup.getInt("rel_cde");
		this.sid = (byte) jactGroup.getInt("sid");	
		
		
		
		String sensorKnd = jactGroup.getString("as_knd");
		
		if (jactGroup.has("act_knd")) {
			JSONArray info_act_knd = jactGroup.getJSONArray("act_knd");		
			for (int ai=0; ai<info_act_knd.length();ai++) {
				JSONObject act_each = (JSONObject) info_act_knd.get(ai);
				byte actkndid = (byte) act_each.getInt("key");	
				ActKnd actknd = null;
				//if (this.kind.equals(sensorKnd))
				actknd = new ActKnd(this, act_each, hc595, I2CFeather, this.isAuto);
				
				//actknd = new ActKnd(this, act_each, hc595,  this.isAuto);
				mapActKnd.put(actkndid, actknd);
			}
		}
		
		if (jactGroup.has("rule_cls")) {
			String rule_cls = jactGroup.getString("rule_cls");
			
			Class cls = Class.forName("com.tene.rules."+ "OP_"+rule_cls);
			Object clsInstance = cls.newInstance();				
			operationPattern = (IOperationPattern) clsInstance;	
			operationPattern.setActKind(mapActKnd);
			operationPattern.setHouseDir(this.parent.getHouse_direction());
			
			operationPattern.seti2c(I2CFeather);
		}
		
		
		//setRFSensor(sensorKnd, mapEFSensorNode);
		
		
		
		if (jactGroup.has("actgroup_perioad")) {
			if (this.sid == 99) {
				actGroupPeriodMap = new ActGroupPeriodMap(jactGroup.getJSONArray("actgroup_perioad"),(byte)0);
			} else {
				actGroupPeriodMap = new ActGroupPeriodMap(jactGroup.getJSONArray("actgroup_perioad"));
			}
			
			
		}
		
		
		if (jactGroup.has("etc")) {
			JSONArray info_etc = jactGroup.getJSONArray("etc");	
			//System.out.println("cor : " + info_cor);
			for (int ei=0; ei<info_etc.length();ei++) {
				JSONObject etc_each = (JSONObject) info_etc.get(ei);
				byte etcId = (byte) etc_each.getInt("mkey");
				byte val = (byte) etc_each.getInt("val");
				String sk = etc_each.getString("sk");
				boolean isuse = etc_each.getString("isuse").equals("Y");
				actGroupEtcMap.put(etcId, val, sk, isuse);
			}
		}
		
		
		setRFSensor(sensorKnd, mapEFSensorNode);
		thread_all.schedule(tt, 10000, 1000);
		
		
		
	}
	
	
	@Override
	public byte getId() {
		return id;
	}
	
	/*
	@Override
	public float getConValue() {
		return actGroupPeriodMap.getCurValue();
	}
	*/
	
	@Override
	public float getCorValue() {
		return actGroupPeriodMap.getCorValue();
	}
	
	@Override
	public void setOppReset() {
		for (Entry<Byte, IActKnd> ent : mapActKnd.entrySet()) {
			IActKnd act = ent.getValue();	
			act.setOppReset();
		}
	}
	
	@Override
	public void setLayerSubStop( byte akid, byte layer, byte key) {
		mapActKnd.get(akid).setLayerSubStop(layer, key);
	}
	
	@Override
	public void setOppFit(byte akid, byte layer, byte key, byte orderfit) {
		if (orderfit < 0 )
			orderfit = 0;
		if (orderfit > 100 )
			orderfit = 100;
		
		mapActKnd.get(akid).setOppFit(layer,key, orderfit);
	}
	
	@Override
	public void setOnOff( byte akid, byte key, byte onoff) {
		mapActKnd.get(akid).setOnOff(key, onoff);
	}
	
	@Override
	public void setAutoManual( byte isAuto) {
		operationPattern.stop();
		this.isAuto = isAuto == (byte)0 ? false : true;
		
		if (this.isAuto) {
			isOpp = true;
			oppTimeInc = 0;
			waitTimeInc = 0;
			
			//actGroupPeriodMap.setCurrentPeriod();
		} 
		
		
		for (Entry<Byte, IActKnd> ent : mapActKnd.entrySet()) {
			IActKnd Act = ent.getValue();
			Act.setAutoManual(this.isAuto);
		}
	}
	
	@Override
	public void updateConditionRule(JSONArray data) {
		//System.out.println("updateConditionRule");
		
		//LED LIGHT
		if (this.sid == 99) {
			actGroupPeriodMap.updateConditionRule(data, (byte)0);
		} else {
			actGroupPeriodMap.updateConditionRule(data);
			oppTimeInc = 0;
		}
				
		
		
		
		/*
		
		*/
		
		//actGroupPeriodMap
	
		//mapPriority.get(priority_ord).setConditionRule(priority_sub_ord, tim_knd,tim_inc,rt_h,rt_m,val_min,val_max);
		//if (this.isAuto)
		//	resetCurRule();
	}
	
	@Override
	public void insertConditionRule(
			byte priority_ord,byte priority_sub_ord) {
		actGroupPeriodMap.insertConditionRule(priority_ord, priority_sub_ord);
		//mapPriority.get(priority_ord).setConditionRuleInsert(priority_ord, priority_sub_ord);
	}
	
	@Override
	public void deleteConditionRule(
			 byte priority_ord,byte priority_sub_ord,
			byte next_sub_ord, int next_val_min) {
		actGroupPeriodMap.deleteConditionRule(priority_sub_ord, next_sub_ord, next_val_min);
		//mapPriority.get(priority_ord).setConditionRuleDelete(priority_sub_ord,next_sub_ord,next_val_min);
	}
	
	@Override
	public void updateCorection(byte corId,byte corVal, int corMin, int corMax, byte isuse) {
		actGroupCorectionMap.update(corId, corVal, corMin, corMax, isuse);		
		corVar = actGroupCorectionMap.getCorVar();
		
	//	System.out.println("corVar : " + corVar);
		
		//corValue = curRange.getPeriodCurValue(timeRate) + corVar;
		
		//actGroupPeriodMap.calculate(corVar);
		//mapCorection.get(corId).setCorVal(corVal);
		//mapCorection.get(corId).setCorMin(cor_min);
		//mapCorection.get(corId).setCorMax(cor_max);
		//mapCorection.get(corId).setUse((isuse==1));
	}
	
	@Override
	public void updateEtc(byte etcId,byte val,byte isuse) {
		actGroupEtcMap.update(etcId, val, isuse);
	}
	
	@Override
	public void updateRoll(byte akid,byte layerid, byte isuse, byte high, byte crate) {
		mapActKnd.get(akid).updateRoll(layerid, isuse, high, crate);
	}
	
	@Override
	public void setLimitValue( byte actkndid, byte actlayerid, byte actlayersubid, int limit_open, int limit_close, int limit_max) {
		mapActKnd.get(actkndid).setLimitValue(actlayerid,actlayersubid, limit_open, limit_close, limit_max);
	}
	
	@Override
	public void setOpenRateReset(byte actkndid, byte actlayerid, byte rate) {
		mapActKnd.get(actkndid).setOpenRateReset(actlayerid,rate);
	}
	
	@Override
	public void setRunStd(byte actkndid, byte onoffid, byte run, byte std, byte it_use) {
		mapActKnd.get(actkndid).setRunStd(onoffid,run,std, it_use);	
	}
	
	@Override
	public void setLED(byte actkndid, byte onoffid, int run) {
		mapActKnd.get(actkndid).setLED(onoffid,run);	
	}
	
	
	@Override
	public void sendToApp(byte cmd, byte akid, byte layerid, byte layersubid, byte[] data, Map<String, Object> updateMap) {
		this.parent.sendToApp(cmd, this.id, akid, layerid,layersubid, data, updateMap);
	}
	
	@Override
	public void sendToArduino(byte cmd, byte akid, byte layerid, byte layersubid, byte opp) {
		//this.parent.sendToArduino(cmd, this.id, akid, layerid,layersubid, opp);
	}
	
	@Override
	public void sendToApp(byte cmd, byte akid, byte onoffid, byte[] data, Map<String, Object> updateMap) {
		this.parent.sendToApp(cmd, this.id, akid, onoffid, data, updateMap);
	}
	
	@Override
	public void sendToApp(byte cmd, byte akid, byte onoffid, byte[] data) {
		this.parent.sendToApp(cmd, this.id, akid, onoffid, data);
	}
	
	@Override
	public byte[] getOpenRate() {
		ByteBuffer buf = ByteBuffer.allocate(100);
		buf.put(this.id);
		byte actCnt = 0;
		for (Entry<Byte, IActKnd> entAct : mapActKnd.entrySet()) {
			IActKnd act = entAct.getValue();
			byte[] data = act.getOpenRate();
			if (data != null) {
				buf.put((byte)data.length);
				buf.put(data);
				actCnt++;
				buf.put(0, (byte) ( (this.id << 4) + actCnt));
			}
		}
		
		byte[] sendData = null;
		int dataSize = buf.position();
		//System.out.println("actgrp : " + dataSize);
		if (dataSize > 1) {
			sendData = new byte[dataSize];
			System.arraycopy(buf.array(), 0, sendData, 0, dataSize);
		}
		
		return sendData;
	}
	
	
	
	
	
	
	
	public void resetCurRule() {
		//control.sendtoserver(CurRulesenddata);
	}
	
	public void setAutoInterval(int oppTime, int waitTime) {
		/*
		for (Entry<Byte, ActGroupPriority> ent : mapPriority.entrySet()) {
			ActGroupPriority actGroupPriority = ent.getValue();	
			actGroupPriority.setAutoInterval(oppTime, waitTime);
		}
		*/
	}
	
	
	
	
	public void setPriorityUse(byte prioID, byte sub_ord, byte isUse) {
		actGroupPeriodMap.setPriorityUse(prioID, sub_ord, isUse==1);
		
	}
	
	

	


	@Override
	public boolean isAuto() {
		return this.isAuto;
	}
	
	

	
	
	@Override
	public Map<Byte,IActKnd> getKindInfo() {
		return this.mapActKnd;
	}
	
}
