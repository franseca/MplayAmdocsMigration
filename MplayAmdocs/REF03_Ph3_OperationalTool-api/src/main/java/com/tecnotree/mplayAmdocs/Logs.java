package com.tecnotree.mplayAmdocs;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.regex.Pattern;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.tecnotree.mplayAmdocs.json.JsonProvisioning_MQ_AMAZON_MPLAY;
import com.tecnotree.tools.Tn3ElasticSearch;
import com.tecnotree.tools.Tn3Logger;

public class Logs {
	
	//CONSTANTES
	private static String PROP_FILE_NAME = "configurationLogs.properties";
	
	//VARIABLES
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String log_outputDirectory = "";
	private static Tn3Logger logger = null;
	private static String ipServerElasticSearch = "", ipServerElasticSearchParam = "", schemaElasticSearch  = "", indexElasticSearch = "", dateFrom = "", dateTo = "", ipWfi = "";
	private static int portServerElasticSearch = 0, sizeHitsElasticSearch = 0, responseCodeQuery = 500, threadPool = 1;
	private static RestHighLevelClient client = null;
	private static Tn3ElasticSearch tn3ElasticSearch = null;
	private static String pattern1 = "^(\\d{4})(\\/|-)(0[1-9]|1[0-2])\\2([0-2][0-9]|3[0-1])(T)(0[0-9]|1[0-9]|2[0-3])(:)([0-5][0-9])(:)([0-5][0-9])$";
	
    public static void main( String[] args ) throws InterruptedException, ExecutionException {
    	//OBTENGO LOS PARAMETROS
    	if(args.length == 0) {//SI NO SE PASARON PARAMETROS
    		System.out.println(dateFormat.format(new Date()) + " - No se envio ningun parametro.");
    		 
    	}else {
   			
    		//IP DEL SERVIDOR ELASTICSEARCH
    		dateFrom = args[0];
    		dateTo = args[1];
    		ipServerElasticSearchParam = args[2];
    		ipWfi = args[3];
    		
    		//VALIDO QUE LA FECHA DESDE SEA DEL FORMATO CORRECTO
	  		if(!Pattern.matches(pattern1, dateFrom)) {
	  			System.out.println(dateFormat.format(new Date()) + " - The parameter dateFrom don't have the correct format. The format must be: YYYY-MM-ddTHH:MM:ss (Ex.:2021-07-21T05:00:00).");
	  			System.out.println(dateFormat.format(new Date()) + " - Finished Logs process.");
    			System.exit(0);
	  		}
	  		
	  		//VALIDO SI LA FECHA DESDE ES VALIDA
	  		try {
	  			DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		  		formatoFecha.setLenient(false);
				formatoFecha.parse(dateFrom);
			} catch (ParseException e) {
				System.out.println(dateFormat.format(new Date()) + " - Exception found - " + e.getMessage());
  		  		System.out.println(dateFormat.format(new Date()) + " - Finished Logs process.");
  		  		return;
			}
		  		  		  	
	  		//VALIDO QUE LA FECHA HASTA SEA DEL FORMATO CORRECTO
	  		if(!Pattern.matches(pattern1, dateTo)) {
	  			System.out.println(dateFormat.format(new Date()) + " - The parameter dateTo don't have the correct format. The format must be: YYYY-MM-ddTHH:MM:ss (Ex.:2021-07-21T23:00:00).");
	  			System.out.println(dateFormat.format(new Date()) + " - Finished Logs process.");
    			System.exit(0);
	  		}
    		  
  			//VALIDO SI LA FECHA HASTA ES VALIDA
  			try {
	  			DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		  		formatoFecha.setLenient(false);
				formatoFecha.parse(dateTo);
			} catch (ParseException e) {
				System.out.println(dateFormat.format(new Date()) + " - Exception found - " + e.getMessage());
  		  		System.out.println(dateFormat.format(new Date()) + " - Finished Logs process.");
  		  		return;
			}
		  	
	  		
    		try {
    			//CARGO EL ARCHIVO DE PROPIEDADES
    			loadProperties(PROP_FILE_NAME);
		  		
    		}catch (IOException|SecurityException|NullPointerException|NumberFormatException e) {
    			System.out.println(dateFormat.format(new Date()) + " - Exception found - " + e.getMessage());
    			System.out.println(dateFormat.format(new Date()) + " - Finished Logs process.");
    			System.exit(0);
    		}
			
    		try {
	  				
    			//CREO EL DIRECTORIO Y EL ARCHIVO DE LOGS
    			logger = new Tn3Logger("REF03_Ph3_OperationalTool-api", log_outputDirectory);
		  		
    			logger.info("Command executed: Logs dateFrom:"+ args[0] + " dateTo:" + args[1]);
    			System.out.println(dateFormat.format(new Date()) + " - Command executed: Logs dateFrom:"+ args[0] + " dateTo:" + args[1]);
		  						  		
    			logger.info("Properties file load.");
    			System.out.println(dateFormat.format(new Date()) + " - Starting Logs process...");
    			logger.info("Starting Logs process...");
    			
    			//CONEXION A ELASTICSEARCH
    			System.out.println(dateFormat.format(new Date()) + " - Connecting to ElasticSearch...");
    			logger.info("Connecting to ElasticSearch...");
    			    			
    			tn3ElasticSearch = new Tn3ElasticSearch((ipServerElasticSearchParam == null) ? ipServerElasticSearch : ipServerElasticSearchParam, portServerElasticSearch, schemaElasticSearch, indexElasticSearch);
    			tn3ElasticSearch.startConnection();
    			client = tn3ElasticSearch.getConn();
    			
    			System.out.println(dateFormat.format(new Date()) + " - Connected in ElasticSearch...");
    			logger.info("Connected in ElasticSearch...");
    			
    			//OBTENGO LOS LOSG DE ELASTICSEARCH Y ENVIO EL PAYLOAD AL WORKFLOWINITIATOR
    			getLogsProvisioning_MQ_AMAZON_MPLAY();
				    			
    			System.out.println(dateFormat.format(new Date()) + " - Finished Logs process.");
    			logger.info("Finished Logs process.");
    			System.exit(0);
  			
    		}catch (/*InterruptedException |*/ IOException|SecurityException|NullPointerException|NumberFormatException e) {
    			
    			System.out.println(dateFormat.format(new Date()) + " - Exception found - " + e.getMessage());
    			System.out.println(dateFormat.format(new Date()) + " - Finished Logs process.");
    			logger.severe(e);
    			//e.printStackTrace();
    			System.exit(1);
    		}
   		
    	}
    }
    	 
