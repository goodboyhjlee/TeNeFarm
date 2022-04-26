package com.tene.socket;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.json.JSONArray;
import org.json.JSONObject;

//import com.tene.exe.Run;
import com.tene.forecast.Forecast;
//import com.tene.sensor.Sensor_RF;
import com.tene.utils.ByteUtils;



public class TeneBroadcsstServer {
	private DatagramSocket ds = null;
	private InetAddress ia = null;
	private int port = 40000;
	
	private Forecast forecast = null;
	private int outTemp = 0;
	private int winddir = 0;
	private int windspeed = 0;
	
	private boolean isIncludeOutTemp = false;
	
   
	public void boradcast(byte[] buffer) {
	   try {
		   
		   if (forecast != null) {
			   int dataSize = buffer.length;
			   int addedDataSize = dataSize + 6;;
			   byte[] sendData = new byte[addedDataSize];
			   
			   System.arraycopy(buffer, 0, sendData, 0, dataSize);
			   
			   ByteArrayOutputStream buf = new ByteArrayOutputStream();
			   
			  
			   if (isIncludeOutTemp) {
				   //buf.write((byte)1); // wind dir
				  // outTemp  = Math.round(forecast.getTemp());	
				   outTemp  = Math.round((float)190.2);
				  // System.out.println("outTemp :  " + outTemp);
				   //buf.write(ByteUtils.intToByteArray16(outTemp));
			   }
			  
			   
			   /*
			   
			   */
			   
			   buf.write((byte)7); // wind dir
			   winddir  = Math.round(forecast.getWindDir());			   
			   buf.write(ByteUtils.intToByteArray16(winddir));
			   
			   buf.write((byte)8); // wind speed
			   windspeed = Math.round(forecast.getWindSpeed()*10);
			   buf.write(ByteUtils.intToByteArray16(windspeed));
			   System.arraycopy(buf.toByteArray(), 0, sendData, dataSize, 6);
			   DatagramPacket data = new DatagramPacket(sendData, sendData.length, ia, port);
			   ds.send(data);
			   
			   //System.out.println("nid : " + outTemp + "  " + winddir + "  " + windspeed);
			   
		   } else {
			   DatagramPacket data = new DatagramPacket(buffer, buffer.length, ia, port);
			   ds.send(data);
		   }
		   
		   //byte[] sendData
		   
	   } catch (Exception e) {
		   e.printStackTrace();
	   } 
   }
	
	
	public int getOutTemp() {
		return this.outTemp;
	}
	
	public int getWindDir() {
		winddir  = Math.round(forecast.getWindDir());	
		return winddir;
	}
	
	public int getWindSpeed() {
		windspeed = Math.round(forecast.getWindSpeed());
		return windspeed;
	}
	
	public boolean isForecast() {
		return (forecast != null);
	}
   
   public TeneBroadcsstServer(JSONArray jSensorNode, String apikey, int nx, int ny) {	   
		try {			
			int snCnt = jSensorNode.length();
			if (snCnt > 0)
			for (int i=0; i<snCnt; i++) {
				JSONObject sensornode = jSensorNode.getJSONObject(i);
				String snKnd = sensornode.getString("knd");
				byte nid = (byte) sensornode.getInt("key");
				
				//if (nid)
				//isIncludeOutTemp
				boolean snIsUse = sensornode.getString("iu").equals("Y") ? true : false;
				
				//System.out.println("nid : " + nid + "  " + snIsUse + "  " + snKnd);
				if (snIsUse) {
					if (snKnd.equals("05")) {
						
						JSONArray jSensor = sensornode.getJSONArray("sensor");
						int sCnt = jSensor.length();
						//System.out.println("sCnt : " + sCnt);
						for (int si=0; si<sCnt; si++) {
							JSONObject sensor = jSensor.getJSONObject(si);
							String sk =  sensor.getString("sk");
							//System.out.println("sk : " + sk);
							if (sk.equals("08"))
								isIncludeOutTemp = true;
						}
						
						
						forecast = new Forecast(apikey,nx,ny);
						
					} 					
				}
			}
			ia = InetAddress.getByName("192.168.0.255");
			ds = new DatagramSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
