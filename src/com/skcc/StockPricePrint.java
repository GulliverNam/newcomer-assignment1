package com.skcc;

import java.io.BufferedReader;
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
import java.util.Set;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;



public class StockPricePrint {
	
	public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ClientProtocolException, IOException {
		// Json 처리추가
		SAXBuilder saxBuilder = new SAXBuilder();
		Document document = null;
		
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
        SSLContext.setDefault(ctx);
		try {
			document = saxBuilder.build(".\\xml\\stockNumbers.xml");
			Element root = document.getRootElement();
			List<Element> children = root.getChildren();
			String baseUrl = "https://m.stock.naver.com/api/json/search/searchListJson.nhn?keyword=";
			URL url = null;
			for(Element child : children) {
				System.out.println(baseUrl+child.getText());
				url = new URL(baseUrl+child.getText());
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}
				});
				System.out.println(conn.getResponseCode());
				
				Charset charset = Charset.forName("UTF-8");
		        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),charset));
		        String inputLine;
		        StringBuffer response = new StringBuffer();
		        while ((inputLine = in.readLine()) != null) {
		            response.append(inputLine);
		        }
		        in.close();
		        ObjectMapper mapper = new ObjectMapper();
		        HashMap<String, JsonNode> map = mapper.readValue(response.toString(), HashMap.class);
		        
		        JsonUtils.getStr(map, "result");
		        
		        System.out.println();
				conn.disconnect();
				// Response -> XML&JSON
			}
		}catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
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
