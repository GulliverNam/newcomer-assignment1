package com.skcc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.fasterxml.jackson.databind.JsonNode;

public class XmlUtils {

	public static Document getStockInfo(String xmlPath, HashMap<String, String> codeMap) throws JDOMException, IOException {
		SAXBuilder saxBuilder = new SAXBuilder();
		Document xml = saxBuilder.build(xmlPath);
		List<Element> xmlInput = xml.getRootElement().getChildren();
		
		for(Element item : xmlInput) {
	        JsonNode stockInfo = JsonUtils.getResponse(item.getAttributeValue("code"));
	        for(String key:codeMap.keySet()) {
	        	item.setAttribute(codeMap.get(key), stockInfo.get(key).toString().replaceAll("\"", ""));
	        }
		}
		return xml;
	}

}
