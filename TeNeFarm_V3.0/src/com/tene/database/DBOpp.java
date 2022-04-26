package com.tene.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

public class DBOpp {
	//public static String DB_URL = "http://tns.tenefarm.com/ict";
	public static String DB_URL = "http://125.139.220.78/ict";
	//public static String DB_URL = "http://192.168.0.101/ict";
	//public static String DB_URL = "http://tene-main.iptime.org/ict";
	public static String usr_id = null;
	
	public static void updateEach(Map<String,Object> map) {
		
		
		try {
			URL url = new URL(DB_URL +  map.get("url"));
			HttpURLConnection  conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST"); // POST로 연결
			conn.setUseCaches(false);
			if (map != null) { // 웹 서버로 보낼 매개변수가 있는 경우우
	            OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
	            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
	            bw.write(getPostString(map)); // 매개변수 전송
	            bw.flush();
	            bw.close();
	            os.close();
	        }		
			
	        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
	            String line;
	            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림
	            String response = "";
	            while ((line = br.readLine()) != null) // 서버의 응답을 읽어옴
	                response += line;
	           // System.out.println(response);
	        }
	        conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void update(Map<String,Object> map) {
		//System.out.println(map);
		String urlString = "/process/update";
		try {
			URL url = new URL(DB_URL +  urlString);
			HttpURLConnection  conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST"); // POST로 연결
			conn.setUseCaches(false);
			if (map != null) { // 웹 서버로 보낼 매개변수가 있는 경우우
	            OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
	            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
	            bw.write(getPostString(map)); // 매개변수 전송
	            bw.flush();
	            bw.close();
	            os.close();
	        }		
			
	        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
	            String line;
	            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림
	            String response = "";
	            while ((line = br.readLine()) != null) // 서버의 응답을 읽어옴
	                response += line;
	           // System.out.println(response);
	        }
	        conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void insert(Map<String,Object> map) {
		String urlString = "/process/insert";
		try {
			URL url = new URL(DB_URL +  urlString);
			HttpURLConnection  conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST"); // POST로 연결
			conn.setUseCaches(false);
			
			if (map != null) { // 웹 서버로 보낼 매개변수가 있는 경우우
	            OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
	            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
	            bw.write(getPostString(map)); // 매개변수 전송
	            bw.flush();
	            bw.close();
	            os.close();
	        }
	 
			 if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
		            String line;
		            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림
		            String response = "";
		            while ((line = br.readLine()) != null) // 서버의 응답을 읽어옴
		                response += line;
		        }
			 
	        conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	public static void updateFromUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(DB_URL + "/" + urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
	*/
	
	public static JSONObject readFromUrl(String urlString) throws Exception {
		//System.out.println(urlString);
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(DB_URL + "/" + urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8") );
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 
	        JSONObject retObject = new JSONObject(buffer.toString());
	        return retObject;
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
	
	public static void sendPushMsg(String params) {
		try {
			String urlString = "sendpushmessagev35?";
			URL url = new URL(DB_URL + "/" + urlString);
			HttpURLConnection  conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("GET"); // POST로 연결
			OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
			os.write(params.getBytes("UTF-8") ); // 매개변수 전송
            os.flush();
            os.close();
            
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
	            String line;
	            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림
	            String response = "";
	            while ((line = br.readLine()) != null) // 서버의 응답을 읽어옴
	                response += line;
	            //System.out.println(response);
	        }
            conn.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 매개변수를 URL에 붙이는 함수
	// 참고: http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post
	private static String getPostString(Map<String, Object> map) {
	    StringBuilder result = new StringBuilder();
	    boolean first = true; // 첫 번째 매개변수 여부
	 
	    for (Entry<String, Object> entry : map.entrySet()) {
	        if (first)
	            first = false;
	        else // 첫 번째 매개변수가 아닌 경우엔 앞에 &를 붙임
	            result.append("&");
	 
	        try { // UTF-8로 주소에 키와 값을 붙임
	            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
	            result.append("=");
	            if (entry.getValue() instanceof String) {
	            	result.append(URLEncoder.encode((String)entry.getValue(), "UTF-8"));
	            } else {
	            	result.append(entry.getValue());
	            }
	        } catch (UnsupportedEncodingException ue) {
	            ue.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return result.toString();
	}

}