    /**
     * Method loads the properties.
     * 
	 * @param propFileName
	 * @throws IOException
	 */
    public static void loadProperties(String propFileName) throws IOException {
		
    	Properties prop = new Properties();
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream(propFileName);
  		prop.load(inputStream);
		 	  		
  		//PROPIEDADES DEL LOS LOGS
  		log_outputDirectory = prop.getProperty("log.outputDirectory");
  				
  		//PROPIEDADES DE ELASTICSEARCH
  		ipServerElasticSearch = prop.getProperty("elasticSearch.ipServer");
  		portServerElasticSearch = Integer.parseInt(prop.getProperty("elasticSearch.portServer"));
  		schemaElasticSearch = prop.getProperty("elasticSearch.schema"); 
  		indexElasticSearch = prop.getProperty("elasticSearch.index");
  		sizeHitsElasticSearch = Integer.parseInt(prop.getProperty("elasticSearch.sizeHits"));
  		responseCodeQuery = Integer.parseInt(prop.getProperty("elasticSearch.response.code")); 
  		
  		//PROPIEDADES DEL HILO
  		threadPool = Integer.parseInt(prop.getProperty("thread.pool")); 
  		
    }
    
    /**
     * Method gets Logs from Kibana about Provisioning_MQ_AMAZON_MPLAY flow
     * 
     * @param fileName
     * @throws IOException
	     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    public static void getLogsProvisioning_MQ_AMAZON_MPLAY() throws IOException, InterruptedException, ExecutionException {
    	
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		qb
		 .must(QueryBuilders.matchQuery("ResponseJSON.code",responseCodeQuery))//EQUAL
		 .must(QueryBuilders.rangeQuery("@timestamp").from(dateFrom).to(dateTo));//BETWEEN
		
		searchSourceBuilder.query(qb);
		searchSourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.ASC));//ORDER BY
		searchSourceBuilder.timeout(new TimeValue(30 * 1000));//SEGUNDOS
		
		System.out.println(dateFormat.format(new Date()) + " - Query executed:" + searchSourceBuilder.toString());
		logger.info("Query executed:" + searchSourceBuilder.toString());
		
    	SearchRequest searchRequest = new SearchRequest();
    	searchRequest.indices(tn3ElasticSearch.getIndex());
    	searchRequest.source(searchSourceBuilder.size(sizeHitsElasticSearch));
    	
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		
		if (searchResponse.getHits().getTotalHits() > 0) {
			
			//SETEO POOL DE HILOS
	    	ExecutorService executorService = Executors.newFixedThreadPool(threadPool);
			
		    System.out.println("getTotalHits(): " + searchResponse.getHits().getTotalHits());
		   
			SearchHits hits = searchResponse.getHits();
			SearchHit hit[] = hits.getHits();
			
			//System.out.println("hit.length: " +  hit.length);
			
			for (int i = 0; i < hit.length; i++) {
				
				System.out.println("======================== Record No. " + (i+1) + " ======================== ");
				logger.info("======================== Record No. " + (i+1) + " ========================= ");
				
				System.out.println("_index: " + hit[i].getIndex());
				logger.info("_index: " + hit[i].getIndex());
				
				System.out.println("@timestamp: " + hit[i].getSourceAsMap().get("@timestamp"));
				logger.info("@timestamp: " + hit[i].getSourceAsMap().get("@timestamp"));
				
				System.out.println("_source: " + hit[i].getSourceAsMap().toString());
				logger.info("_source: " + hit[i].getSourceAsMap().toString());
				
				//OBTENGO PAYLOAD PARA ENVIAR AL WORKFLOWINITIATOR
				String payload = tn3ElasticSearch.getDataRequestJsonMqBulkData(hit[i]);
				
				System.out.println("Payload sent: " + payload);
				logger.info("Payload sent: " + payload);
								
				//ENVIO AL WORKFLOWINITIATOR EL PAYLOAD OBTENIDO DEL LOG EN ELASTICSEARCH
				FutureTask<String> task = (FutureTask<String>) executorService.submit (new CallWorkFlowInitiator(i, payload, ipWfi));
				String response = (String) task.get();
				
				System.out.println("Response: " + response);
				logger.info("Response: " + response);
				
				System.out.println("===============================================================");
				logger.info("===============================================================");
				
			}//FIN DE for (int i = 0; i < hit.length; i++) {
			
			System.out.println(dateFormat.format(new Date()) + " - Total records found:" + searchResponse.getHits().getTotalHits());
			logger.info("Total records found:" + searchResponse.getHits().getTotalHits());
			
			executorService.shutdown ();
			
			System.out.println(dateFormat.format(new Date()) + " - Tasks finished.");
			logger.info("Tasks finished.");
						
			tn3ElasticSearch.finishConnection();
			
			System.out.println(dateFormat.format(new Date()) + " - Connection to ElasticSearch closed.");
			logger.info("Connection to ElasticSearch closed.");
				
						
		} else {
			
			System.out.println(dateFormat.format(new Date()) + " - No results matching the criteria.");
			logger.info("No results matching the criteria.");
			
		}//FIN DE if (searchResponse.getHits().getTotalHits() > 0) {
			
    }	
        
    public static void callWorkFlowIniator() throws IOException, InterruptedException {
    	/* SE NECESITA JAVA 11
    	 HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    	
    	// form parameters
    	String json = new StringBuilder()
                .append("{")
                .append("\"name\":\"mkyong\",")
                .append("\"notes\":\"hello\"")
                .append("}").toString();

    	HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://httpbin.org/post"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .build();

    	HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print status code
        System.out.println(response.statusCode());

        // print response body
        System.out.println("response.body()->" + response.body());*/
    	List<String[]> allElements = new ArrayList<String[]>();
    	//allElements = getLogsProvisioning_MQ_AMAZON_MPLAY();
    	
    	///////////////////////////////////////////////
    	String[] content = new String[]{"","","","CH","DNI","72932870","","","C","2021-06-17T17:40:02","CHSUB","[,9191919191]","49998584","ALDM","Fixed_Voice_Main","[Fixed_Voice_Plan|Fixed_Voice_Plan,TEST_MQ]","WRLN","false","1062666"};
    	allElements.add(content);
    	
    	////////////////////////////////////////////////////////
    	
    	if(allElements.size() != 0) {
    		
    		for(int i = 0; i < allElements.size(); i++) {
    			String[] record = (String[])allElements.get(i);
    			
	    		JsonProvisioning_MQ_AMAZON_MPLAY jsonP = new JsonProvisioning_MQ_AMAZON_MPLAY();
	    		System.out.println(jsonP.getPayload(record));
	        	
	            //String result = sendPOST(ipWFI+"/DAP/workflow/async/Provisioning_MQ_AMAZON_MPLAY",json);
	            //System.out.println(result);
    		}
           
    	}else {
    	
    		System.out.println(dateFormat.format(new Date()) + " - No results matching the criteria.");
			logger.info("No results matching the criteria.");
    	}

    }
    
    
    	 
}
