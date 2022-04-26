package com.tene.controller.relay;


import java.util.Calendar;
import java.util.Date;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.tene.controller.Control;


public class RasGPIO {
	//private Pin PIN_FAN = RaspiPin.GPIO_27;	
	//private Pin PIN_FAN = RaspiPin.GPIO_01;
	private Pin PIN_FAN = RaspiPin.GPIO_25;
	private GpioPinDigitalOutput pin_fan = null;
	
	//private Pin PIN_FEATHER = RaspiPin.GPIO_00;	
	//private Pin PIN_FEATHER = RaspiPin.GPIO_28;
	private Pin PIN_FEATHER = RaspiPin.GPIO_02;
	private GpioPinDigitalOutput pin_feather = null;
	
	//private Pin PIN_RAIN = RaspiPin.GPIO_03;	
	//private Pin PIN_RAIN = RaspiPin.GPIO_29;
	
	private Pin PIN_RAIN = RaspiPin.GPIO_04;
	
	private GpioPinDigitalInput pin_rain = null;
	
	private boolean isBriefing = false;
	
	private Date btBegin = null;
	private Date btNow = null;
	
	
	private boolean firstBr = false;
	private Control control = null;
	
	public RasGPIO(Control control) {
		this.control = control;
		//System.out.println("RRRR " + pin_rain);
		try {
			
			btBegin = new Date();
		
			final GpioController gpio = GpioFactory.getInstance();
			pin_fan = gpio.provisionDigitalOutputPin(PIN_FAN, "MyLED", PinState.LOW);
			
		
			pin_feather = gpio.provisionDigitalOutputPin(PIN_FEATHER, "MyLED", PinState.LOW);
			pin_feather.setShutdownOptions(true);
			
			pin_feather.high();
			
			
			
			pin_rain = gpio.provisionDigitalInputPin(PIN_RAIN, PinPullResistance.PULL_UP);
			//pin_rain = gpio.provisionDigitalInputPin(PIN_RAIN, PinPullResistance.PULL_DOWN);
			
			
			pin_rain.setShutdownOptions(true);
			
			
			pin_rain.addListener(new GpioPinListenerDigital() {
	            @Override
	            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
	                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
	                
	                //if (event.getState() == PinState.HIGH) {
	                if (event.getState() == PinState.LOW) {
	                	//System.out.println("Rain");
	                	if (!isBriefing) {
	                		
		                	btNow = new Date();
		                	
		                	//long nowMin = btNow.getTime();
		                	//long beginMin = btBegin.getTime();
		                	
		                	if (10 < ((btNow.getTime() - btBegin.getTime()) / (1000*60))) {
		                		btBegin = new Date();	                		
		                		control.briefingFromDoorSensor();
		                		isBriefing = true;
		                	} else if (!firstBr) {
		                		firstBr = true;
		                		btBegin = new Date();	                		
		                		control.briefingFromDoorSensor();
		                		isBriefing = true;
		                	}
	                	}
	                	
	                } else {
	                	//System.out.println("stop");
	                	//if (isBriefing) {
	                		//btEnd = new Date();
	                		isBriefing = false;
	                	//}
	                	
	                }
	                
	                //System.out.println("Rain : " + isRain);
	            }
	
	        });
			
			//System.out.println("RRRR " + pin_rain);
		} catch(Exception e) {
			e.printStackTrace();
		}
	
	}

	/*
	public boolean getRain() {
		return isBriefing;
	}
	*/

	public void runFan(float temp) {
		//System.out.println("CPU Temperature   :  " + temp);
		
		if (temp > 40) {
		//if (temp > 30) {
			pin_fan.high();
		} else {
			pin_fan.low();
		}
		
	}
	
	public void resetFeather() {	
		
		try {
			pin_feather.low();
			Thread.sleep(500);
			pin_feather.high();
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
}