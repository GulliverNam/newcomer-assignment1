package com.skcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class JsonUtils {
	
	static String baseUrl = "https://m.stock.naver.com/api/json/search/searchListJson.nhn?keyword=";
	
	public static JsonNode getResponse(String stockNumber) throws IOException {
		URL url = new URL(baseUrl + stockNumber);
		
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		
		Charset charset = Charset.forName("UTF-8");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),charset));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        
		return new ObjectMapper().readValue(response.toString(), JsonNode.class).get("result").get("d").get(0);
	}

	public static JsonNode getStockInfo(String jsonPath, HashMap<String, String> codeMap) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readValue(new File(jsonPath), JsonNode.class);
		JsonNode items = json.get("items");
		
		for(JsonNode item : items) {
		    JsonNode stockInfo = getResponse(item.get("item").toString().replaceAll("\"", ""));
		    for(String key:codeMap.keySet()) {
		    	((ObjectNode)item).put(codeMap.get(key), stockInfo.get(key).toString().replaceAll("\"", ""));
		    }
		}
		return json;
	}
}
