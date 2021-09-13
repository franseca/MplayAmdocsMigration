package com.tecnotree.mplayAmdocs.json;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonProvisioning_MQ_AMAZON_MPLAY {

	String json;
	Map<String, String> inputMap = new HashMap<String, String>();
	
	public JsonProvisioning_MQ_AMAZON_MPLAY(){
		
		json = new String("{"
	    		+ "  \"MovistarPlayRequest\": {"
	    		+ "    \"ItemProperty\": ["
	    		+ "      {"
	    		+ "        \"value\": $0,"
	    		+ "        \"key\": \"MSISDN\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $1,"
	    		+ "        \"key\": \"Product\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $2,"
	    		+ "        \"key\": \"Plan\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $3,"
	    		+ "        \"key\": \"orderActionType\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $4,"
	    		+ "        \"key\": \"DocumentType\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $5,"
	    		+ "        \"key\": \"DocumentId\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $6,"
	    		+ "        \"key\": \"email\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $7,"
	    		+ "        \"key\": \"IMSI\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $8,"
	    		+ "        \"key\": \"customerType\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $9,"
	    		+ "        \"key\": \"receptionDate\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $10,"
	    		+ "        \"key\": \"reasonId\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $11,"
	    		+ "        \"key\": \"serviceId\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $12,"
	    		+ "        \"key\": \"subscriberId\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $13,"
	    		+ "        \"key\": \"system\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $14,"
	    		+ "        \"key\": \"serviceName\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $15,"
	    		+ "        \"key\": \"PS\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $16,"
	    		+ "        \"key\": \"access\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $17,"
	    		+ "        \"key\": \"isPlanChanged\""
	    		+ "      },"
	    		+ "      {"
	    		+ "        \"value\": $18,"
	    		+ "        \"key\": \"orderId\""
	    		+ "      }"
	    		+ "    ]"
	    		+ "  }"
	    		+ "}");
		
		
	}
	
	public String getPayload(String[] record) {
		
		for(int i = 0; i < record.length; i++) {
			
			//System.out.println("\\$"+String.valueOf(i));
			//System.out.println("\""+record[i]+"\"");
			
			json = json.replace("$"+String.valueOf(i), "\""+record[i]+"\"");
			
			System.out.println("json->" + json);
		}
		
		
		
		
		return "";
	}
	
    
}
