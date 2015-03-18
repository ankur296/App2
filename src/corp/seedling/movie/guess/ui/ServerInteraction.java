package corp.seedling.movie.guess.ui;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ServerInteraction extends Thread{

	private String url;
	
	protected ServerInteraction(String url){
		
		this.url = url;
	}
	
	@Override
	public void run() {
		super.run();
	  DefaultHttpClient client = new DefaultHttpClient(); 
	 
	    
	         HttpGet getRequest = new HttpGet(url);
	         
	       try {
	          HttpResponse getResponse = client.execute(getRequest);
	          final int statusCode = getResponse.getStatusLine().getStatusCode();
	         
	          if (statusCode != HttpStatus.SC_OK) {
	             System.out.println("Error " + statusCode + " for URL " + url);
	          }
	          
	          HttpEntity getResponseEntity = getResponse.getEntity();
	          
	          if (getResponseEntity != null) {
//	             return EntityUtils.toString(getResponseEntity);
	          }
	       }
	       catch (IOException e) {
	          getRequest.abort();
	          System.out.println("Error for URL " + url);
	       }
//	       return null;
	    }

}