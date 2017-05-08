package com.galaxyinternet.rili.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Client {
	//private String url = "http://localhost:9999/authority_service/depart/getLeafDepartList";
	//private String url = "http://localhost:9999/authority_service/user/findUserByName";
	//private String url = "http://localhost:9999/authority_service/user/getUserById";
	//private String url = "http://localhost:9999/authority_service/user/getUsersByDepId";
	private String url = "http://10.8.232.205/authority_service/user/getUsersByDepId";
	
	final String CONTENT_TYPE_TEXT_JSON = "text/json";
	
	public Client() {
		
		
		 CloseableHttpClient client =HttpClientBuilder.create().build();
		
		
		
		final HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "application/json;charset=UTF-8");
		request.addHeader("accept", "application/json");  
		
		
		Map<String,Object> map = new HashMap<String,Object>();
		List<Integer> userList = new ArrayList<Integer>();
		userList.add(2);
		userList.add(3);
		userList.add(4);
		userList.add(5);
		userList.add(6);
		
		//map.put("userIds", object2JSONString(userList));
		
		//map.put("userName", "洪");
		//map.put("userId", 3);
		map.put("depId", 32);
		
		StringEntity se = null;
		try{
			String temp = map2String(map);
			
			se = new StringEntity(temp,"utf-8");
			//se.setContentEncoding("UTF-8");
			se.setContentType(CONTENT_TYPE_TEXT_JSON);
			
			  request.setEntity(se);

			HttpResponse responses = client.execute(request);
			String content = EntityUtils.toString(responses.getEntity(),"utf-8");
			
			 JsonParser parser=new JsonParser();  //创建JSON解析器
			 JsonObject object=(JsonObject) parser.parse(content);
            System.out.println(content);
            System.out.println(object.get("success"));

            
            if( object.get("success").getAsBoolean()){
			 JsonArray array=object.get("value").getAsJsonArray(); //得到为json的数组

	            for(int i=0;i<array.size();i++){
	                System.out.println("---------------");
	                JsonObject subObject=array.get(i).getAsJsonObject();
	
	                System.out.println("id="+subObject.get("depId").getAsInt());
	                System.out.println("name="+subObject.get("depName").getAsString());
	                
	            }
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		
      
        
	}
	
	/**
	 * Map 转 Json型字符串
	 * @param map
	 * @return 
	 */
	public String map2String(Map<String,? extends Object> map){
		return object2JSONString(map);
	}
	
	private String object2JSONString(Object object){
		String jsonString = null;
		try{
			jsonString = new ObjectMapper().writeValueAsString(object);
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static void main(String[] args){
		new Client();
	}

}
