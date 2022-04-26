package com.tene.controller.relay;

import java.util.BitSet;
import java.util.Scanner;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import com.tene.constant.ConstOperationKnd;





public class HC595 {
	
	private HC595Thread qThread = null; 
	
	private Pin PIN_595_LATCH = RaspiPin.GPIO_29;  //4	
	private Pin PIN_595_CLOCK = RaspiPin.GPIO_28;  //3	
	private Pin PIN_595_EN = RaspiPin.GPIO_27;   //5
	private Pin PIN_595_DATA = RaspiPin.GPIO_26;   //5
	
	
	private GpioPinDigitalOutput pin_clock = null;
	private GpioPinDigitalOutput pin_latch = null;
	private GpioPinDigitalOutput pin_data = null;
	private GpioPinDigitalOutput pin_en = null;
	
	private int Count595 = 32;
	

	//private BitSet dataBitSet = new BitSet(Count595);
	
	public HC595() {
		final GpioController gpio = GpioFactory.getInstance();
		
		
		
		pin_clock = gpio.provisionDigitalOutputPin(PIN_595_CLOCK, "MyLED", PinState.LOW);
		pin_clock.setShutdownOptions(true, PinState.LOW);
		
		pin_latch = gpio.provisionDigitalOutputPin(PIN_595_LATCH, "MyLED", PinState.LOW);
		pin_latch.setShutdownOptions(true, PinState.LOW);
		
		pin_data = gpio.provisionDigitalOutputPin(PIN_595_DATA, "MyLED", PinState.LOW);
		pin_data.setShutdownOptions(true, PinState.LOW);
		
		pin_en = gpio.provisionDigitalOutputPin(PIN_595_EN, "MyLED", PinState.HIGH);
		pin_en.setShutdownOptions(true, PinState.HIGH);

		
		//gpio.provisionDigitalInputPin(PIN_595_EN, "MyLED", PinPullResistance.PULL_UP);
			
		
		
		try {
			Thread.sleep(100);
			//dataBitSet.clear();
			
			
			//pin_en.low();
			//Thread.sleep(10);
			pin_latch.low();
			for (byte i=0; i<Count595; i++)  {
				pin_clock.low();	
				Gpio.digitalWrite(PIN_595_DATA.getAddress(),  Gpio.LOW);
				pin_clock.high();
				
			}
			pin_latch.high();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		
		pin_en.low();
		
		qThread = new HC595Thread(pin_clock,pin_latch,pin_en, PIN_595_DATA, Count595); 
		
		qThread.start();
	}
	
	
	
	public void updateBit(byte bitPos, byte status, byte layer) {		
		boolean pwm = false;
		boolean dir = false;
		if (status == ConstOperationKnd.OPEN) {
			pwm = true;
			dir = true;			
		} else if (status == ConstOperationKnd.CLOSE) {
			pwm = true;
			dir = false;
						
		} else if (status == ConstOperationKnd.STOP) {
			pwm = false;
			dir = false;			
		}
		
		
		//byte bitPos = (byte) (55 - (rno-2));
		//byte bitPos = (byte) (55 - rno);
		//byte bitPos = rno;
		
		
		//System.out.println(rno + "  " + bitPos + "  " + pwm + "  " + dir);
		
		/*
		dataBitSet.set(bitPos+1,dir);	
		dataBitSet.set(bitPos, pwm);
		
		if (layer == 4) {
			dataBitSet.set(bitPos-2,dir);		
			dataBitSet.set(bitPos-3, pwm);
		}
		*/
		qThread.addData(bitPos,dir,pwm);
		
		//qThread.addData(dataBitSet);
		
	}
	
	
	public void updateBit_Pans(byte bitPos, byte status) {	
		//System.out.println("pro");
		//System.out.println("updateBit_Pans : " + rno  + "  " + status);
		boolean opp = false;
		if (status == ConstOperationKnd.OPEN) {
			opp = true;			
		} else if (status == ConstOperationKnd.STOP) {
			opp = false;			
		}
		//byte bitPos = (byte) (55 - rno);
		//byte bitPos = (byte) (rno);
		
		//dataBitSet.set(bitPos,opp);
		//dataBitSet.set(rno,opp);	
		
		qThread.addData(bitPos,opp);
		
		//qThread.addData((byte)2,dataBitSet);
		//System.out.println("pan : " + dataBitSet);
		//System.out.println("pan : " + rno + "  " + bitPos);
		///dataBitSet.set(bitPos-1, pwm);
		/*
		System.out.println("pan : " + rno + "  " + bitPos);
		for (byte i=0; i<56; i++)  {
			//long org = getBit(this.dataMD,i);
			byte org = (dataBitSet.get(i) ? (byte)1 : 0);
			System.out.print(org);
		}
		System.out.println("");
		*/
	}
	
	/*
	public void updateBegin() {
		qThread.addData((byte)1);
		//System.out.println("begin");
		//pin_latch.low();
	}
	
	public void updateEnd() {
		qThread.addData((byte)3);
		//System.out.println("end");
		//pin_latch.high();
	}
	*/
	
	public void stop() {
		
		/*
		//System.out.println("595 stop");
		try {
		//dataBitSet.clear();
		
		
		//pin_en.low();
		//Thread.sleep(10);
		pin_latch.low();
		//for (byte i=31; i>-1; i--)  {
		for (byte i=0; i<Count595; i++)  {
			//System.out.println(i + "  " + (qData.getDataBitSet().get(i) ? Gpio.HIGH : Gpio.LOW));
			pin_clock.low();	
			//Thread.sleep(10);
			Gpio.digitalWrite(PIN_595_DATA.getAddress(), (dataBitSet.get(i) ? Gpio.HIGH : Gpio.LOW));
			//Gpio.digitalWrite(PIN_595_DATA.getAddress(),  Gpio.LOW);
			//Thread.sleep(10);
			pin_clock.high();
			
		}
		//Thread.sleep(10);
		pin_latch.high();
		//Thread.sleep(10);
		
		//pin_en.high();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
	}
	
	
	
	/*
	public void update() {
		for (byte i=0; i<56; i++)  {
			pin_clock.low();			
			Gpio.digitalWrite(PIN_595_DATA.getAddress(), dataBitSet.get(i));
			pin_clock.high();
		}
		
	}
	*/

}
