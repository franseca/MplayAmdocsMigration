package com.tecnotree.mplayAmdocs;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class CallWorkFlowInitiator implements Callable<String> {

	String payload;
	int hiloId;
	String ipWfi;
	
	CallWorkFlowInitiator(int hiloId, String payload, String ipWfi){
		this.payload = payload;
		this.hiloId = hiloId;
		this.ipWfi = ipWfi;
	}
	
	public String call() throws IOException { 
		String result = "";
		
		System.out.println("El hilo " + hiloId + " coge el pool " + Thread.currentThread().getName());
		
		result = sendPOST(ipWfi+"/DAP/workflow/async/Provisioning_MQ_AMAZON_MPLAY",payload); 
		
		System.out.println("El hilo " + hiloId + " deja el pool " + Thread.currentThread().getName());
		
		return result; 
	}
	
	
	private static String sendPOST(String url, String payload) throws IOException {

        String result = "";
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-type", "application/json; charset=utf-8");	
        post.addHeader("Originator", "REF03_Ph3_OperationalTool");
        post.addHeader("Accept", "application/json");
        
        // send a JSON data
        post.setEntity(new StringEntity(payload));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
        	
        	//System.out.println(response.getStatusLine().getStatusCode()); 
            result = EntityUtils.toString(response.getEntity());
        }

        return result;
    }

}
