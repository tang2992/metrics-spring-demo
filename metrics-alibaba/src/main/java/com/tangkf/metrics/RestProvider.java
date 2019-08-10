package com.tangkf.metrics;

import java.net.URI;

import javax.inject.Inject;

import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.annotation.EnableTimer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

public class RestProvider {
	
	@Inject private HttpClient httpClient;
	@Inject private MetricRegistry registry;
	
	@EnableTimer(group = "test", key = "test.rest.get")
	public HttpResponse get(URI uri) {
		try {
			registry.counter(MetricRegistry.name(RestProvider.class, "counter")).inc();
			
			HttpGet httpget = new HttpGet(uri.toURL().toExternalForm());
			HttpResponse response = this.httpClient.execute(httpget);
			if (response.getStatusLine().getStatusCode() != 200) {
		    	throw new IllegalStateException("invalid response: " + 
				    response.getStatusLine().getStatusCode() + ": " + 
				   	response.getStatusLine().getReasonPhrase()
				);
		    }
		    
		    return response;
		}
		catch (Exception exception) {
			throw new IllegalStateException(exception);
		}
	}
}
