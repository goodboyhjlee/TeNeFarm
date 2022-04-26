package com.tene.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


public class GetCurrentTime {
	public static int getCurrentHM()  {
		//boolean valid = false;
		
		Date cur_date = new Date();	
		
		return (cur_date.getHours() * 60) + cur_date.getMinutes(); 
		
		//return valid;
	}
	public static boolean compareHoure(byte hs, byte he)  {
		boolean valid = false;
		
		Date cur_date = new Date();	
		String currentTime = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
				String.format("%02d", (cur_date.getDate()))   +  " " +  String.format("%02d", cur_date.getHours()) + ":" +
				String.format("%02d", cur_date.getMinutes()) + ":" + String.format("%02d", cur_date.getSeconds());
		
		String finalTime = null;
		String initialTime = null;
		
		try {	
			if ( (he-hs) < 0) {
				if (hs < cur_date.getHours()) {
					initialTime = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
							String.format("%02d", (cur_date.getDate()))   +  " " +  String.format("%02d", hs) + ":00:00";
					
					finalTime   = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
							String.format("%02d", (cur_date.getDate()+1))   +  " " +  String.format("%02d", he) + ":59:59";
				} else {					
					initialTime = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
							String.format("%02d", (cur_date.getDate()-1))   +  " " +  String.format("%02d", hs) + ":00:00";
					finalTime   = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
							String.format("%02d", (cur_date.getDate()))   +  " " +  String.format("%02d", he) + ":59:59";
				}
			} else {
				initialTime = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
						String.format("%02d", (cur_date.getDate()))   +  " " +  String.format("%02d", hs) + ":00:00";
				finalTime   = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
						String.format("%02d", (cur_date.getDate()))   +  " " +  String.format("%02d", he) + ":59:59";
			}

			java.util.Date inTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(initialTime);
            Calendar inti_cal = Calendar.getInstance();
            inti_cal.setTime(inTime);

            //Current Time
            java.util.Date checkTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentTime);
           
            Calendar cur_cal = Calendar.getInstance();
            cur_cal.setTime(checkTime);

            //End Time
            java.util.Date finTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(finalTime);
            Calendar fin_cal = Calendar.getInstance();
            fin_cal.setTime(finTime);	        
	        valid = cur_cal.after(inti_cal) && cur_cal.before(fin_cal)  ;
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return valid;
	}
	
	public static void compareHourMin(int beginHM,int endTHM, Map<Byte,Integer> ret)  {
		Calendar today = Calendar.getInstance();
		int nowHM = (today.getTime().getHours()*60) + today.getTime().getMinutes();
		int valid = 0;
		float elapsedTime =  0;
		float duringTime = 0;
		float rate = 0;
		//System.out.println(nowHM + "/" + beginHM + "/" + endTHM);
		if (beginHM < endTHM) { // Normal 
			if ( (beginHM < nowHM) && (endTHM >= nowHM) ) {
				valid = 1;
				elapsedTime =  nowHM - beginHM;
				duringTime = endTHM - beginHM;
			}
		} else {
			duringTime =  (24-beginHM) + endTHM;
			if (beginHM < nowHM) { // begin date change +1
				valid = 1;
				elapsedTime =  nowHM - beginHM;
			} else {
				if (endTHM >= nowHM) {
					valid = 1;
					elapsedTime =  nowHM - (24-beginHM);
				}
			}
		}
		
		if (valid==1) {
			
			
			rate = (float) ((elapsedTime / duringTime) * 100.0); 
			
			//System.out.println(elapsedTime + "/" + duringTime + "=" + rate);
			
			
			//ret.get((byte)0) = 1;
			ret.put((byte)0, valid);
			ret.put((byte)1, Math.round(rate));
		}
				
		
		/*
		String finalTime = null;
		String initialTime = null;
		
		
		
		String curTime = String.format("%02d", beginH) + ":" + String.format("%02d", beginM) + ":00";
		
		try {	
			if ( (endH-beginH) < 0) {
				if (beginH < endH) { // Normal 
					initialTime = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
							String.format("%02d", (cur_date.getDate()))   +  " " +  String.format("%02d", beginH) + ":00:00";
					
					finalTime   = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
							String.format("%02d", (cur_date.getDate()+1))   +  " " +  String.format("%02d", endH) + ":59:59";
				} else { // before day					
					initialTime = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
							String.format("%02d", (cur_date.getDate()-1))   +  " " +  String.format("%02d", beginH) + ":00:00";
					finalTime   = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
							String.format("%02d", (cur_date.getDate()))   +  " " +  String.format("%02d", endH) + ":59:59";
				}
			} else {
				initialTime = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
						String.format("%02d", (cur_date.getDate()))   +  " " +  String.format("%02d", hs) + ":00:00";
				finalTime   = (cur_date.getYear() + 1900) + "-" + String.format("%02d", (cur_date.getMonth()+1)) + "-" + 
						String.format("%02d", (cur_date.getDate()))   +  " " +  String.format("%02d", he) + ":59:59";
			}

			java.util.Date inTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(initialTime);
            Calendar inti_cal = Calendar.getInstance();
            inti_cal.setTime(inTime);

            //Current Time
            java.util.Date checkTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentTime);
           
            Calendar cur_cal = Calendar.getInstance();
            cur_cal.setTime(checkTime);

            //End Time
            java.util.Date finTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(finalTime);
            Calendar fin_cal = Calendar.getInstance();
            fin_cal.setTime(finTime);	        
	        valid = cur_cal.after(inti_cal) && cur_cal.before(fin_cal)  ;
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		//return valid;
	}

	public static int getTime() {
		
		Date dd = new Date();
		int cur_hh = dd.getHours();
		//int cur_mm = dd.getMinutes();
		//int cur_ss = dd.getSeconds();
		
		return cur_hh;
	}
	
	public static boolean isAM() {
		
		Date dd = new Date();
		
		
		
		return (dd.getHours() < 13);
	}
	
	public static String getTimeForControl() {
	     SimpleDateFormat f = new SimpleDateFormat("HH:mm");
	     return f.format(new Date());
	}
	
	public static String getTimeForRelay() {
	     SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
	     return f.format(new Date());
	}
	
	public static String getCurrentTime() {
	     SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     return f.format(new Date());
	}
	
	public static String getCurDate() {
	     SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
	     return f.format(new Date());
	}
	
	public static String getCurTime() {
	     SimpleDateFormat f = new SimpleDateFormat("HH00");
	     return f.format(new Date());
	}
	
	public static String getCurForeCastDate() {
		Calendar cal = Calendar.getInstance();
		Date now = new Date();				
		cal.setTime(now);
		int hour = now.getHours();
		int min = now.getMinutes();
		
		//System.out.println(hour + ":" + min);
		
		if (min > 40) {
			
		} else {
			if (hour == 0)
				cal.add(Calendar.DATE, -1);
		}
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		
	    return f.format(cal.getTime());
	}
	
	public static String getCurForeCastTime() {
		Calendar cal = Calendar.getInstance();
		Date now = new Date();				
		cal.setTime(now);
		int hour = now.getHours();
		int min = now.getMinutes();
		
		//System.out.println(hour + ":" + min);
		
		if (min > 40) {
			
		} else {
			cal.add(Calendar.HOUR, -1);
		}
		SimpleDateFormat f = new SimpleDateFormat("HH00");
	    return f.format(cal.getTime());
	}
	
	public static int getIncSecond(Date pre, Date now) {
		long diff = now.getTime() - pre.getTime();
		return (int) (diff/1000);
	}
}
