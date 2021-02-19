package com.skcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.ClientProtocolException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;



public class StockPricePrint {
	
	public static void main(String[] args) throws Exception {
		// folder 데이터 -> 이름 / 확장자 -> util 처리 / json input/output xml input/output -> 각 Object
		
		// Json 입력처리
		ObjectMapper mapper = new ObjectMapper();
		
		// XML 입력처리
		SAXBuilder saxBuilder = new SAXBuilder();
		
		//SSL 처리
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
        SSLContext.setDefault(ctx);
        
		String baseUrl = "https://m.stock.naver.com/api/json/search/searchListJson.nhn?keyword=";
		URL url = null;
		
		HashMap<String, String> codeMap = new HashMap<>();
        codeMap.put("cd", "종목코드");
        codeMap.put("nm", "종목명");
        codeMap.put("nv", "현재가");
        codeMap.put("cv", "전일비");
        codeMap.put("cr", "등락률");
        codeMap.put("rf", "rf");
        codeMap.put("mks", "시가총액_억");
        codeMap.put("aa", "거래대금_백만");
		
		//json - 목록가져오기
        JsonNode jsonInput = mapper.readValue(new File(".\\files\\stockNumbers.json"), JsonNode.class);
		JsonNode items = jsonInput.get("items");
		
		for(JsonNode item : items) {
			System.out.println(baseUrl + item.get("item").toString().replaceAll("\"", ""));
			url = new URL(baseUrl + item.get("item").toString().replaceAll("\"", ""));
			
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
	        
	        JsonNode json = mapper.readValue(response.toString(), JsonNode.class).get("result").get("d").get(0);
	        for(String key:codeMap.keySet()) {
	        	((ObjectNode)item).put(codeMap.get(key), json.get(key).toString().replaceAll("\"", ""));
	        }
	        
			conn.disconnect();
		}
		System.out.println(jsonInput.toPrettyString());
		
		//XML - 목록가져오기
		Document document = saxBuilder.build(".\\files\\stockNumbers.xml");
		List<Element> xmlInput = document.getRootElement().getChildren();
		
		for(Element item : xmlInput) {
			System.out.println(baseUrl + item.getAttributeValue("code"));
			url = new URL(baseUrl + item.getAttributeValue("code"));
			
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
	        
	        JsonNode json = mapper.readValue(response.toString(), JsonNode.class).get("result").get("d").get(0);
	        
	        for(String key:codeMap.keySet()) {
	        	item.setAttribute(codeMap.get(key), json.get(key).toString().replaceAll("\"", ""));
	        }
	        
			conn.disconnect();
		}
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		xmlOutputter.output(document, System.out);
	}
	
	private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

}
