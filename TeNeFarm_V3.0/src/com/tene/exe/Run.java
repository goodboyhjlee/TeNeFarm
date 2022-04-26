package com.tene.exe;




import java.io.File;
import java.io.FileReader;

import org.json.JSONArray;
import org.json.JSONObject;
import com.tene.constant.HeaderDefine;
import com.tene.controller.Control;
import com.tene.database.DBOpp;

public class Run  {
	
	public  JSONObject inforData = null;
	public  static byte con_cde = 0;
	public  static String usr_id = null;
	public  static String farm_cde = null;
	
	private JSONObject control_info = null;
	private		JSONObject fore_info = null;
	private JSONObject sensornode_ws = null;
	
	
	
	
	
	
	
	
	public static String readFromFile(String urlString) throws Exception {
		FileReader reader = null;
	    try {
	        reader = new FileReader(urlString);
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024*2];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
	
	
	
	
	
	public void Start() {
		
		try {
			new Control(control_info, fore_info,sensornode_ws);
			
			//System.out.println("sssss");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			
			try {
				String default_json_path =  "/home/pi/tene/env/default.json";
				String ddd = readFromFile(default_json_path);				
				JSONObject retObject = new JSONObject(ddd);
				JSONArray info_l = retObject.getJSONArray("data");
				JSONObject control_info = info_l.getJSONObject(0);
				JSONObject fore_info = retObject.getJSONObject("fore");
				new Control(control_info, fore_info,sensornode_ws);
				
			} catch (Exception ee) {
				
			}
		}
	}
	
	public Run() {
		HeaderDefine.init();
		String ep_envjson_path =  "d:\\workspace_gthings\\TeNeFarm_Local_V4.0\\ep_env.json";
		String os = System.getProperty("os.name").toLowerCase();
		if (os.equals("linux")) {
			ep_envjson_path = "/home/pi/tene/env/ep_env.json";
		}
		
		JSONObject local_jsonObject;
		
		try {
			local_jsonObject = new JSONObject(readFromFile(ep_envjson_path));
			con_cde =  (byte)local_jsonObject.getInt("con_cde");
			usr_id = (String) local_jsonObject.get("usr_id");
			farm_cde = (String) local_jsonObject.get("farmCode");	
			DBOpp.usr_id = usr_id;	
			JSONObject retObject_con = DBOpp.readFromUrl("getfarminfo_control?" + "farm_cde=" + farm_cde + "&con_cde=" + con_cde);			
			JSONArray info_con = retObject_con.getJSONArray("data");
			control_info = info_con.getJSONObject(0);			
			fore_info = retObject_con.getJSONObject("fore");
			
			/*
			File file_con = new File("/home/pi/tene/env/default.json");
			try {
		      FileWriter fw = new FileWriter(file_con);
		      fw.write(retObject_con.toString());
		      fw.close();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    */
			
			boolean isws = control_info.getString("isws").equals("Y") ? true : false;			
			if (isws) {
				try {	
					JSONObject retObject_ws = DBOpp.readFromUrl("getfarminfo_forecaststation?" + "farm_cde=" + Run.farm_cde );					
					
					JSONArray info_ws = retObject_ws.getJSONArray("data");
					sensornode_ws = info_ws.getJSONObject(0);
					
					File file_ws = new File("/home/pi/tene/env/ws.json");
					try {
				      //FileWriter fw = new FileWriter(file_ws);
				      //fw.write(retObject_ws.toString());
				      //fw.close();
				    } catch (Exception e) {
				      e.printStackTrace();
				    }
				
				} catch (Exception e) {
					e.printStackTrace();
				}
 			}
			
			
			
			
			
			
			Start();
			
		} catch (Exception e1) {
			e1.printStackTrace();
			
		}
	}
	
}
