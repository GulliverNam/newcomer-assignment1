package com.skcc;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;


public class JsonUtils {

	public static String getStr(HashMap<String, ArrayList> map, String key) {
		if( map ==null) return "";
		System.out.println(map.get(key));
		Object obj = map.get(key);
		if( obj ==null) return "";
		return obj.toString();
	}
}
