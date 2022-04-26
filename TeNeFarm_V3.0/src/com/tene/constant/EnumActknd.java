package com.tene.constant;

public class EnumActknd {
	public static enum KIND {
	    LEFTRIGHT, NOLEFTRIGHT, ONOFF
	}
	
	//public static enum OPP_DIRECTION {
	public static enum ACT_KIND {
	    SIDESCREEN, TOPSCREEN, WARMSIDESCREEN, WARMTOPSCREEN, CO2, MIST, BLOCKSCREEN, VENT_PAN,MOVE_FAN, HEATER, AIRCON, WATER, LIGHT, WATERSCREEN, TURNEL
	}
	
	public static EnumActknd.ACT_KIND setActKind(String kind) {
		EnumActknd.ACT_KIND ret = EnumActknd.ACT_KIND.SIDESCREEN;
		switch (kind) {
		case "01" :
			ret  = EnumActknd.ACT_KIND.SIDESCREEN;
			break;
		case "02" :
			ret  = EnumActknd.ACT_KIND.BLOCKSCREEN;
			break;
		case "03" :
			ret  = EnumActknd.ACT_KIND.MOVE_FAN;
			break;
		case "04" :
			ret  = EnumActknd.ACT_KIND.WATER;
			break;
		case "05" :
			ret  = EnumActknd.ACT_KIND.VENT_PAN;
			break;
		case "06" :
			ret  = EnumActknd.ACT_KIND.TOPSCREEN;
			break;
		case "07" :
			ret  = EnumActknd.ACT_KIND.WARMSIDESCREEN;
			break;
		case "08" :
			ret  = EnumActknd.ACT_KIND.WARMTOPSCREEN;
			break;
		case "10" :
			ret  = EnumActknd.ACT_KIND.CO2;
			break;
		case "11" :
			ret  = EnumActknd.ACT_KIND.MIST;
			break;
		case "12" :
			ret  = EnumActknd.ACT_KIND.HEATER;
			break;
		case "13" :
			ret  = EnumActknd.ACT_KIND.LIGHT;
			break;
		case "14" :
			ret  = EnumActknd.ACT_KIND.WATERSCREEN;
			break;
		case "15" :
			ret  = EnumActknd.ACT_KIND.TURNEL;
			break;
		}
		
		return ret;
	}
}
