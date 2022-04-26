package com.tene.controller.house.actgrp.actknd.actlayer.actlayersub;


import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import com.tene.constant.ConstOperationKnd;
import com.tene.constant.HeaderDefine;
import com.tene.controller.relay.HC595;
import com.tene.Interfaces.IActLayer;
import com.tene.Interfaces.IActLayerSub;



public class ActLayerSub implements IActLayerSub {	
	private IActLayer parent = null;
	private HC595 hc595 = null;
	private BitSet ref = null;
	private byte index = 0;
	private byte id = 0;
	
	private byte opp = -1;
	private byte preOpp = -1;
	
	private int lo = 0;
	private int lc = 0;	
	private int lm = 0;
	
	private byte of = 0;
	private byte or = 0;
	
	private byte side = 0; //  left right
	
	
	private float cur_sv = -1;	
	
	private final int timerInteval = 10;	
	
	private boolean isAutoFit = false;  // from scroll value
	
	
	private byte hc595Open = 0;
	private byte hc595Close = 0;
	
	private byte destMoreTime = 20;
	private byte curMoreTime = 0;
	
	private Timer thread_more = new Timer();
	private TimerTask tt_more = new TimerTask() {
		@Override
		public void run() {
			if (opp == ConstOperationKnd.STOP) {
				if ( (destMoreTime > curMoreTime)  ) {					
					byte openrate = getOpenRate();
					if ( (openrate == 0) || (openrate == 100) ) {
						curMoreTime++;
						if (curMoreTime == 1) {
							if ( (openrate == 100) ) {							
								//System.out.println("OPEN : " + curMoreTime);
								hc595.updateBit(hc595Open, ConstOperationKnd.OPEN, (byte) 0);						
							} else if ( (openrate == 0) ) {
								//System.out.println("CLOSE : " + curMoreTime);
								hc595.updateBit(hc595Open, ConstOperationKnd.CLOSE, (byte) 0);						
							}
						} else if  (destMoreTime == curMoreTime) {
							//System.out.println("STOP : " + curMoreTime);
							hc595.updateBit(hc595Open, ConstOperationKnd.STOP, (byte) 0);
						}
					}
				}
				
			}
		}
	};
	
		
	
	private Timer thread_open = new Timer();
	private TimerTask tt_open = new TimerTask() {
		@Override
		public void run() {
			if (opp == ConstOperationKnd.OPEN) {
				float te = cur_sv + 1;
				if (te >= lo)
					te = lo;
				setCur_sv(te);
				
			} 
		}
	};
	
	private Timer thread_close = new Timer();
	private TimerTask tt_close = new TimerTask() {
		@Override
		public void run() {
			if (opp == ConstOperationKnd.CLOSE) {
				float te = cur_sv - 1;
				if (te <= 0)
					te = 0;
				setCur_sv(te);
			} 
		}
	};
	
	
	public ActLayerSub(IActLayer parent, JSONObject jobject, HC595 hc595) throws Exception {		
		this.parent = parent;
		this.hc595 = hc595;
		
		this.id = (byte) jobject.getInt("key");
		
		this.hc595Open =  (byte)jobject.getInt("ro");
		this.hc595Close =  (byte)jobject.getInt("rc");
		
		
		this.opp =  (byte)jobject.getInt("os");
		
		this.of = (byte) jobject.getInt("of");
		this.or = (byte) jobject.getInt("or");
		
		this.lo = jobject.getInt("lo") * (1000 / timerInteval);
		this.lc = jobject.getInt("lc") * (1000 / timerInteval);
		this.lm = jobject.getInt("lm");
		
		this.side = (byte) jobject.getInt("side");
		
		this.cur_sv =  (float) ((or/100.0) * lo);
		
		float rate_open_close = (float) ((lc*1.0) / (lo*1.0));
		thread_open.schedule(tt_open, 1000, timerInteval);
		thread_close.schedule(tt_close, 1000, Math.round(timerInteval*rate_open_close));
		thread_more.schedule(tt_more, 1000, 1000);
		
	}
	
	private void setCur_sv(float cur_sv) {
		this.cur_sv = cur_sv;
		if (isReached()) {
			stop();
			
			
		}
	}
	
	private boolean isOpen() {
		return this.opp == ConstOperationKnd.OPEN;
	}
	
	private boolean isClose() {
		return this.opp == ConstOperationKnd.CLOSE;
	}
	
		
	
	private boolean isReached() {
		boolean ret = false;
		byte openrate = getOpenRate();
		if (isAutoFit) {
			if (isOpen()) {			
				ret = (lm <= openrate) || (lo <= cur_sv) || (of <= openrate) || (openrate >= 100);
			} else if (isClose()) { 
				//System.out.println("isClose : " + of + "  " + openrate + "  " + cur_sv);
				ret = (of >= openrate) || (cur_sv <= 0);
			}
		} else {
			if (isOpen()) {			
				ret = (lm <= openrate) || (lo <= cur_sv) || (openrate >= 100);
			} else if (isClose()) { 
				ret = (cur_sv <= 0);
			}
		}
		
		//System.out.println("isReached : " + ret);
		
		
		
		return ret;
	}
	
	
	


	@Override
	public byte getId() {		
		return this.id;
	}


