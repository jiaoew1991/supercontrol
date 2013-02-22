package com.jiaoew.remotecontroler.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class HttpRequestHelper {

	private String requestUrl;

	public void setRequestUri(String requestUri) {
		this.requestUrl = requestUri;
	}
	public String postJsonResult(List<? extends NameValuePair> data) {
		StringBuffer rst = new StringBuffer();
		HttpPost post = new HttpPost(requestUrl);
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(data);
			post.setEntity(httpEntity);
			
			HttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);	
			HttpResponse response = httpClient.execute(post);
			
			HttpEntity responeEntity = response.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(responeEntity.getContent()));
			String line = null;
			while ((line = br.readLine()) != null) {
				rst.append(line + '\n');
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//			URL url = new URL(requestUrl);
//			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//			urlConnection.setDoInput(true);
//			urlConnection.setDoOutput(true);
//			urlConnection.setRequestMethod("POST");
//			urlConnection.setRequestProperty("Charset", "utf-8");
//			
//			urlConnection.connect();
		return rst.toString();
	}
}
