package com.tene.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtils {
	
	public static byte binaryStringToByte(String s){
	    byte ret=0, total=0;
	    for(int i=0; i<8; ++i){         
	        ret = (s.charAt(7-i)=='1') ? (byte)(1 << i) : 0;
	        total = (byte) (ret|total);
	    }
	    return total;
	}
	
	public static byte[] stringTobyte(String str) {
	     return str.getBytes();
	}
	
	public static String byteToString(byte[] bytes) {
	     return new String(bytes);
	}
	public static byte [] float2ByteArray (float value) {  
		int bits = Float.floatToIntBits(value);
		byte[] bytes = new byte[4];
		bytes[0] = (byte)(bits & 0xff);
		bytes[1] = (byte)((bits >> 8) & 0xff);
		bytes[2] = (byte)((bits >> 16) & 0xff);
		bytes[3] = (byte)((bits >> 24) & 0xff);
		
		return bytes;
	 }
	
	public static int bytetoInt(byte[] bytes) {
	     return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}
	
	public static float byteArray2Float (byte [] value) {  
		float f_value = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN).getFloat();
		return f_value;
	}
	
	
	
	public static final byte[] intToByteArray(int value) {
	    return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
	}
	
	
	public static final byte[] intToByteArray16(int value) {
	    return new byte[] {
	            (byte)(value >>> 8),
	            (byte)value};
	}
	
	public static final byte[] longToByteArray16(long value) {
	    return new byte[] {
	    		
	            (byte)(value >>> 8),
	            (byte)value};
	}
	
	public static int byteArray2Int (byte [] value) {
		
		ByteBuffer wrapped = ByteBuffer.wrap(value); // big-endian by default
		int f_value = wrapped.getShort(); // 1
		return f_value;
	}

	public static int fromByteArray(byte[] bytes) {
	     return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}
	

}
