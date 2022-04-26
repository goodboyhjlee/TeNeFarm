package com.tene.controller;




import java.io.ByteArrayOutputStream;
//import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
//import com.pi4j.io.spi.SpiChannel;
//import com.pi4j.io.spi.SpiDevice;
//import com.pi4j.io.spi.SpiFactory;
//import com.pi4j.io.spi.SpiMode;
import com.pi4j.system.SystemInfo;
import com.tene.Interfaces.IActGroup;
import com.tene.Interfaces.IActKnd;
import com.tene.Interfaces.IActLayer;
import com.tene.Interfaces.IActLayerSub;
import com.tene.Interfaces.IBroadCast;
import com.tene.Interfaces.ISensor;
import com.tene.Interfaces.ISensorNode;
import com.tene.constant.HeaderDefine;
import com.tene.database.DBOpp;
import com.tene.controller.house.House;
import com.tene.controller.relay.RasGPIO;

import com.tene.exe.Run;

import com.tene.socket.TeneBroadcsstLocal;

import com.tene.utils.GetCurrentTime;
import com.tene.controller.relay.HC595;


public class Control implements Runnable {
	
	private int socketPort = 0;
	
	private double lon = 0;
	private double lat = 0;
	private int sunRise_h = 0;
	private int sunRise_m = 0;	
	private int sunSet_h = 0;
	private int sunSet_m = 0;
	
	public IBroadCast broadClient = null;
	//private TeneBroadcsstServer broadcsstServer = null;
	
	
	private DatagramSocket server = null;
	private Thread       thread = null;
	
	private boolean isuse = false;
	private boolean isws = false;
	private byte con_cde = 0;
	
	private Map<Byte,House> mapHouse = new HashMap<Byte,House>();
	private byte houseCntForInit = 0;
	
	private Map<Byte,List<ISensor>> mapInsertData = new HashMap<Byte,List<ISensor>>();
	private Map<String,Object> mapSendDataUpdate = new HashMap<String,Object>();
	private Map<String,Object> mapSendDataInsert = new HashMap<String,Object>();
	
	//private Map<Byte,IActLayerSub> mapLayerSub = new HashMap<Byte,IActLayerSub>();
	
	private BitSet layerSubIndex = new BitSet(32);
	
	
	private HC595 hc595 = null;
	
	//private ArduSPI arduSPI = null;
	private RasGPIO rasGPIO = null;
	
	private byte sendtimeNth = 0; 
	private byte sendtimeTerm = 5;	
	private byte savetimeTerm = 1;
	
	private boolean isFirstSend = false;
	//public Map<Byte,IDigitalRelay> mapRelay = new HashMap<Byte,IDigitalRelay>();
	
	private I2CBus i2c = null;
	private I2CDevice I2CFeather = null;
	
	private byte udpCmd = -1;
	private SendThread sendThread = new SendThread(); 
	
	private byte randomkey = 0;
	private byte authkey = 7;
	
	
	public boolean innerPanRun = false;
	//private byte innerFan595No = 21;
	
	
	
	private int sendCnt = 1;
	
	
	
	private byte sDataTotalSize = 0;
	//private byte sendIdx = 0;
	//private byte checkTime = 10;
	//private byte checkInc = 0;
	private boolean i2cread = false;
	//private ByteBuffer bufI2C = null;
	
	private Timer thread_Sensor = new Timer();
	private void makeTimer() {
		
	}
	
	private boolean checkRead = true;
	
	private TimerTask tt_Sensor = new TimerTask() {
		@Override
		public void run() {
			try {
				if (i2cread) {	
				//if (false) {
					
					
					/*
					readCount++;
					//System.out.println(readCount);
					if (readCount > 1000) {
						byte[] i2cReqBuffer = new byte[1];
						i2cReqBuffer[0] = (byte)50;
						I2CFeather.write(i2cReqBuffer);	
						Thread.sleep(500);
						readCount = 0;
						
						System.out.println("Reset I2C");
					}
					*/
					
					for (Entry<Byte, House> entHouse : mapHouse.entrySet()) {
						House house = entHouse.getValue();
						
						
							
						if (house.getMapSensorNode().size() > 0) {
							int sensorCnt = 0;
							int readSize  = 0;
							Map<Byte, ISensorNode> mapNode = house.getMapSensorNode();
							
							for (Entry<Byte, ISensorNode> entNode : mapNode.entrySet()) {
								ISensorNode node = entNode.getValue();
								sensorCnt = (node.getMapSensor().size());
								readSize += (((sensorCnt+1) * 3) + 2);
							}
							
							readSize += 2;
														
							byte[] buffer = new byte[readSize];
							
							byte rCount = (byte) Math.ceil(readSize/32.0);
							if (readSize > 32) {
								
								for (byte kk=0; kk < rCount; kk++) {
									byte[] i2cReqBuffer = new byte[4];
									i2cReqBuffer[0] = (byte)2;
									i2cReqBuffer[1] = house.getHouse_cde();
									i2cReqBuffer[2] = (byte)(kk * 32);
									
									
									if ( ((kk+1) * 32)  > readSize) {
										i2cReqBuffer[3] = (byte) (readSize - (kk * 32));
									} else {
										i2cReqBuffer[3] = (byte)32;
									}
									
									Thread.sleep(100);
									I2CFeather.write(i2cReqBuffer);	
									Thread.sleep(100);
									
									byte[] buf_read = null;
									
									//System.out.println(kk + "  " + readSize);
									
									if ( ((kk+1) * 32)  > readSize) {
										//byte curReadSize = (byte) (((kk+1) * 32)  - readSize);
										byte curReadSize = (byte) (readSize - (kk * 32));
										//System.out.println("curReadSize :  " + curReadSize);
											
										buf_read = new byte[curReadSize];
										int ret =  I2CFeather.read(buf_read,0,curReadSize);
									} else {
										buf_read = new byte[32];
										int ret =  I2CFeather.read(buf_read,0,32);
									}	
									
									
									byte buf_len = (byte) buf_read.length;									
									for (byte bk=0; bk<buf_len; bk++) {
										buffer[(kk*32) + bk] = buf_read[bk];	
									}
								}
								
								
							} else {								
								//System.out.println("nid : " + 11111);
								byte[] i2cReqBuffer = new byte[4];
								i2cReqBuffer[0] = (byte)2;
								i2cReqBuffer[1] = house.getHouse_cde();
								i2cReqBuffer[2] = 0;
								i2cReqBuffer[3] = (byte) readSize;
								Thread.sleep(100);
								I2CFeather.write(i2cReqBuffer,0,4);	
								Thread.sleep(100);									
								int ret =  I2CFeather.read(buffer,0,readSize);
							}							
							
							byte pos = 0;
							byte house_cde = buffer[pos++];
							byte nCnt = buffer[pos++];
														
							if (house.getHouse_cde() == house_cde) {
								checkRead = true;
								for (byte nidx=0; nidx<nCnt; nidx++) {	
									byte nid = buffer[pos++];	
									//System.out.println("nid : " + nid);
									byte sCnt = buffer[pos++];
									//sCnt = (byte) (sCnt - 1);
									//System.out.println("sCnt : " + sCnt);
									for (byte sidx=0; sidx<sCnt; sidx++) {	
										byte sid = buffer[pos++];
										//System.out.println("sid : " + sid);
										//if (sid > 0) {
										byte high = buffer[pos++];		
										byte low = buffer[pos++];
										house.setSensorValue(nid, sid, high, low );
									}
								}
							}
							
							Thread.sleep(200);
						} //house node size end if	
						
						//========================================================
						//set ¾ç¾×						
						Map<Byte, ISensorNode> mapSensorNodeInHouse = house.getMapSensorNode();						
						for (Entry<Byte, ISensorNode> entSensorNode : mapSensorNodeInHouse.entrySet()) {
							ISensorNode sensornode = entSensorNode.getValue();
							if (sensornode.getKind().equals("03")) {
								sensornode.setSensorValue((byte)0,(byte)0,(byte)0);
							}
							
						}
						
						
					} // end house for loop
					
					if (!checkRead) {
						System.out.println("checkRead : " + checkRead);
						i2cReset();
					}
					
					
					
					//========================================================
					//get Weather Data
					Thread.sleep(200);
					
					byte[] i2cReqBuffer = new byte[2];
					i2cReqBuffer[0] = (byte)36;
					i2cReqBuffer[1] = (byte)17;
					//i2cReqBuffer[2] = (byte)0;
					//i2cReqBuffer[3] = (byte)16;
					I2CFeather.write(i2cReqBuffer);						
					Thread.sleep(100);
					byte[] buffer = new byte[30];
					int ret =  I2CFeather.read(buffer,0,17);
					Thread.sleep(100);
					//System.out.println("ret : " + ret);
					int pos = 0;
					
					//if (buffer[pos++] == 36) {
						//byte nid = buffer[pos++];
						byte hcc = buffer[pos++];
						byte nID = buffer[pos++];
						//System.out.println("hcc : " + hcc);
						//System.out.println("nID : " + nID);
						for (byte sidx=0; sidx<5; sidx++) {
							byte sid = buffer[pos++];
							byte high = buffer[pos++];		
							byte low = buffer[pos++];
							
							byte[] arr = {0,0,high,low};	
							ByteBuffer wrapped = ByteBuffer.wrap(arr);
							int sValue = wrapped.getInt();
							float value = 0;
							
							//System.out.println("sid : " + sid);
							//System.out.println("sValue : " + sValue);
							
							switch (sid) {	
							case 1 : //temperature								
								int val = (  (high << 8) | (low & 0x00FF) ) ;
								int isMinus = ((high >> 7) == 1) ? -1 : 1;
							    if (isMinus < 0) {
							    	int rev = ~val;
							      val = rev+1;
							    }
							    value = (float) ((val*isMinus) / 10.0);
								broadClient.setTemperature(value);
								
								//System.out.println("T : " + value);
								break;
							case 2 : //humidity
								value = (float) ((sValue) / 10.0);
								broadClient.setHumidity(value);
								//System.out.println("H : " + value);
								break;	
							case 3 : //rain drop
								
								//if ()
								//System.out.println("Rain : " + value);
								broadClient.setRainDrop((byte) sValue);
								break;	
							case 4 : //wind speed
								value = (float) ((sValue) / 10.0);
								//System.out.println("windspeed : " + value);
								broadClient.setWindSpeed(value);
								break;	
							case 5 : //wind dir
								//System.out.println("winddir : " + value);
								broadClient.setWindDir(sValue);
								break;
							}
							
							//gui.setSensrValueWS( nid, sid, value );
						}
					//}
					
					
					
					
				}	
		
			} catch (Exception e) {	
				i2cReset();
				/*
				byte[] i2cReqBuffer = new byte[1];
				i2cReqBuffer[0] = (byte)50;
				try {
					System.out.println("Reset begin");
					Thread.sleep(1000);
					I2CFeather.write(i2cReqBuffer);
					Thread.sleep(1000);
					System.out.println("Reset end");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				*/
				e.printStackTrace();
				//System.out.println("Re Read I2C");				
				//i2cInit();
			}
		}
	};
	
