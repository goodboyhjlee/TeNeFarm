package com.tene.forecast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tene.database.DBOpp;
import com.tene.utils.GetCurrentTime;

public class Forecast {
	private String url_forecast = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib";
	//private String url_sun = "http://apis.data.go.kr/B090041/openapi/service/RiseSetInfoService/getLCRiseSetInfo";
	private String apikey = null;
	
	//private double lon = 0;
	//private double lat = 0;
	private int nx = 0;
	private int ny = 0;
	
	private float windDir = 0;
	private float windSpeed = 0;
	private float temp = 0;
	private float hum = 0;
	
	/*
	private int sunRise_h = 0;
	private int sunRise_m = 0;
	
	private int sunSet_h = 0;
	private int sunSet_m = 0;
	
	private byte sunRiszTerm = 24;
	private byte sunRiszInc = 25;
	*/
	
	private Timer timerForcast = new Timer();	
	private TimerTask taskForcast = new TimerTask() {
		@Override
		public void run() {		
			try {	
				//getForcastData();
				/*
				getSunriseData();
				if (sunRiszTerm < sunRiszInc) {
					sunRiszInc = 0;
				} else {
					sunRiszInc++;
				}
				*/
			} catch (Exception e1) {
				//e1.printStackTrace();
			}
		}       
   };
   
   
	
	public Forecast( String apikey, int nx, int ny) {
		try {
			
		//	DBOpp.readFromUrl("/getdatalist?id=forecastlocation&usr_id=" + usr_id);
			
			//String dbUrl = DBOpp.DB_URL + "/getdatalist?id=forecastlocation&usr_id=" + usr_id;
			//JSONObject retObject = readFromUrl(dbUrl);
			//JSONArray aa = retObject.getJSONArray("data_list");
			//JSONObject rec = (JSONObject) aa.get(0);
			/*
			ServiceKey = rec.getString("apikey");
			lon = rec.getDouble("lon");
			lat = rec.getDouble("lat");
			nx = rec.getInt("nx");
			ny = rec.getInt("ny");
			*/
			
			this.apikey = apikey;
			//lon = rec.getDouble("lon");
			//lat = rec.getDouble("lat");
			this.nx = nx;
			this.ny = ny;
			
			timerForcast.schedule(taskForcast, 0, 1000 * 60 * 10);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	
	public void getForcastData() {
		String base_date = GetCurrentTime.getCurForeCastDate();
		String base_time = GetCurrentTime.getCurForeCastTime();
		
				
		String forecastUrl = url_forecast + "?ServiceKey=" + apikey + "&base_date=" + base_date + 
				"&base_time=" + base_time + 
				"&nx=" + nx + 
				"&ny=" + ny +
				"&_type=json";
		
		//System.out.println(forecastUrl);
		
		try {
			JSONObject retObject = readFromUrl(forecastUrl);
			//System.out.println(retObject);
			JSONArray repData = retObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");
			
			int cnt = repData.length();
			for (int i=0;i<cnt; i++) {
				JSONObject data = repData.getJSONObject(i);
				String category = data.getString("category");
				if (category.equals("VEC")) {
					windDir = (float) data.getDouble("obsrValue");
					//System.out.println("windDir " + windDir);
				} else if (category.equals("WSD")) {
					windSpeed = (float) data.getDouble("obsrValue");
					//System.out.println("getWindSpeed " + windSpeed);
				} else if (category.equals("T1H")) {
					temp = data.getInt("obsrValue");
				} else if (category.equals("REH")) {
					hum = data.getInt("obsrValue");
				}  
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/*
	public void getSunriseData() {
		try {
			Calendar[] sunriseSunset = com.tene.forecast.SunriseSunset.getSunriseSunset(Calendar.getInstance(), lat, lon);
			sunRise_h = sunriseSunset[0].getTime().getHours();
    		sunRise_m = sunriseSunset[0].getTime().getMinutes();
    		sunSet_h = sunriseSunset[1].getTime().getHours();
    		sunSet_m = sunriseSunset[1].getTime().getMinutes();
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	*/
	
	
	private JSONObject readFromUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 
	        JSONObject retObject = new JSONObject(new String(buffer.toString().getBytes(),"EUC-KR"));
	        return retObject;
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
	



	public float getWindDir() {
		return windDir;
	}



	public float getWindSpeed() {
		return windSpeed;
	}



	public float getTemp() {
		return temp;
	}



	public float getHum() {
		return hum;
	}


/*
	public int getSunRise_h() {
		return sunRise_h;
	}



	public int getSunRise_m() {
		return sunRise_m;
	}



	public int getSunSet_h() {
		return sunSet_h;
	}



	public int getSunSet_m() {
		return sunSet_m;
	}
	*/

}
