package com.tene.constant;

import java.util.HashMap;
import java.util.Map;

public class HeaderDefine {
	
	
	public static final byte RUN_BRIEFING = 100;
	
	public static final byte C_WEB_CLIENT_CONNECTED = 11;
	public static final byte C_WEB_CLIENT_RECONNECTED = 12;
	public static final byte C_WEB_CLIENT_CHECKCONNTION = 13;
	public static final byte C_WEB_CLIENT_CONNECTED_COMPLETE = 23;
	public static final byte C_WEB_CLIENT_CLOSE = 24;
	public static final byte C_ISLOCAL = 25;
	
	//public static final byte C_WEB_CLIENT_CONNECTED_COMPLETE_FORECAST = 25;
	
	
	
	//receive
	public static final byte R_DATA = 21;
	//public static final byte R_ENV = 21;
	
	
	//CONTROL KIND
	public static final byte CH_AUTOMANUAL = 31;
	
	
	
	
	//WS
	
	public static final byte ORD = 98;
	
	public static final byte CH_ANAL_OPEN = 32;
	public static final byte CH_ONOFF = 33;
	public static final byte CH_LAYER_SUB_STOP = 36;
	public static final byte CH_OPPFIT = 34;
	
	public static final byte CH_ONOFF_SUB_ALL = 57;
	
	public static final byte CH_SINGLE_VALUE = 34;
	public static final byte CH_MULTY_VALUE = 35;
	
	public static final byte CH_DYNAMIC_VALUE = 37;
	public static final byte CH_WINDDIR = 38;
	
	public static final byte CH_LIMIT_VALUE = 39;
	
	public static final byte CH_CONDITION_VALUE = 40;
	
	public static final byte CH_CONDITION_PRIORITY = 41;
	
	public static final byte CH_OPENRATE = 42;
	public static final byte CH_OPENRATE_RESET = 43;
	
	public static final byte CH_RUNSTD = 44;
	public static final byte CH_LED = 47;
	
	//public static final byte GET_VALUE = 43;
	
	//public static final byte GET_SENSOR_KND = 44;
	
	public static final byte CH_ACTGRP_ALL_STOP = 45;
	
	public static final byte CH_ACTGRP_ACT_STOP = 46;
	
	public static final byte CH_PERIORD = 51;
	
	public static final byte CH_PRIORITY_DETAIL = 52;
	
	public static final byte CH_CUR_RULE = 54;
	
	public static final byte CH_RULE_INTERVAL = 55;
	
	public static final byte INS_PERIORD = 58;
	public static final byte DEL_PERIORD = 59;
	
	public static final byte UP_CORECTION = 64;
	public static final byte UP_ETC = 65;
	public static final byte UP_ROLL = 66;
	
	
	
	public static final byte CH_SENSOR_ALERT = 61;
	public static final byte CH_SENSOR_ALERT_WS = 62;
	
	public static final byte CHECKALIVE = 63;
	
	//public static final byte SEND_LED = 67;
	
	
	public static Map<Byte,String> no_index = new HashMap<Byte,String>();
	
	public static void init() {
		no_index.put((byte) 0, "0");
        no_index.put((byte) 1, "1");
        no_index.put((byte) 2, "2");
        no_index.put((byte) 3, "3");
        no_index.put((byte) 4, "4");
        no_index.put((byte) 5, "5");
        no_index.put((byte) 6, "6");
        no_index.put((byte) 7, "7");
        no_index.put((byte) 8, "8");
        no_index.put((byte) 9, "9");
        no_index.put((byte) 10, "A");
        no_index.put((byte) 11, "B");
        no_index.put((byte) 12, "C");
        no_index.put((byte) 13, "D");
        no_index.put((byte) 14, "E");
        no_index.put((byte) 15, "F");
        no_index.put((byte) 16, "G");
        no_index.put((byte) 17, "H");
        no_index.put((byte) 18, "I");
        no_index.put((byte) 19, "J");
        no_index.put((byte) 20, "K");
        no_index.put((byte) 21, "L");
        no_index.put((byte) 22, "M");
        no_index.put((byte) 23, "N");
        no_index.put((byte) 24, "O");
        no_index.put((byte) 25, "P");
        no_index.put((byte) 26, "Q");
        no_index.put((byte) 27, "R");
        no_index.put((byte) 28, "S");
        no_index.put((byte) 29, "T");
        no_index.put((byte) 30, "U");
        no_index.put((byte) 31, "V");
	}
	
	
	
    
	
}