	private Timer thread_all = new Timer();
	private TimerTask tt = new TimerTask() {
		@Override
		public void run() {		
			//if (layerSubIndex.isEmpty()) {
			//	hc595.stop();
			//}
			//if (sendThread.getClientCnt()
			if (sendThread.getClientCnt() > 0) {
				if (!layerSubIndex.isEmpty()) {
					sendOpenRate();
				}
				
				if (sendtimeNth == sendtimeTerm) {
					sendtimeNth = 0;	
					//System.out.println("ssss");
					rDataSend();					
				} else {
					sendtimeNth ++;
				}
			}
			
			
			
			Date now = new Date();
			@SuppressWarnings("deprecation")
			int min = now.getMinutes();
			@SuppressWarnings("deprecation")
			int sec = now.getSeconds();
			
			//System.out.println(min + "  " + sec);
			
			if ( ((min % savetimeTerm) == 0) && (sec == 0) ) {
				mapSendDataInsert.clear();					
				mapSendDataInsert.put("usr_id", Run.usr_id);					
				mapSendDataInsert.put("farm_cde", Run.farm_cde);
				mapSendDataInsert.put("con_cde", con_cde);
				mapSendDataInsert.put("id", "insert_tb_data_measure_v2");
				mapSendDataInsert.put("mea_dat", GetCurrentTime.getCurrentTime());
				
								
				
				for (Entry<Byte, List<ISensor>> ent : mapInsertData.entrySet()) {
					byte house_cde = ent.getKey();
					mapSendDataInsert.put("house_cde", house_cde);
					List<ISensor> listSensor = ent.getValue();
					for (ISensor sensor : listSensor) {
						String fldName = sensor.getFldNam();
						//fldName = sensor.getNid() + "_" + fldName;
						float value = sensor.getValue();
						mapSendDataInsert.put(fldName, value);					
					}
					//System.out.println(mapSendDataInsert);
					DBOpp.insert(mapSendDataInsert);
				}
				
				
				try {						
					float cputemp = SystemInfo.getCpuTemperature();
					//System.out.println("CPU Temperature   :  " + cputemp);
					//if (cputemp > 60) {
					if (cputemp > 40) {
						if (!innerPanRun) {	
							//arduSPI.sendCmd((byte) (3 << 5));
							innerPanRun = true;
						}
						
					} else {
						if (innerPanRun) {
							//arduSPI.sendCmd((byte) (4 << 5));
							innerPanRun = false;
						}
					}
					rasGPIO.runFan(cputemp);					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
	};
	
	
	public void seti2cread(boolean wait) {
		i2cread = wait;
		//System.out.println(i2cread);
	}
	
	public int getSunRiseH() {
		return sunRise_h;
	}
	
	public int getSunRiseM() {
		return sunRise_m;
	}
	
	public int getSunSetH() {
		return sunSet_h;
	}
	
	public int getSunSetM() {
		return sunSet_m;
	}
	
	@SuppressWarnings("deprecation")
	private void getSunriseData() {
		try {
			Calendar[] sunriseSunset = com.tene.forecast.SunriseSunset.getSunriseSunset(Calendar.getInstance(), lat, lon);
			sunRise_h = sunriseSunset[0].getTime().getHours();
    		sunRise_m = sunriseSunset[0].getTime().getMinutes();
    		sunSet_h = sunriseSunset[1].getTime().getHours();
    		sunSet_m = sunriseSunset[1].getTime().getMinutes();
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void i2cReset() {
		//System.out.println("i2cReset");
		//i2cInit();
		try {
			System.out.println("Reset begin");
			I2CFeather = null;
			Thread.sleep(100);
			I2CFeather = i2c.getDevice(0x08);
			Thread.sleep(1000);
			byte[] i2cReqBuffer = new byte[1];
			i2cReqBuffer[0] = (byte)50;
			I2CFeather.write(i2cReqBuffer);	
			Thread.sleep(1000);
			//readCount = 0;
			
			System.out.println("Reset end");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void i2cInit() {
		i2cread = false;		
		//rasGPIO.resetFeather();
		
		//
		/*
		//================= i2c init begin SPI
		ByteArrayOutputStream i2cHouseInfo = new ByteArrayOutputStream();
		i2cHouseInfo.write((byte)1);	
		i2cHouseInfo.write(houseCntForInit);
		for (Entry<Byte, House> entHouse : mapHouse.entrySet()) {
			House house = entHouse.getValue();
			if (house.isIsuse()) {
				//System.out.println("getHouse_cde : " + house.getHouse_cde());
				i2cHouseInfo.write(house.getHouse_cde());
				//node count
				Map<Byte, ISensorNode> mapSensorNodeInHouse = house.getMapSensorNode();
				byte nodeCnt = (byte) mapSensorNodeInHouse.size();
				i2cHouseInfo.write(nodeCnt);
				//System.out.println("nodeCnt : " + nodeCnt);
				
				for (Entry<Byte, ISensorNode> ent : mapSensorNodeInHouse.entrySet()) {
					ISensorNode sensornode = ent.getValue();
					//add 20201222
					//System.out.println("getNid : " + sensornode.getNid());
					i2cHouseInfo.write(sensornode.getNid());
					
					Map<Byte, ISensor> mapSensor = sensornode.getMapSensor();
					
					byte SensorCnt = (byte) mapSensor.size();
					
					//sensor count
					i2cHouseInfo.write((byte)(SensorCnt+1));
					//SensorCnt += mapSensor.size();
				}
				
				
				//i2cHouseInfo.write((byte)(SensorCnt+1));
			}				
		}
		
		
		try {
			byte[] buf = i2cHouseInfo.toByteArray();
			//System.out.println("buf len : " + buf.length);
			
			for (int kk=0; kk<buf.length; kk++) {
				SPI.write(buf[kk]);
			}
			
			
				
			
			
			
			//I2CFeather.write(buf);
			//I2CFeather.write(buf,0,13);
			//I2CFeather.write
		} catch (Exception e) {
			e.printStackTrace();
		}	
		//================= i2c init end
		*/
		
	/*
		//================= i2c init begin  I2C
		ByteArrayOutputStream i2cHouseInfo = new ByteArrayOutputStream();
		i2cHouseInfo.write((byte)1);	
		i2cHouseInfo.write(houseCntForInit);
		for (Entry<Byte, House> entHouse : mapHouse.entrySet()) {
			House house = entHouse.getValue();
			if (house.isIsuse()) {
				System.out.println("getHouse_cde : " + house.getHouse_cde());
				i2cHouseInfo.write(house.getHouse_cde());
				//node count
				Map<Byte, ISensorNode> mapSensorNodeInHouse = house.getMapSensorNode();
				byte nodeCnt = (byte) mapSensorNodeInHouse.size();
				i2cHouseInfo.write(nodeCnt);
				System.out.println("nodeCnt : " + nodeCnt);
				
				for (Entry<Byte, ISensorNode> ent : mapSensorNodeInHouse.entrySet()) {
					ISensorNode sensornode = ent.getValue();
					//add 20201222
					System.out.println("getNid : " + sensornode.getNid());
					i2cHouseInfo.write(sensornode.getNid());
					
					Map<Byte, ISensor> mapSensor = sensornode.getMapSensor();
					
					byte SensorCnt = (byte) mapSensor.size();
					
					System.out.println("SensorCnt : " + SensorCnt);
					
					//sensor count
					i2cHouseInfo.write((byte)(SensorCnt+1));
					//SensorCnt += mapSensor.size();
				}
				
				
				//i2cHouseInfo.write((byte)(SensorCnt+1));
			}				
		}
		
		
		try {
			byte[] buf = i2cHouseInfo.toByteArray();
			System.out.println("buf len : " + buf.length);
			
			
			
			I2CFeather.write(buf);
			//I2CFeather.write(buf,0,13);
			//I2CFeather.write
		} catch (Exception e) {
			e.printStackTrace();
		}	
		//================= i2c init end
		 */
	
		
		
		System.out.println("i2cInit");
		i2cread = true;
		
			
	}
	
	//private  SpiDevice SPI = null;
	
	
	public Control(JSONObject jControl, JSONObject locationInfo, JSONObject sensornode_ws) throws Exception {
		try {
			
			
			
			hc595 = new HC595();		
			rasGPIO = new RasGPIO(this);
			i2c = I2CFactory.getInstance(I2CBus.BUS_1);
			//I2CFeather = i2c.getDevice(0x04);
			I2CFeather = i2c.getDevice(0x08);
			
			this.con_cde = (byte) jControl.getInt("key");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.isuse = jControl.getString("iu").equals("Y") ? true : false;		
		if (this.isuse) {
			this.lon  = locationInfo.getInt("lon");
			this.lat  = locationInfo.getInt("lat");
			
			this.isws = jControl.getString("isws").equals("Y") ? true : false;
			
			
			if (isws) {
				try {	
					
					/*
					if (sensornode_ws.has("sensornode")) {
						JSONArray jSensorNode = sensornode_ws.getJSONArray("sensornode");
						broadcsstServer = new TeneBroadcsstServer(jSensorNode, 
								locationInfo.getString("apikey"),
								locationInfo.getInt("nx"),
								locationInfo.getInt("ny"));
					}
					*/
					
					if (sensornode_ws.has("sensornode")) {
						if (this.con_cde == 2) {
							getSunriseData();
							JSONArray jSensorNode = sensornode_ws.getJSONArray("sensornode");
							broadClient = new TeneBroadcsstLocal(jSensorNode);
						} else {
							getSunriseData();
							broadClient = new TeneBroadcsstLocal();
						}
						
					}
					
				} catch (Exception e) {
					String default_json_path =  "/home/pi/tene/env/ws.json";
					String ddd = Run.readFromFile(default_json_path);					
					JSONObject retObject = new JSONObject(ddd);
					JSONArray info_l = retObject.getJSONArray("data");
					JSONObject sensornode_info = info_l.getJSONObject(0);
					/*
					if (sensornode_info.has("sensornode")) {
						JSONArray jSensorNode = sensornode_info.getJSONArray("sensornode");
						broadcsstServer = new TeneBroadcsstServer(jSensorNode, 
								locationInfo.getString("apikey"),
								locationInfo.getInt("nx"),
								locationInfo.getInt("ny"));
					}
					*/
					
					if (sensornode_ws.has("sensornode")) {
						if (this.con_cde == 2) {
							getSunriseData();
							JSONArray jSensorNode = sensornode_ws.getJSONArray("sensornode");
							broadClient = new TeneBroadcsstLocal(jSensorNode);
						} else {
							getSunriseData();
							broadClient = new TeneBroadcsstLocal();
						}
					}
				}
 			} else {
				//broadClient = new TeneBroadcsstClient();
			}
			
			
			
			
			socketPort = jControl.getInt("sp");
			
			mapSendDataInsert.put("usr_id", Run.usr_id);
			mapSendDataUpdate.put("farm_cde", Run.farm_cde);
			mapSendDataUpdate.put("con_cde", this.con_cde);
			mapSendDataInsert.put("id", "insert_tb_data_measure_v2");

			//================================= HOUSE
			JSONArray jHouse = jControl.getJSONArray("house");
			houseCntForInit = (byte) jHouse.length();	
			for (int i=0; i<houseCntForInit; i++) {
				JSONObject house = jHouse.getJSONObject(i);
				byte house_cde = (byte) house.getInt("key");
				boolean isUse = house.getString("iu").equals("Y") ? true : false;
				
				if (isUse ) {
					mapHouse.put(house_cde, new House(this, house, hc595, I2CFeather));
				}
			}
			
			
			byte index = 0;
			for (Entry<Byte, House> ent : mapHouse.entrySet()) {
				House house = ent.getValue();
				
				Map<Byte, ISensorNode> mapSensorNodeInHouse = house.getMapSensorNode();
				List<ISensor> sensors = new ArrayList<ISensor>();
				for (Entry<Byte, ISensorNode> entSensorNode : mapSensorNodeInHouse.entrySet()) {
					//sDataTotalSize += 2; // nid sensorCnt
					ISensorNode sensornode = entSensorNode.getValue();
					Map<Byte, ISensor> mapSensor = sensornode.getMapSensor();
					for (Entry<Byte, ISensor> sent : mapSensor.entrySet()) {
						ISensor sensor = sent.getValue();
						sensors.add(sensor);
						//if (!sensor.getKind().equals("14")) // stack radiation
						//sDataTotalSize += 3; // value sid
					}
				}
				
				//sDataTotalSize += 3; // battery
				
				
				mapInsertData.put(house.getHouse_cde(), sensors);
				
				Map<Byte, IActGroup>  mapGroup = house.getMapActGroup();
				for (Entry<Byte, IActGroup> entGrp : mapGroup.entrySet()) {
					IActGroup actgrp = entGrp.getValue();
					Map<Byte, IActKnd> mapKnd = actgrp.getKindInfo();
					for (Entry<Byte, IActKnd> entKnd : mapKnd.entrySet()) {
						IActKnd knd = entKnd.getValue();
						Map<Byte, IActLayer> mapLayer = knd.getLayerInfo();
						for (Entry<Byte, IActLayer> entLayer : mapLayer.entrySet()) {
							IActLayer actlayer = entLayer.getValue();
							Map<Byte, IActLayerSub> mapLayerSub = actlayer.getSubLayerInfo();
							for (Entry<Byte, IActLayerSub> entLayerSub : mapLayerSub.entrySet()) {
								IActLayerSub layersub = entLayerSub.getValue();
								layersub.setIndex(index, layerSubIndex);
								//mapLayerSub.put(index, layersub);								
								index++;
							}
						}
						
						
					}
					
				}
			}	
			
			sDataTotalSize += 1; // house cnt
			sDataTotalSize += 30; // weather data
			sendCnt = (sDataTotalSize / 32);				
			int r = (sDataTotalSize % 32);
			
			if (r > 0)
				sendCnt = sendCnt + 1;
			
			
			
			
			try {
				server = new DatagramSocket(socketPort);
				
			    sendThread.setStreamOut(server);
			    start();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
		
			try {
				i2cInit();	
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			
			//Thread.sleep(5000);
			
			
			
			isFirstSend = true;
			
			thread_all.schedule(tt, 5000, 1000 * 1);
			//thread_Sensor.schedule(tt_Sensor, 10000, 1000 * 5);
			thread_Sensor.schedule(tt_Sensor, 1000, 1000);
			
			
			
			
		}
		
	}
	
	
	public void briefingFromDoorSensor() {
		for (Entry<Byte, House> entHouse : mapHouse.entrySet()) {
			House house = entHouse.getValue();
			sendThread.addData(new byte[] { HeaderDefine.RUN_BRIEFING, house.getHouse_cde() });
			
		}
		
	}
	
	
	
	
	@Override
	public void run() {
		byte[] rev_data = new byte[60];
		try {
			while (true) {	
				DatagramPacket dp =new DatagramPacket(rev_data, rev_data.length);				
				server.receive(dp);
				
				//System.out.println("socketPort : " + socketPort);
				//System.out.println("dp.getPort() : " + dp.getPort());
				
				//if ( (dp.getPort() == 9998) || (dp.getPort() == socketPort) ) {
				
				if (true) {
				
				rev_data = dp.getData();
				udpCmd = rev_data[0];
				//System.out.println("udpCmd : " + udpCmd);
				
				if ( (udpCmd == HeaderDefine.C_WEB_CLIENT_CONNECTED) || 
						(udpCmd == HeaderDefine.C_WEB_CLIENT_CONNECTED_COMPLETE) || 
						(udpCmd == HeaderDefine.C_WEB_CLIENT_CHECKCONNTION) || 
						(udpCmd == HeaderDefine.C_WEB_CLIENT_RECONNECTED)) {
					switch (udpCmd) {
						case HeaderDefine.C_WEB_CLIENT_CONNECTED :
							//System.out.println("C_WEB_CLIENT_CONNECTED");
							randomkey = (byte)(Math.random() * 10);					
							Thread.sleep(500);
							//System.out.println("randomkey " + randomkey);
							byte[] sData = new byte[] { HeaderDefine.C_WEB_CLIENT_CONNECTED, randomkey, -1,-25 };
							DatagramPacket dpsend = new DatagramPacket(sData, sData.length, dp.getAddress(), dp.getPort());
							server.send(dpsend);
				   			break;
						case HeaderDefine.C_WEB_CLIENT_CONNECTED_COMPLETE :		
							//System.out.println("C_WEB_CLIENT_CONNECTED_COMPLETE");
							byte returnKey = (byte) (rev_data[1] - authkey);
							//System.out.println("returnKey " + returnKey);
							if (returnKey == randomkey) {
								sendThread.addAddress(dp.getAddress(), dp.getPort(),returnKey);
								sendThread.addData(this.getCurrentValuesAppFirstConnected());
							} else {
								System.out.println("Not Auth : " + dp.getAddress() + ":" + dp.getPort());
																
								sendThread.remotePeer(dp.getAddress(), dp.getPort());
								dp = null;
							}
							
							break;	
							
						case HeaderDefine.C_WEB_CLIENT_CHECKCONNTION :
							//System.out.println("C_WEB_CLIENT_CHECKCONNTION");
							sendThread.addData(new byte[] { HeaderDefine.C_WEB_CLIENT_CHECKCONNTION });
							break;
							
						
							/*
						case HeaderDefine.C_WEB_CLIENT_RECONNECTED :
							System.out.println("C_WEB_CLIENT_RECONNECTED");
							sendThread.addAddress(dp.getAddress(), dp.getPort());
							sendThread.addData(new byte[] { HeaderDefine.C_WEB_CLIENT_RECONNECTED });
							break;
							*/
					}
					
				} else {
					if (sendThread.getKey(dp)) 
					switch (udpCmd) {
					
			   		
					case HeaderDefine.RUN_BRIEFING :
						System.out.println("RUN_BRIEFING");
						//this.setOppFit(rev_data[2], rev_data[3], rev_data[4], rev_data[5], rev_data[6], rev_data[7]);	
						sendThread.addData(new byte[] { HeaderDefine.RUN_BRIEFING, rev_data[1] });
						break;
					case HeaderDefine.C_WEB_CLIENT_CLOSE :
						sendThread.remotePeer(dp.getAddress(), dp.getPort());
						break;						
					case HeaderDefine.CH_LAYER_SUB_STOP :	
			   			this.setLayerSubStop(rev_data[2], rev_data[3], rev_data[4], rev_data[5], rev_data[6]);		   			
			   			break;
			   			
			   		case HeaderDefine.CH_OPPFIT :
			   			this.setOppFit(rev_data[2], rev_data[3], rev_data[4], rev_data[5], rev_data[6], rev_data[7]);		   			
			   			break;
			   			
					case HeaderDefine.CH_ONOFF :		
			   			this.setOnOff(rev_data[2], rev_data[3], rev_data[4], rev_data[5], rev_data[6]);		   			
			   			break;
			   			
			   		case HeaderDefine.CH_AUTOMANUAL :
			   			this.setAutoManual(rev_data[2], rev_data[3], rev_data[4]);			   			
			   			break;
			   			
			   		case HeaderDefine.C_ISLOCAL :
			   			//this.setIsLocal(rev_data[2], rev_data[3]);			   			
			   			break;	
			   			
			   		case HeaderDefine.CH_LIMIT_VALUE :					   
						byte[] byte_buf = new byte[4];
						for (int i=0;  i<4; i++) {
							byte_buf[i] =  rev_data[i + 6];
						}                  	  	
						float limit_value_open = ByteBuffer.wrap(byte_buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
					   
						for (int i=0;  i<4; i++) {
							byte_buf[i] =  rev_data[i + 10];
						}                  	  	
						float limit_value_close = ByteBuffer.wrap(byte_buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
					   
						for (int i=0;  i<4; i++) {
							byte_buf[i] =  rev_data[i + 14];
						}                  	  	
						float limit_value_max = ByteBuffer.wrap(byte_buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
						   
						
						this.setLimitValue( rev_data[1], rev_data[2], rev_data[3], rev_data[4],rev_data[5],
								Math.round(limit_value_open),
								Math.round(limit_value_close),
								Math.round(limit_value_max));
						
						break;
						
			   		case HeaderDefine.CH_OPENRATE_RESET :
						this.setOpenRateReset( rev_data[1], rev_data[2], rev_data[3], rev_data[4],rev_data[5]);
						break;
						
			   		case HeaderDefine.CH_RUNSTD :;
						this.setRunStd( rev_data[1], rev_data[2], rev_data[3], rev_data[4],rev_data[5], rev_data[6], rev_data[7]);
						break;
			   		case HeaderDefine.CH_LED :;
			   			byte[] byte_buf_led = new byte[4];					   
					   for (int i=0;  i<4; i++) {
						   byte_buf_led[i] =  rev_data[i + 5];
					   } 
					   float fled = ByteBuffer.wrap(byte_buf_led).order(ByteOrder.LITTLE_ENDIAN).getFloat();
					   int iled = Math.round(fled);
					   this.setLED( rev_data[1], rev_data[2], rev_data[3], rev_data[4],iled);
					break;
			   		case HeaderDefine.CH_CONDITION_PRIORITY :
			   			/*
			   			dataSize = streamIn.readByte();
						rev_data = new byte[dataSize];
						streamIn.read(rev_data);
						
						//Run.control.setPriorityUse(rev_data[0], rev_data[1], rev_data[2], rev_data[3]);
						
						
						   
						byte priority_ord = rev_data[4];		
						byte priority_priority = rev_data[5];
						byte priority_knd = rev_data[6];
						byte priority_isuse = rev_data[7];
						*/
						   
						//System.out.println(dataSize + "  " + priority_ord + "  " + priority_priority + " " + priority_knd + "  " + priority_isuse);
						//this.controller.setPriorityIsUse(priority_ord, priority_priority, priority_knd, priority_isuse);
						   
						break;
						
			   		case HeaderDefine.CH_RULE_INTERVAL :
			   			/*
			   			rev_data = new byte[10];
						streamIn.read(rev_data);
						
						byte[] byte_buf_interval = new byte[4];
						for (int i=0;  i<4; i++) {
						   byte_buf_interval[i] =  rev_data[i + 2];
						}  
						int oppTime = Math.round(ByteBuffer.wrap(byte_buf_interval).order(ByteOrder.LITTLE_ENDIAN).getFloat());
						for (int i=0;  i<4; i++) {
							byte_buf_interval[i] =  rev_data[i + 6];
						}
						int waitTime = Math.round(ByteBuffer.wrap(byte_buf_interval).order(ByteOrder.LITTLE_ENDIAN).getFloat());
						
						//Run.control.setAutoInterval(rev_data[0], rev_data[1], oppTime, waitTime);
			   			*/
			   			
			   			break;
			   		case HeaderDefine.CH_CONDITION_VALUE :
			   			/*
						   rev_data = new byte[15];
						   streamIn.read(rev_data);
						   byte[] byte_buf_condition = new byte[4];
						   for (int i=0;  i<4; i++) {
							   byte_buf_condition[i] =  rev_data[i + 7];
						   }  
						   float control_v_s = ByteBuffer.wrap(byte_buf_condition).order(ByteOrder.LITTLE_ENDIAN).getFloat();
						   for (int i=0;  i<4; i++) {
							   byte_buf_condition[i] =  rev_data[i + 11];
						   }
						   float control_v_e = ByteBuffer.wrap(byte_buf_condition).order(ByteOrder.LITTLE_ENDIAN).getFloat();					   
						   //Run.control.setConditionValue(
							//	   rev_data[1], rev_data[2],
							//	   rev_data[3], rev_data[4],
							//	   rev_data[5], rev_data[6],
							//	   Math.round(control_v_s),Math.round(control_v_e));
							 
							 */
						   
						   break;
						   
			   		case HeaderDefine.CH_PERIORD :
						   byte datasize = rev_data[1];		
						   System.out.println(datasize + "  " + rev_data.length);
						   byte[] periData = new byte[datasize];
						   System.arraycopy(rev_data, 2, periData, 0, datasize);
						   this.updatePeriod(periData);
						   break;
			   		case HeaderDefine.INS_PERIORD :
						this.insertPeriod(rev_data[1], rev_data[2]);
			   			break;
			   		case HeaderDefine.DEL_PERIORD :
						this.deletePeriod(rev_data[1], rev_data[2], rev_data[3]);
			   			break;
			   		case HeaderDefine.UP_CORECTION :
						byte[] byte_cor = new byte[4];
						for (int i=0;  i<4; i++) {
							byte_cor[i] =  rev_data[i + 5];
						}
						int cor_min = Math.round(ByteBuffer.wrap(byte_cor).order(ByteOrder.LITTLE_ENDIAN).getFloat());
						
						for (int i=0;  i<4; i++) {
							byte_cor[i] =  rev_data[i + 9];
						}
						int cor_max = Math.round(ByteBuffer.wrap(byte_cor).order(ByteOrder.LITTLE_ENDIAN).getFloat());
						
						this.updateCorection(
								   rev_data[1], // house_cde
								   rev_data[2], // agid
								   rev_data[3], // corID 
								   rev_data[4], //corVal							   
								   cor_min, // cor_min
								   cor_max, // cor_max
								   rev_data[13] // isuse
						   );
			   			break;
			   			
			   		case HeaderDefine.UP_ETC :
						byte[] sk_data = new byte[2];
						sk_data[0] = rev_data[6];
						sk_data[1] = rev_data[7];
						String sk = new String(sk_data);
						this.updateEtc(
								   rev_data[1], // house_cde
								   rev_data[2], // agid
								   rev_data[3], // etcID 
								   rev_data[4], //etcVal
								   rev_data[5], // isuse
								   sk
						   );
			   			break;
			   			
			   		case HeaderDefine.UP_ROLL :
						this.updateRoll(
								   rev_data[1], // house_cde
								   rev_data[2], // agid
								   rev_data[3], // akid 
								   rev_data[4], // key layer
								   rev_data[5], // isuse
								   rev_data[6], // high
								   rev_data[7] // crate
						   );
			   			break;

						   
			   		case HeaderDefine.CH_SENSOR_ALERT :
						   byte alert_houseCde= rev_data[1];	
						   byte alert_nid = rev_data[2];					   
						   byte alert_sid = rev_data[3];
						   byte isuse = rev_data[4];				   
						   
						   byte[] byte_buf_avmin = new byte[4];					   
						   for (int i=0;  i<4; i++) {
							   byte_buf_avmin[i] =  rev_data[i + 5];
						   }  
						   
						   float al = ByteBuffer.wrap(byte_buf_avmin).order(ByteOrder.LITTLE_ENDIAN).getFloat();					   
						   byte[] byte_buf_avmax = new byte[4];					   
						   for (int i=0;  i<4; i++) {
							   byte_buf_avmax[i] =  rev_data[i + 9];
						   }  
						   
						   float ah = ByteBuffer.wrap(byte_buf_avmax).order(ByteOrder.LITTLE_ENDIAN).getFloat();					 
						   
						   this.setIsAlert(alert_houseCde, alert_nid, alert_sid, isuse, Math.round(al), Math.round(ah));
						  
						   break;
						   
			   		case HeaderDefine.CH_SENSOR_ALERT_WS :
			   			if (this.con_cde == 2) {
			   				byte alert_nid_ws = rev_data[1];					   
						   byte alert_sid_ws = rev_data[2];
						   byte isuse_ws = rev_data[3];				   
						   
						   byte[] byte_buf_avmin_ws = new byte[4];					   
						   for (int i=0;  i<4; i++) {
							   byte_buf_avmin_ws[i] =  rev_data[i + 4];
						   }  
						   
						   float al_ws = ByteBuffer.wrap(byte_buf_avmin_ws).order(ByteOrder.LITTLE_ENDIAN).getFloat();					   
						   
						   byte[] byte_buf_avmax_ws = new byte[4];					   
						   for (int i=0;  i<4; i++) {
							   byte_buf_avmax_ws[i] =  rev_data[i + 8];
						   }  
						   
						   float ah_ws = ByteBuffer.wrap(byte_buf_avmax_ws).order(ByteOrder.LITTLE_ENDIAN).getFloat();	
						   //System.out.println("CH_SENSOR_ALERT_WS " + alert_nid_ws + "  " +
							//	   alert_sid_ws + " " + isuse_ws + "  " + al_ws + " " + ah_ws);
						   
						   this.setIsAlert_ws( alert_nid_ws, alert_sid_ws, isuse_ws, Math.round(al_ws), Math.round(ah_ws));
			   			}
			   			
						   
						   break;
			   		case HeaderDefine.CHECKALIVE :					   
						   break;
			   		default:   				
			   			//dataS
			   			break;
				   } else {
					   System.out.println("After Not Auth : " + dp.getAddress() + ":" + dp.getPort());
				   }
					
				}
				
				
				}
				
			}
		} catch(Exception e) {
			e.printStackTrace();
			//this.run();
			
		} finally {
		}
		
	}
	
	
	private void start()  { 
	   if (thread == null) {
		   thread = new Thread(this); 
	       thread.start();
	       sendThread.start();
	       //System.out.println("ssssss");
	   }
	}
	
	/*
	@SuppressWarnings("deprecation")
	private void stop()   { 
		if (thread != null)  { 
			thread.stop(); 
		    thread = null;
		}
	}
	*/
	
	
	
	
	private void rDataSend() {
	   this.sendThread.addData(getCurrentValues());
	}
	
	public byte[] getCurrentValuesAppFirstConnected() {
		
		byte[] sendData = null;
		try {
			byte hCnt = (byte) mapHouse.size();
			ByteBuffer buf = ByteBuffer.allocate((hCnt * 60) + 20 + 20 + 20);	
			buf.put(HeaderDefine.R_DATA);			
			buf.put(hCnt);
			//int hCntPos = buf.position()-1;
			
			for (Entry<Byte, House> ent : mapHouse.entrySet()) {
				House house = ent.getValue();
				//buf.put(house.getHouse_cde());
				SensorSendData hData = new SensorSendData();
				house.getCurrentValuesAppFirstConnected(hData); // call by ref
				buf.put(house.getHouse_cde());
				buf.put(hData.getDataSize());
				if (hData.getDataSize() > 0) {
					buf.put(hData.getData());
				} 
				
				
				//buf.put(hData.getDataSize());
				//buf.put(hData.getData());
				hData = null;
			}		
			
			if (this.isws) {
				buf.put((byte) 22);
				byte[] wsData = broadClient.getSensorValue();
				buf.put((byte) (wsData.length/3));
				buf.put(wsData);
				
				//System.out.println(sunRise_h+":"+sunRise_m);
				
				buf.put((byte) 23);
				buf.put((byte) sunRise_h);
				buf.put((byte) sunRise_m);
				buf.put((byte) sunSet_h);
				buf.put((byte) sunSet_m);
				
				
				
			}
			
			int dataSize = buf.position();
			sendData = new byte[dataSize];
			System.arraycopy(buf.array(), 0, sendData, 0, dataSize);
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return sendData;
	}
	
	private byte[] getCurrentValues() {
		//System.out.println("hCntPos : ");
		byte[] sendData = null;
		try {	
			
			byte hCnt = (byte) mapHouse.size();			
			//ByteBuffer buf = ByteBuffer.allocate(hCnt * 30);			
			ByteBuffer buf = ByteBuffer.allocate(hCnt * 100);
			buf.put(HeaderDefine.R_DATA);			
			buf.put(hCnt);
			//int hCntPos = buf.position()-1;
			//System.out.println("hCntPos : " + hCntPos);
			for (Entry<Byte, House> ent : mapHouse.entrySet()) {
				House house = ent.getValue();				
				house.incReceiveCnt();				
				//buf.put(house.getHouse_cde());
				
				SensorSendData hData = new SensorSendData();
				
				//SensorSendData hData = house.getCurrentValues();
				house.getCurrentValues(hData);
				buf.put(house.getHouse_cde());
				buf.put(hData.getDataSize());
				//System.out.println("getDataSize : " + house.getHouse_cde() + "  " + hData.getDataSize());
				//if (hData != null) {
				if (hData.getDataSize() > 0) {					
					buf.put(hData.getData());
				} 
				
				
				
				hData = null;
			}
			
				
			//System.out.println("this.isws : " + this.isws);
			if (this.isws) {
				buf.put((byte) 22);
				byte[] wsData = broadClient.getSensorValue();
				buf.put((byte) (wsData.length/3));
				buf.put(wsData);
				
			}
			
			
			int dataSize = buf.position();
			//System.out.println("getDataSize : " + dataSize);
			sendData = new byte[dataSize];
			System.arraycopy(buf.array(), 0, sendData, 0, dataSize);
			
			buf = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return sendData;
	}
	
	
	public void setLayerSubStop(byte house_cde,  byte agid, byte akid, byte layer, byte key) {		
		if (mapHouse.get(house_cde) != null) {
			mapHouse.get(house_cde).setLayerSubStop(agid, akid, layer, key);
		}
	}
	
	
	public void setOppFit( byte house_cde, byte agid, byte akid, byte layer, byte key, byte orderfit) {
		mapHouse.get(house_cde).setOppFit(agid, akid, layer, key, orderfit);		
		put("id","update_orderfit");
		put("house_cde",house_cde);
		put("agid",agid);
		put("akid",akid);
		put("layer",layer);
		put("key",key);
		put("orderfit",orderfit);
		update();
	}
	
	public void setOnOff( byte house_cde, byte agis, byte akid, byte key, byte onoff) {
		mapHouse.get(house_cde).setOnOff(agis, akid,  key, onoff);
	}
	
	public void setAutoManual(byte house_cde,byte agid, byte isAuto) {
		mapHouse.get(house_cde).setAutoManual(agid,  isAuto);
		sendToSocket(new byte[] { HeaderDefine.CH_AUTOMANUAL, house_cde, agid, isAuto });
		put("id","updateactgrpam");
		put("house_cde",house_cde);
		put("agid",agid);
		put("isauto",(isAuto == 0 ? "N" : "Y"));
		update();
	}
	
	
	
	
	
	public void sendToSocket(byte[] sendData) {
		this.sendThread.addData(sendData);
	}
		
	
	
	private void insert() {
		try {
			DBOpp.insert(mapSendDataInsert);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void put(String key, Object value) {
		mapSendDataUpdate.put(key, value);
	}
	
	private void update() {
		try {
			System.out.println(mapSendDataUpdate);
			DBOpp.update(mapSendDataUpdate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public void setOppReset() {
		for (Entry<Byte, House> entHouse : mapHouse.entrySet()) {
			House house = entHouse.getValue();	
			house.setOppReset();
		}
	}	
	
	
	public void setPriorityUse(byte house_cde,byte agid, byte prioID, byte isUse) {
		mapHouse.get(house_cde).setPriorityUse(agid,  prioID, isUse);
		put("id","updateactgrppriorityisuse");
		put("house_cde",house_cde);
		put("agid",agid);
		put("ord",prioID);
		put("isuse",(isUse == 0 ? "N" : "Y"));
		update();
	}
	
	
	
	public void resetCurRule() {
		for (Entry<Byte, House> entHouse : mapHouse.entrySet()) {
			entHouse.getValue().resetCurRule();
		}
	}
	
	public void setAutoInterval(byte house_cde, byte agid, int oppTime, int waitTime) {
		mapHouse.get(house_cde).setAutoInterval(agid, oppTime, waitTime);
		
		put("id","update_actuatorgrp_interval_value");
		put("house_cde",house_cde);
		put("agid",agid);		
		put("opp_time",oppTime);
		put("wait_time",waitTime);
		update();
		
		
	}
	
	
	
	public void setConditionValue(byte house_cde,byte agid, byte prioID, byte sub_ord, 
			byte control_h_s, byte control_h_e, int control_v_s, int control_v_e) {
		mapHouse.get(house_cde).setConditionValue(agid,prioID,sub_ord,
				control_h_s,control_h_e,control_v_s,control_v_e );
		
		put("id","updateactgrpprioritysub");
		put("house_cde",house_cde);
		put("agid",agid);		
		put("ord",prioID);
		put("sub_ord",sub_ord);
		put("hour_s",control_h_s);
		put("hour_e",control_h_e);
		put("val_min",control_v_s);
		put("val_max",control_v_e);
		update();
		
		
	}
	
	
	public void updatePeriod(byte[] data) {
		try {
			int pCnt = (data.length - 2) / 14;;
			byte house_cde = data[0];
			byte agid = data[1];
			byte[] bPeriods = new byte[data.length - 2];
			System.arraycopy(data, 2, bPeriods, 0, data.length - 2);
			
			int pos = 0;
			byte[] byte_float = new byte[4];
			JSONArray info_l = new JSONArray();
			for (int p=0; p<pCnt; p++) {	
				byte key = bPeriods[pos++];
				byte tknd = bPeriods[pos++];
				
				for (int i=0; i<4; i++) {
					   byte_float[i] =  bPeriods[pos++]; 
				}
				int tinc = Math.round(ByteBuffer.wrap(byte_float).order(ByteOrder.LITTLE_ENDIAN).getFloat());
				
				for (int i=0; i<4; i++) {
					   byte_float[i] =  bPeriods[pos++];
				}
				int hm = Math.round(ByteBuffer.wrap(byte_float).order(ByteOrder.LITTLE_ENDIAN).getFloat());
				
				for (int i=0; i<4; i++) {
					   byte_float[i] =  bPeriods[pos++];
				}
				int sval =Math.round( ByteBuffer.wrap(byte_float).order(ByteOrder.LITTLE_ENDIAN).getFloat());
				
				
				
				JSONObject retObject = new JSONObject();
				
				//byte step = (byte) begin_period.getInt("mkey");
				//int begin_hm = begin_period.getInt("hm");
				//int begin_val = begin_period.getInt("sval");
				
				retObject.put("mkey", key);
				retObject.put("hm", hm);
				retObject.put("sval", sval);
				retObject.put("tinc", tinc);
				retObject.put("tknd", tknd);
				info_l.put(retObject);
				
				
				put("id","updateactgrpperiod");
				put("house_cde",house_cde);
				put("agid",agid);		
				put("key",key);
				put("tknd",tknd);
				put("tinc",tinc);
				put("hm",hm);
				put("sval",sval);
				
				update();
			}
			
			mapHouse.get(house_cde).updateConditionRule(agid,info_l);	
			
			/*
			JSONObject retObject = DBOpp.readFromUrl("getdatalist?id=actgroup_perioad&" + "farm_cde=" + 
			Run.farm_cde + "&con_cde=" + con_cde + "&house_cde=" + house_cde + "&agid=" + agid);
			JSONArray info_l = retObject.getJSONArray("data_list");
			mapHouse.get(house_cde).updateConditionRule(agid,info_l);	
			*/	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateEtc(byte house_cde,byte agid, byte etcId,byte val,byte isuse, String sk) {
		mapHouse.get(house_cde).updateEtc(agid,etcId,val,isuse,sk);
		put("id","updateetc");
		put("house_cde",house_cde);	
		put("agid",agid);		
		put("etcid",etcId);
		put("etc_val",val);
		put("isuse",(isuse == 1 ? "Y" : "N"));
		update();
	}
	
	public void updateRoll(byte house_cde,byte agid, byte akid,byte layerid, byte isuse, byte high, byte crate) {
		
		//System.out.println(house_cde + "  " + agid + "  " + etcId + "  " + val + "  " + isuse + "  " + sk);
		
			mapHouse.get(house_cde).updateRoll(agid,akid,layerid,isuse,high,crate);
			
			put("id","updateroll");
			put("house_cde",house_cde);	
			put("agid",agid);		
			put("akid",akid);
			put("key",layerid);
			put("risuse",(isuse == 1 ? "Y" : "N"));
			put("high",high);
			put("crate",crate);
			update();
			
		
	}
	
	public void insertPeriod(byte house_cde,byte agid) {
		
		//mapHouse.get(house_cde).insertConditionRule(agid,priority_ord,priority_sub_ord );
		int maxkey = 0;
		
		try {
			JSONObject retObject = DBOpp.readFromUrl("getdatalist?" + 
					"id=periodmaxkey&" + 
					"farm_cde=" + Run.farm_cde + "&" + 
					"con_cde=" + Run.con_cde + "&" + 
					"house_cde=" + house_cde + "&" +
					"agid=" + agid);
			
			JSONArray info_l = retObject.getJSONArray("data_list");
			//System.out.println(info_l.get(0));;
			
			JSONObject obj = (JSONObject) info_l.get(0);
			maxkey = obj.getInt("key");
			
			put("id","insertactgrpperiod");
			put("house_cde",house_cde);
			put("agid",agid);
			put("key",maxkey);
			mapSendDataInsert.put("id", "insertactgrpperiod");
			mapSendDataInsert.put("house_cde", house_cde);
			mapSendDataInsert.put("agid", agid);
			mapSendDataInsert.put("key", maxkey);
			
			insert();
			
			byte[] sendData = new byte[4];
			sendData[0] = HeaderDefine.INS_PERIORD;
			sendData[1] = house_cde;
			sendData[2] = agid;
			sendData[3] = (byte) maxkey;
			
			this.sendToSocket(sendData);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//DBOpp.readFromUrl(urlString)
		
		
	}
	
	public void deletePeriod(byte house_cde,byte agid, byte key) {
		try {
			//mapHouse.get(house_cde).deleteConditionRule(agid,priority_ord,priority_sub_ord,next_sub_ord,next_val_min );
			
			put("id","delete_actgrpperiod");
			put("house_cde",house_cde);
			put("agid",agid);		
			put("key",key);
			update();
			
			byte[] sendData = new byte[4];
			sendData[0] = HeaderDefine.DEL_PERIORD;
			sendData[1] = house_cde;
			sendData[2] = agid;
			sendData[3] = (byte) key;
			
			this.sendToSocket(sendData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	public void updateCorection(byte house_cde,byte agid, byte corId,byte corVal,
			int cor_min,int cor_max, byte isuse) {
	
			mapHouse.get(house_cde).updateCorection(agid,corId,corVal,cor_min,cor_max,isuse);
			
			put("id","updatecorection");
			put("house_cde",house_cde);	
			put("agid",agid);		
			put("corid",corId);
			put("cor_val",corVal);
			
			put("cor_min",cor_min);
			put("cor_max",cor_max);
			put("isuse",(isuse == 1 ? "Y" : "N"));
			
			//System.out.println(mapSendDataUpdate);
			update();
			
		
	}
	
	
	
	
	
	public void setLimitValue(byte house_cde,byte agid, byte actkndid, byte actlayerid, byte actlayersubid,  
			int limit_open, int limit_close, int limit_max) {
		mapHouse.get(house_cde).setLimitValue(agid,actkndid,actlayerid,actlayersubid, limit_open,limit_close,limit_max);	
		put("id","updateactsubopplimit");
		put("house_cde",house_cde);	
		put("agid",agid);		
		put("akid",actkndid);
		put("layer",actlayerid);
		put("mkey",actlayersubid);
		
		put("limit_open",limit_open);
		put("limit_close",limit_close);
		put("limit_max",limit_max);
		update();
	}
	
	public void setOpenRateReset(byte house_cde,byte agid, byte actkndid, byte actlayerid, byte rate) {
		mapHouse.get(house_cde).setOpenRateReset(agid,actkndid,actlayerid,rate);	
		
		put("id","updateactsubopenratereset");
		put("house_cde",house_cde);	
		put("agid",agid);		
		put("akid",actkndid);
		put("layer",actlayerid);
		put("rate",rate);
		update();
		
		
	}
	
	public void setRunStd(byte house_cde,byte agid, byte actkndid, byte onoffid, byte run, byte std, byte it_use) {
		mapHouse.get(house_cde).setRunStd(agid,actkndid,onoffid,run,std, it_use);	
		
		put("id","updateactonoffrunstd");
		put("house_cde",house_cde);	
		put("agid",agid);		
		put("akid",actkndid);
		put("key",onoffid);
		put("run",run);
		put("std",std);
		put("it_use",it_use==1?"Y":"N");
		update();
	}
	
	public void setLED(byte house_cde,byte agid, byte actkndid, byte onoffid, int run) {
		mapHouse.get(house_cde).setLED(agid,actkndid,onoffid,run);	
		
		put("id","updateactonoffrunstd");
		put("house_cde",house_cde);	
		put("agid",agid);		
		put("akid",actkndid);
		put("key",onoffid);
		put("run",run);
		put("std",1);
		put("it_use","N");
		update();
	}
	
	public void setIsAlert(byte house_cde, byte nid, byte sid, byte isalert, int avmin, int avmax) {
		//System.out.println("house_cde : " + avmax);
		mapHouse.get(house_cde).setIsAlert(con_cde, nid,sid,isalert,avmin,avmax);
	}
	
	public void setIsAlert_ws(byte nid,  byte sid, byte isalert, int avmin, int avmax) {
		//System.out.println("nid : " + nid);
		broadClient.setIsAlert(Run.farm_cde, nid, sid, isalert, avmin, avmax);
	}
	
	/*
	public void setAlertValue(byte house_cde, byte nid, byte sid, int avmin, int avmax) {
		mapHouse.get(house_cde).setAlertValue(con_cde, nid, sid, avmin, avmax);
	}
	*/
	

	

	public byte getUid() {
		return con_cde;
	}
	
	
	private void sendOpenRate() {
		ByteBuffer buf = ByteBuffer.allocate(100);
		buf.put(HeaderDefine.CH_OPENRATE);
		buf.put((byte) 0); // house cnt
		for (Entry<Byte, House> entHouse : mapHouse.entrySet()) {
			House house = entHouse.getValue();
			byte[] dataHouse = house.getOpenRate();
			if (dataHouse != null) {
				buf.put((byte) dataHouse.length); // house data size
				buf.put(dataHouse);
				buf.put(1,(byte) (buf.get(1)+1));
			}
		}
		
		byte[] sendData = null;
		int dataSize = buf.position();
		
		sendData = new byte[dataSize];
		System.arraycopy(buf.array(), 0, sendData, 0, dataSize);
		this.sendToSocket(sendData);
			
		
	}
	
	public void sendToApp(byte cmd, byte house_cde, boolean isLocal) {
		ByteBuffer buf = ByteBuffer.allocate(5);	
		buf.put(cmd);		
		buf.put(house_cde);
		buf.put((byte) ((isLocal ? 1 : 0) ));
		//buf.put(  (byte) ((house_cde << 4) + ((isLocal ? 1 : 0) )) );
		int dataSize = buf.position();
		byte[] sendData = new byte[dataSize];
		System.arraycopy(buf.array(), 0, sendData, 0, dataSize);		
		this.sendToSocket(sendData);
		
		
		Map<String, Object> updateMap = new HashMap<String, Object>();
		updateMap.put("url", "/process/update_islocal");			
		updateMap.put("farm_cde", Run.farm_cde);
		updateMap.put("con_cde", this.con_cde);
		updateMap.put("house_cde", house_cde);
		updateMap.put("islocal", (isLocal ? "Y" : "N"));
		DBOpp.update(updateMap);
		
	}

	public void sendToApp(byte cmd, byte house_cde, byte agid, byte akid, byte layerid, byte layersubid, byte[] data, Map<String, Object> updateMap) {
		ByteBuffer buf = ByteBuffer.allocate(50);	
		buf.put(cmd);		
		buf.put(  (byte) ((house_cde << 4) + (agid )) );
		buf.put(  (byte) ((akid << 4) + (layerid )) );
		buf.put(  layersubid);
		buf.put(data);		
		int dataSize = buf.position();
		byte[] sendData = new byte[dataSize];
		System.arraycopy(buf.array(), 0, sendData, 0, dataSize);		
		this.sendToSocket(sendData);
		updateMap.put("farm_cde", Run.farm_cde);
		updateMap.put("con_cde", this.con_cde);
		updateMap.put("house_cde", house_cde);
		updateMap.put("agid", agid);
		updateMap.put("akid", akid);
		updateMap.put("layerid", layerid);
		updateMap.put("key", layersubid);
		DBOpp.updateEach(updateMap);
	}
	
	public void sendToApp(byte cmd, byte house_cde, byte agid, byte akid, byte onoffid,  byte[] data, Map<String, Object> updateMap) {
		ByteBuffer buf = ByteBuffer.allocate(50);	
		buf.put(cmd);
		buf.put(  (byte) ((house_cde << 4) + (agid )) );
		buf.put(  (byte) ((akid << 4) + (onoffid )) );
		buf.put(data);
		int dataSize = buf.position();
		byte[] sendData = new byte[dataSize];
		System.arraycopy(buf.array(), 0, sendData, 0, dataSize);		
		this.sendToSocket(sendData);		
		updateMap.put("farm_cde", Run.farm_cde);
		updateMap.put("con_cde", this.con_cde);
		updateMap.put("house_cde", house_cde);
		updateMap.put("agid", agid);
		updateMap.put("akid", akid);
		updateMap.put("key", onoffid);
		DBOpp.updateEach(updateMap);
	}
	
	public void sendToApp(byte cmd, byte house_cde, byte agid, byte akid, byte onoffid,  byte[] data) {
		ByteBuffer buf = ByteBuffer.allocate(50);	
		buf.put(cmd);		
		buf.put(  (byte) ((house_cde << 4) + (agid )) );
		buf.put(  (byte) ((akid << 4) + (onoffid )) );
		buf.put(data);		
		int dataSize = buf.position();
		byte[] sendData = new byte[dataSize];
		System.arraycopy(buf.array(), 0, sendData, 0, dataSize);		
		this.sendToSocket(sendData);
	}
	
	
	
	
	public float getOutTemp() {
		return broadClient.getTemperature();
	}
	
	public float getOutHum() {
		return broadClient.getHumidity();
	}
	
	public float getWindSpeed() {
		return broadClient.getWindSpeed();
	}
	
	public byte getRainDrop() {
		//System.out.println("control : " + broadClient.getRainDrop());
		return broadClient.getRainDrop();
	}
	
	public int getWindDir() {
		return broadClient.getWindDir();
	}
	
	
	
	
}
