package com.tene.controller.house.actgrp.actonoff;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import com.tene.constant.ConstOperationKnd;
import com.tene.constant.EnumActknd;
import com.tene.constant.HeaderDefine;
import com.tene.controller.relay.HC595;
import com.tene.utils.ByteUtils;
import com.pi4j.io.i2c.I2CDevice;
import com.tene.Interfaces.IActKnd;
import com.tene.Interfaces.IActOnOff;


public class ActOnOff implements IActOnOff {
	private IActKnd parent = null;	
	private byte intervalKnd = 1;
	private byte id = 0;
	private boolean isAuto = false;
	//private BitSet ref = null;
	//private byte index = 0;
	
	private EnumActknd.ACT_KIND kind = EnumActknd.ACT_KIND.VENT_PAN;
	
	private int wave = 0;
	
	private byte ir = 0;
	private byte is = 0;
	private boolean it_use = false;
	
	private int inc_run = 0;
	private int inc_std = 0;
	private boolean isOpp = false;
	private boolean isAutoStandby = false;
	private byte autoOpp = ConstOperationKnd.STOP;
	
	private byte opp = -1;
	
	private I2CDevice I2CFeather = null;
		
	private byte hc595Open = 0;
	
	private HC595 hc595 = null;
	
	@Override
	public void setAutoOpp(byte opp) {
		autoOpp = opp;
		if (!(this.it_use)) {
			setOpp(opp);
		}
		//
		if (opp == ConstOperationKnd.STOP) {
			setOpp(opp);
			inc_std = 0;
			inc_run = 0;
			isOpp = true;
		}
	}
	
	private TimerTask makeTimer() {
		return new TimerTask() {
			@Override
			public void run() {
				if (kind != EnumActknd.ACT_KIND.LIGHT ) {
					if (isAuto) {
						//System.out.println(it_use + "  " + autoOpp);
						//if (it_use) {
						//	if (autoOpp == ConstOperationKnd.RUN)  {
								
						//	}
						//}
						if ((it_use) && (autoOpp == ConstOperationKnd.RUN) ) {
							isAutoStandby = true;				
							if (isOpp) {
								if (inc_run == 0) {
									//System.out.println("run : " + isOpp);
									setOpp(ConstOperationKnd.RUN);
								}
								inc_run++;
								if (inc_run >= ir) {
									inc_std = 0;
									inc_run = 0;
									isOpp = false;									
								} 	
								
								
							} else {	
								if (inc_std == 0) {
									//System.out.println("stop : " + isOpp);
									setOpp(ConstOperationKnd.STOP);
								}
								inc_std++;
								if (inc_std >= is) {
									inc_std = 0;
									inc_run = 0;
									isOpp = true;									
								} 
							}	
						//} else if ((autoOpp == ConstOperationKnd.RUN) ) {
							//setOpp(ConstOperationKnd.RUN);
						//} else if ((autoOpp != 0) ) {
						//	setOpp(ConstOperationKnd.STOP);
						} else {
							isAutoStandby = false;
						}				
						
					}
				}
			}
				
		};
	}
	
	
	private Timer thread_RunStd = new Timer();
	private TimerTask tt_RunStd = makeTimer();
	
	
	
	
	public ActOnOff(IActKnd parent, JSONObject jobject, HC595 hc595, EnumActknd.ACT_KIND kind,  I2CDevice I2CFeather, boolean isAuto) throws Exception {
		
		this.parent = parent;
		this.hc595 = hc595;	
		this.kind = kind;
		
		this.I2CFeather = I2CFeather;
		
		//System.out.println("ActOnOff : " + kind);
		this.id = (byte) jobject.getInt("key");
		
		this.isAuto = isAuto;
		this.hc595Open =  (byte)jobject.getInt("rno");
		this.opp = (byte) jobject.getInt("oppstate");
		
		this.ir =  (byte)jobject.getInt("ir");
		this.wave =  jobject.getInt("ir");
		this.is =  (byte)jobject.getInt("is");
		this.it_use = jobject.getString("it_use").equals("Y") ? true : false;
		//this.preOpp = this.opp;
		
		if (this.kind == EnumActknd.ACT_KIND.WATER ) {
			this.intervalKnd = 1;
		} else {
			this.intervalKnd = 60;
		}
		
		
		if (this.it_use) {
			this.isAutoStandby = true;
			//thread_RunStd.schedule(tt_RunStd, 1000*10, 1000*intervalKnd);
		} else {
			this.isAutoStandby = false;
		}
		thread_RunStd.schedule(tt_RunStd, 1000, 1000*intervalKnd);
			
	}

	@Override
	public byte getId() {
		return this.id;
	}

	@Override
	public void setOppReset() {
		
	}
	
	@Override
	public void setRunStd( byte run, byte std, byte it_use) {
		this.ir = run;
		this.is = std;
		this.it_use = it_use == 1 ? true : false;
		
		if (this.it_use) {	
			this.isAutoStandby = true;
			if (thread_RunStd != null) {
				inc_run = 0;
				inc_std = 0;
				thread_RunStd.cancel();
				thread_RunStd = null;
				tt_RunStd.cancel();
				tt_RunStd = null;
				
				thread_RunStd = new Timer();
				tt_RunStd = makeTimer();
				thread_RunStd.schedule(tt_RunStd, 1000*20, 1000*intervalKnd);
			} else {
				thread_RunStd = new Timer();
				tt_RunStd = makeTimer();
				thread_RunStd.schedule(tt_RunStd, 1000*20, 1000*intervalKnd);
			}
		} else {
			this.isAutoStandby = false;
			if (thread_RunStd != null) {
				thread_RunStd.cancel();
				thread_RunStd = null;
				tt_RunStd.cancel();
				tt_RunStd = null;
			}
		}
	}
	
