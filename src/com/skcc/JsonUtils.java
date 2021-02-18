package com.skcc;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;


public class JsonUtils {

	public static String getStr(HashMap<String, JsonNode> map, String key) {
		if( map ==null) return "";
		
		JsonNode  obj = map.get(key);
		if( obj ==null) return "";
		return key;
		
	}
}
