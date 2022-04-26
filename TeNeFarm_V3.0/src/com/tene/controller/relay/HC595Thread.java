package com.tene.controller.relay;


import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;



import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.wiringpi.Gpio;

public class HC595Thread extends Thread {
	
	//private Queue<QData> stackdata = new LinkedList<QData>();
	private GpioPinDigitalOutput pin_clock = null;
	private GpioPinDigitalOutput pin_latch = null;
	private Pin PIN_595_DATA = null;
	private BitSet currentBits = new BitSet(32);
	private BitSet prevBits = new BitSet(32);
	
	
	private int Count595 = 0;
	
	public HC595Thread(GpioPinDigitalOutput pin_clock,
			GpioPinDigitalOutput pin_latch,GpioPinDigitalOutput pin_en,
			Pin PIN_595_DATA, int Count595) {
		
		this.pin_clock = pin_clock;
		this.pin_latch = pin_latch;
		//this.pin_en = pin_en;
		this.PIN_595_DATA = PIN_595_DATA;
		
		this.Count595 = Count595;
	}
	
	public void run() {		
		while (!this.isInterrupted()) {
			try {
				//if (stackdata.size() > 0) {
				//	QData qData = stackdata.poll();
				//	if (qData != null) {
						//switch (qData.getCmd()) {
						//case 1 :
				   		//	break;
						//case 2 :
				if (currentBits.isEmpty()) {
					pin_latch.low();
					for (byte i=0; i<Count595; i++)  {
						//System.out.println(i + "  " + (qData.getDataBitSet().get(i) ? Gpio.HIGH : Gpio.LOW));
						pin_clock.low();
						//Gpio.digitalWrite(PIN_595_DATA.getAddress(), (qData.getDataBitSet().get(i) ? Gpio.HIGH : Gpio.LOW));
						Gpio.digitalWrite(PIN_595_DATA.getAddress(),  Gpio.LOW);
						pin_clock.high();
					}
					pin_latch.high();
					Thread.sleep(100);
				} else {
					if (currentBits.equals(prevBits)) {
						
					} else {
						//System.out.println(currentBits);
						prevBits = (BitSet) currentBits.clone();
						//for (byte kk=0; kk<2; kk++) {
							pin_latch.low();
							for (byte i=0; i<Count595; i++)  {
								//System.out.println(i + "  " + (qData.getDataBitSet().get(i) ? Gpio.HIGH : Gpio.LOW));
								pin_clock.low();
								//Gpio.digitalWrite(PIN_595_DATA.getAddress(), (qData.getDataBitSet().get(i) ? Gpio.HIGH : Gpio.LOW));
								Gpio.digitalWrite(PIN_595_DATA.getAddress(), (currentBits.get(i) ? Gpio.HIGH : Gpio.LOW));
								pin_clock.high();
							}
							pin_latch.high();
							
							Thread.sleep(500);
						//}
						
						
						
						
						//prevBits = currentBits;
					}
					
					//pin_latch.low();
					//for (byte i=0; i<Count595; i++)  {
						//System.out.println(i + "  " + (qData.getDataBitSet().get(i) ? Gpio.HIGH : Gpio.LOW));
						//pin_clock.low();
						//Gpio.digitalWrite(PIN_595_DATA.getAddress(), (qData.getDataBitSet().get(i) ? Gpio.HIGH : Gpio.LOW));
						//Gpio.digitalWrite(PIN_595_DATA.getAddress(), (currentBits.get(i) ? Gpio.HIGH : Gpio.LOW));
						//pin_clock.high();
					//}
					//pin_latch.high();
				}
							
							
				   			//break;
						//case 3 :
				   		//	break;
						//}
				//}
				//} else {
					/*
					if (currentBits != null ) {
						pin_latch.low();
						for (byte i=0; i<Count595; i++)  {
							//System.out.println(i + "  " + (qData.getDataBitSet().get(i) ? Gpio.HIGH : Gpio.LOW));
							pin_clock.low();
							Gpio.digitalWrite(PIN_595_DATA.getAddress(), (currentBits.get(i) ? Gpio.HIGH : Gpio.LOW));
							pin_clock.high();
						}
						pin_latch.high();
					}
					*/
					
					
				//}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	public void addData(byte cmd) {
		QData qData = new QData(cmd);
		stackdata.add(qData);
		
		//currentBits.or(qData.getDataBitSet());
		//currentBits.and(qData.getDataBitSet());
		//this.currentDataBitSet = qData.getDataBitSet();
	}
	*/
	
	public void addData(byte bitPos, boolean opp) {
		//QData qData = new QData(cmd,dataBitSet);
		//currentBits.set(bitPos+1,dir);		
		//dataBitSet.set(bitPos-1, pwm);
		currentBits.set(bitPos, opp);
		
		
		//QData qData = new QData(currentBits);
		//stackdata.add(qData);
		//currentBits.or(qData.getDataBitSet());
		//currentBits.and(qData.getDataBitSet());
		//this.currentDataBitSet = qData.getDataBitSet();
	} 
	
	//public void addData(byte cmd, BitSet dataBitSet) {
	public void addData(byte bitPos, boolean dir, boolean pwm) {
		//QData qData = new QData(cmd,dataBitSet);
		currentBits.set(bitPos+1,dir);		
		//dataBitSet.set(bitPos-1, pwm);
		currentBits.set(bitPos, pwm);
		
		
		//QData qData = new QData(currentBits);
		//stackdata.add(qData);
		//currentBits.or(qData.getDataBitSet());
		//currentBits.and(qData.getDataBitSet());
		//this.currentDataBitSet = qData.getDataBitSet();
	} 
	
	
	
	public void close() {
		this.stop();
		
	}

}
