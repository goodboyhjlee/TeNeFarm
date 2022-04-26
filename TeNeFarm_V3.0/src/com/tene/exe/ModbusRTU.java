/*
 * (c) Stefan Rossmann
 *	This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.tene.exe;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 *
 * @author SR555
 */
public class ModbusRTU 
{
     public static void main(String args[]) throws IOException, SerialPortException, ModbusException, SerialPortTimeoutException, InterruptedException
     {
    	 boolean success = false;
    	 try {
	        // ModbusClient modbusClient = new ModbusClient("30.1.6.211",502);
	    	 ModbusClient modbusClient = new ModbusClient();
	         
	         
	         modbusClient.Available(100);
	        // System.out.println(modbusClient.Available(10));
	         //modbusClient.Connect();
	         //����
	         modbusClient.Connect("30.1.6.212",502);
	         
	         //����
	         //modbusClient.Connect("30.1.6.211",502);
	         
	         
	         int[] soc = modbusClient.ReadHoldingRegisters(0x40005,1);
	         //int[] soc = modbusClient.ReadInputRegisters(0x40002,4);
	         //System.out.println(soc[0]);
	         System.out.println(Arrays.toString(soc));
	         
	        // int[] byte_buf = new byte[2];
	         //for (int i=0;  i<2; i++) {
			//	byte_buf[i] =  soc[i];
	         //}                  	  	
	         //float limit_value_open = ByteBuffer.wrap(soc).order(ByteOrder.LITTLE_ENDIAN).getFloat();
			
    	 } catch(Exception e) {
    		 e.printStackTrace();
    	 }
  
         /*
         int[] sum_power = modbusClient.ReadHoldingRegisters(1016,2);
         System.out.println(Arrays.toString(sum_power));
         String hex_pw = "";
         for(int i : sum_power) {
			hex_pw += Integer.toHexString( i );
			System.out.println(hex_pw);
         }
         System.out.println(Integer.parseInt(hex_pw,16)*0.001);
			*/
			
			/*
         while (true)
         {
        	 System.out.println(modbusClient.ReadInputRegisters(0, 10)[5]);
        	 //Thread.sleep(200);
         }
         */
         //modbusClient.WriteMultipleCoils(0, new boolean[] {true,true,true});
         //modbusClient.Disconnect();
         /*
         while (success == false)
         {
        	 try
        	 {
        		 modbusClient.Connect("127.0.0.1",502);
        		 boolean[] response = modbusClient.ReadCoils(2, 20);
        		 int[] responseint = modbusClient.ReadHoldingRegisters(0, 20);
        		 modbusClient.WriteSingleCoil(0, true);
        		 modbusClient.WriteSingleRegister(200, 456);
        		 modbusClient.WriteMultipleCoils(200, new boolean[]{true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true});
        		 modbusClient.WriteMultipleRegisters(300, new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});        
        		 for (int i = 0; i < response.length; i++)
        		 {
        			 System.out.println(response[i]);
        			 System.out.println(responseint[i]); 
        		 }
        		 success = true;  
        		 Thread.sleep(1000);
        	 }
        	 catch (Exception e)
        	 {
        		 e.printStackTrace();
        	 }
        	 finally
        	 {
        		 modbusClient.Disconnect();
        	 }
        	 
         }
         */
     }
}
