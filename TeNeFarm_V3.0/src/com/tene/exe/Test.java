package com.tene.exe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.json.JSONObject;

import com.tene.utils.GetCurrentTime;

import de.re.easymodbus.modbusclient.ModbusClient;

public class Test  {
	
	public static void main(String[] args) {
		
	 /*
	 System.out.println("TENEFARM Local Server Started [ver 3.1]");
	 ModbusClient modbusClient = new ModbusClient("30.1.6.211",port);
		try
		{
			modbusClient.Connect();
			//address(해당 주소값) , 그 뒤 데이터 몇개를 뽑을지  
			int[] soc = modbusClient.ReadHoldingRegisters(1010,1);
			System.out.println(soc[0]);
			System.out.println(Arrays.toString(soc));
         
			int[] sum_power = modbusClient.ReadHoldingRegisters(1016,2);
			System.out.println(Arrays.toString(sum_power));
			String hex_pw = "";
			for(int i : sum_power) {
				hex_pw += Integer.toHexString( i );
				System.out.println(hex_pw);
			}
			System.out.println(Integer.parseInt(hex_pw,16)*0.001);
		}
		catch (Exception e)
		{	
			e.printStackTrace();
		}
		*/
		
	}	
	
	
}