	@Override
	public void setLED( int run) {
		this.wave = run;		
		
		if (this.opp == ConstOperationKnd.RUN) {
			byte[] i2cReqBuffer = new byte[4];		
			i2cReqBuffer[0] = (byte)3;
			i2cReqBuffer[1] = this.id;
			byte[] ww = ByteUtils.intToByteArray16(wave);
			i2cReqBuffer[2] = ww[0];
			i2cReqBuffer[3] = ww[1];
			
			try {
				I2CFeather.write(i2cReqBuffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public boolean isAutoStandby() {
		return this.isAutoStandby;
	}

	@Override
	public void setOpp( byte opp) {
		
		if (opp != this.opp) {
			//System.out.println("setOpp : " + opp  + "  " + this.opp + "  " + hc595Open);
			this.opp = opp;
			//this.preOpp = opp;
			
			this.inc_run = 0;
			this.inc_std = 0;
			
			this.isOpp = (opp == 0) ? true : false;
			//String onoff = (opp == 0) ? "on" : "off";
			//byte cmd = 0 ;
			try {
				if (this.kind == EnumActknd.ACT_KIND.LIGHT ) {
					//System.out.println("setOpp LED : " + opp  + "  " + this.opp + "  " + hc595Open);
					byte[] i2cReqBuffer = new byte[4];
					if (this.opp == ConstOperationKnd.RUN) {
						i2cReqBuffer[0] = (byte)3;
						i2cReqBuffer[1] = this.id;
						byte[] ww = ByteUtils.intToByteArray16(wave);
						i2cReqBuffer[2] = ww[0];
						i2cReqBuffer[3] = ww[1];
						
						
						
					} else if (this.opp == ConstOperationKnd.STOP) {
						i2cReqBuffer[0] = (byte)3;
						i2cReqBuffer[1] = this.id;
						i2cReqBuffer[2] = 0;
						i2cReqBuffer[3] = 0;
						
					}
					
					I2CFeather.write(i2cReqBuffer);
				} else {
					hc595.updateBit_Pans(hc595Open, opp);
				}
				
				
    			byte[] sendData = new byte[1];
    			sendData[0] = this.opp;				
				Map<String, Object> updateMap = new HashMap<String, Object>();				
				updateMap.put("url", "/process/update_oppstate");
				updateMap.put("os", this.opp);
    			this.parent.sendToApp(HeaderDefine.CH_ONOFF, this.id,sendData, updateMap);
				
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void setAutoManual(boolean isAuto) {
		this.isAuto = isAuto;
		setOpp(ConstOperationKnd.STOP);
		if (this.isAuto) {
			if (this.it_use) {		
				this.isAutoStandby = true;
				if (thread_RunStd != null) {
					inc_run = 0;
					inc_std = 0;
					thread_RunStd.cancel();
					thread_RunStd = null;
					tt_RunStd.cancel();
					tt_RunStd = null;
					thread_RunStd = new Timer();
					tt_RunStd = makeTimer();
					thread_RunStd.schedule(tt_RunStd, 1000*20, 1000*intervalKnd);
				} else {
					thread_RunStd = new Timer();
					tt_RunStd = makeTimer();
					thread_RunStd.schedule(tt_RunStd, 1000*20, 1000*intervalKnd);
				}
			} else {
				this.isAutoStandby = false;
				if (thread_RunStd != null) {
					thread_RunStd.cancel();
					thread_RunStd = null;
					tt_RunStd.cancel();
					tt_RunStd = null;
				}
			}
		}
		
	}

	@Override
	public void sendToApp(byte cmd, byte layerid, byte layersubid, byte[] data, Map<String, Object> updateMap) {
		
	}
	
	
	

	@Override
	public byte get595Run() {
		return hc595Open;
	}

	@Override
	public byte getStatus() {
		return this.opp;
	}
	
	/*
	@Override
	public void updateConditionRule(JSONArray periods) {		
		System.out.println(periods);				
		try {
			
			JSONObject colors = periods.getJSONObject(0);
			
			R = colors.getInt("tinc");
			G = colors.getInt("hm");
			B = colors.getInt("sval");
			
			
			byte[] i2cReqBuffer = new byte[4];
			if (this.opp == ConstOperationKnd.RUN) {
				i2cReqBuffer[0] = (byte)3;
				i2cReqBuffer[1] = (byte)R;
				i2cReqBuffer[2] = (byte)G;
				i2cReqBuffer[3] = (byte)B;
			} else if (this.opp == ConstOperationKnd.STOP) {
				i2cReqBuffer[0] = (byte)3;
				i2cReqBuffer[1] = 0;
				i2cReqBuffer[2] = 0;
				i2cReqBuffer[3] = 0;
			}
			
			I2CFeather.write(i2cReqBuffer);
			
			//this.I2CFeather
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	
}
