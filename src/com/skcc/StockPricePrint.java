package com.skcc;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.fasterxml.jackson.databind.JsonNode;



public class StockPricePrint {
	
	public static void main(String[] args) {
		//SSL 처리
		try {
			ConnectionUtils.ignoreSSL();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		HashMap<String, String> codeMap = new HashMap<>();
		codeMap.put("cd", "종목코드");
		codeMap.put("nm", "종목명");
		codeMap.put("nv", "현재가");
		codeMap.put("cv", "전일비");
		codeMap.put("cr", "등락률");
		codeMap.put("rf", "rf");
		codeMap.put("mks", "시가총액_억");
		codeMap.put("aa", "거래대금_백만");
		
		File dir = new File("files");
		File files[] = dir.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			String filePath = files[i].getPath();
			String extension = filePath.split("\\.")[1];
			if(extension.equals("json")) {
				JsonNode json = null;
				try {
					json = JsonUtils.getStockInfo(filePath, codeMap);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(json.toPrettyString());
			} else if(extension.equals("xml")) {
				Document xml = null;
				try {
					xml = XmlUtils.getStockInfo(filePath, codeMap);
					new XMLOutputter(Format.getPrettyFormat()).output(xml, System.out);
				} catch (JDOMException | IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("other extension");
			}
		}
	}
	
}
