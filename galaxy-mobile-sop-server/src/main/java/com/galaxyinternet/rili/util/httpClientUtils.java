package com.galaxyinternet.rili.util;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class httpClientUtils {
	
	
	
	final static String CONTENT_TYPE_TEXT_JSON = "text/json";
	/** 
     * 模拟请求 
     *  
     * @param url               资源地址 
     * @param map           参数列表 
     * @param encoding  编码 
     * @param handler       结果处理类 
     * @return 
     */  
    public static String send(String url, Map<String,Object> map)  {  
  
    	 
    	 CloseableHttpClient client =HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "application/json;charset=UTF-8");
		request.addHeader("accept", "application/json");  
		
		StringEntity se = null;
		String content  = null;
		try{
			String temp = map2String(map);
			
			se = new StringEntity(temp,"utf-8");
			//se.setContentEncoding("UTF-8");
			se.setContentType(CONTENT_TYPE_TEXT_JSON);

			request.setEntity(se);
      
			HttpResponse responses = client.execute(request);
			content = EntityUtils.toString(responses.getEntity(),"utf-8");

        }catch(Exception e){
			e.printStackTrace();
		}
		return content;
	}


    /**
	 * Map 转 Json型字符串
	 * @param map
	 * @return 
	 */
	public static String map2String(Map<String,? extends Object> map){
		return object2JSONString(map);
	}
	
	private static String object2JSONString(Object object){
		String jsonString = null;
		try{
			jsonString = new ObjectMapper().writeValueAsString(object);
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonString;
	}
	

	
	
	
	
	
	
}