	@Override
	public void setOppFit( byte orderfit) {
		this.isAutoFit = true;
		this.of = orderfit;
		
		//System.out.println( orderfit + "  " + getOpenRate());
		
		if (getOpenRate() < orderfit) {
			setOpp(ConstOperationKnd.OPEN);
			
		} else if (getOpenRate() > orderfit) {
			setOpp(ConstOperationKnd.CLOSE);
			
		} else {
			setOpp(ConstOperationKnd.STOP);
		}		
	}
	
	@Override
	public void setOppNormal(byte opp) {
		//System.out.println(  "setOppNormal :  " + opp);
		this.isAutoFit = false;
		this.setOpp(opp);
	}
	
	
	private void setOpp(byte opp) {
		//System.out.println("setOpp : " + opp);
		if  (this.opp != opp)  {
			this.opp = opp;
			if  (opp == ConstOperationKnd.STOP)  {
				//this.ref
				stop();
			} else {
				if (!isReached()) {
					operation();
				} 
			}
		} 
	}
	
		
	private  void stop() {
		try {
			ref.set(index, false);
			this.opp = ConstOperationKnd.STOP;
			if (preOpp != opp) {
				curMoreTime = 0;
				hc595.updateBit(this.hc595Open, this.opp, (byte) 0);
				//arduSPI.sendCmd((byte)((opp << 5) + this.ledNo));
			}
			preOpp = opp;
			
			byte[] sendData = new byte[1];
			sendData[0] = (byte) ( (1 << 7) + this.getOpenRate());
			
			Map<String, Object> updateMap = new HashMap<String, Object>();
			
			updateMap.put("url", "/process/update_oppstateor");				
			updateMap.put("os", 1);
			updateMap.put("or", this.getOpenRate());
			
			updateMap.put("side", this.side);
			updateMap.put("prevos", preOpp);
			
			this.parent.sendToApp(HeaderDefine.CH_LAYER_SUB_STOP, this.id,sendData, updateMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private synchronized void operation() {
		try {
			//System.out.println(  "operation :  " + preOpp + "  " + opp);
			if (this.preOpp != this.opp) {
				
				ref.set(index, true);
				hc595.updateBit(this.hc595Open, this.opp, (byte) 0);
				//arduSPI.sendCmd((byte)((opp << 5) + this.ledNo));
			}
			this.preOpp = this.opp;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void setAutoManual(boolean isAuto) {
		setOpp(ConstOperationKnd.STOP);
		
	}


	@Override
	public void setLimitValue(int limit_open, int limit_close, int limit_max) {
		this.lo = limit_open;
		this.lc = limit_close;
		this.lm = limit_max;	
		
		float rate_open_close = (float) ((lc*1.0) / (lo*1.0));
		
		try {
			thread_close.cancel();
			thread_close = null;
			tt_close.cancel();
			thread_close = new Timer();
			tt_close = new TimerTask() {
				@Override
				public void run() {
					if (isClose()) {
						setCur_sv(cur_sv - 1);
					}
				}
			};
			
			thread_close.schedule(tt_close, 1000, Math.round(timerInteval*rate_open_close));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//thread_open.schedule(tt_open, 1000, timerInteval);
		//thread_close.schedule(tt_close, 1000, Math.round(timerInteval*rate_open_close));
	}
	
	@Override
	public void setOpenRateReset(  byte rate) {
		this.or = rate;	
		this.cur_sv =  (float) ((or/100.0) * lo);
	}

	@Override
	public byte getOpenRate() {
		//System.out.println("id : : " + this.id + "  " + ((cur_sv / (float)lo) * 100.0));
		return (byte)(((float)cur_sv / (float)lo) * 100.0);
	}

	@Override
	public byte getOpp() {
		return this.opp;
	}
	
	
	@Override
	public boolean isAutoSatisfy(byte operation) {
		boolean satisfy = false;
		byte openrate = getOpenRate();
		
		if (operation == ConstOperationKnd.OPEN) {
			boolean boolOpenMaxRate = (lm <= openrate);
			boolean boolOpenLimit = (lo <= cur_sv);
			boolean boolOpenOverRate = (openrate >= 100);
			if (isAutoFit) {
				boolean boolOrderRate = (of <= openrate);
				satisfy = boolOpenMaxRate || boolOpenLimit || boolOpenOverRate || boolOrderRate;
			} else {
				satisfy = boolOpenMaxRate || boolOpenLimit || boolOpenOverRate;
			}
			
		} else if (operation == ConstOperationKnd.CLOSE) {
			boolean boolCloseOverRate = (cur_sv <= 0);
			satisfy = boolCloseOverRate;
			
		}
		return satisfy;
	}
	
	
	@Override
	public boolean isMatchSide(byte side) {
		return (this.side == side);
	}
	
	@Override
	public boolean isFit(byte orderfit) {
		this.of = orderfit;
		return  (orderfit >= getOpenRate());
	}
	
	@Override
	public boolean isClosed() {
		return (cur_sv <= 0);
	}
	
	@Override
	public boolean isOpend() {
		//System.out.println("isOpend : " + lm + "  " + "getOpenRate() : "  + "  " + getOpenRate());
		return  (lm <= getOpenRate());
	}

	
	@Override
	public byte get595Open() {
		return hc595Open;
	}

	@Override
	public byte get595Close() {
		return hc595Close;
	}
	
	@Override
	public void setIndex(byte k, BitSet ref) {
		this.index = k;
		this.ref = ref;
	}
	
}
