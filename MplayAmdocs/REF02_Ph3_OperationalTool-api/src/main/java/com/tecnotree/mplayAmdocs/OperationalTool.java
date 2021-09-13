package com.tecnotree.mplayAmdocs;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import com.jcraft.jsch.JSchException;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.tecnotree.mplay.mplayAmdocs.csvRecord.CSVRecordGvpmConsolidated;
import com.tecnotree.mplay.mplayAmdocs.relational.GvpmConsolidated;
import com.tecnotree.tools.Tn3Logger;
import com.tecnotree.tools.Tn3MySQL;

public class OperationalTool 
{
	//private static final String CSV_FILE_PATH = "/input/";
			
	//CONSTANTES
	private static String PROP_FILE_NAME = "configurationOperationalTool.properties";
		
	//VARIABLES
	private static GvpmConsolidated gvpmConsolidated = null;
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Tn3MySQL tMySql = null;
	private static Tn3Logger logger = null;
	private static String db_ipServer = "", db_portServer = "", db_user = "", db_password = "", db_schema = "";
	private static String ssh_host = "", ssh_port = "", ssh_user = "", ssh_password = "";
	private static String file_inputDirectory = "";
	private static String log_outputDirectory = "";
			
	private static String withSsh = "";
	
	private static Connection conn = null;
	
	public static void main( String[] args ) throws SQLException, JSchException
    {
		//OBTENGO LOS PARAMETROS
  		if(args.length == 0) {//SI NO SE PASARON PARAMETROS
  			System.out.println(dateFormat.format(new Date()) + " - No se envio ningun parametro.");
  			
  		}else {
  			
  			//INPUT PARAMETERS
  			String fileName = args[0];
  			if(args.length == 2)//SI SE ENVIA PARAMETRO SSH
  				withSsh = args[1];
  		
  			//VALIDO QUE EL PARAMETRO SSH SEA N O S
	  		if(!withSsh.equals(""))
		  		if(!withSsh.equalsIgnoreCase("N") && !withSsh.equalsIgnoreCase("Y")) {
			  		System.out.println(dateFormat.format(new Date()) + " - The parameter SSH don't have the correct format. The formats must be: N or Y.");
			  		System.out.println(dateFormat.format(new Date()) + " - Finished OperationalTool.");
			  		return;
		  		}
	  				  		
			try {
				//CARGO EL ARCHIVO DE PROPIEDADES
		  		loadProperties(PROP_FILE_NAME);
		  		
			}catch (IOException|SecurityException|NullPointerException|NumberFormatException e) {
  		  		System.out.println(dateFormat.format(new Date()) + " - Exception found - " + e.getMessage());
  		  		System.out.println(dateFormat.format(new Date()) + " - Finished OperationalTool.");
  		  		return;
  		  	}
			
			try {
  				
		  		//CREO EL DIRECTORIO Y EL ARCHIVO DE LOGS
		  		logger = new Tn3Logger("OperationalTool", log_outputDirectory);
		  		
		  		logger.info("Command executed: OperationalTool "+ args[0] + " " + args[1]);
		  		System.out.println(dateFormat.format(new Date()) + " - Command executed: OperationalTool "+ args[0] + " " + args[1]);
		  						  		
		  		logger.info("Properties file load.");
		  		System.out.println(dateFormat.format(new Date()) + " - Starting operational tool...");
		  		logger.info("Starting operational tool...");
		  		
		  		//EJECUTA LOS UPDATES SEGUN LA DATA DEL ARCHIVO
		  		System.out.println(dateFormat.format(new Date()) + " - File has to process: " + file_inputDirectory+fileName);
  		  		logger.info("File has to process: " + file_inputDirectory+fileName);
		  		executeUpdateStatements(file_inputDirectory+fileName);
			
	            System.out.println(dateFormat.format(new Date()) + " - Finished operational tool.");
  		  		logger.info("Finished operational tool.");
  		  		System.exit(0);
  		  	
  			}catch (SecurityException | NullPointerException | NumberFormatException | IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
  				System.out.println(dateFormat.format(new Date()) + " - Exception found - " + e.getMessage());
    			System.out.println(dateFormat.format(new Date()) + " - Finished operational tool.");
  		  		logger.severe(e);
  		  		System.exit(1);
  		  		//e.printStackTrace();
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
		//InputStream inputStream = new FileInputStream(propFileName);
  		InputStream inputStream = ClassLoader.getSystemResourceAsStream(propFileName);
  		prop.load(inputStream);
		  			
  		//PROPIEDADES DE LA BASE DE DATOS
  		db_ipServer = prop.getProperty("db.ipServer");
  		db_portServer = prop.getProperty("db.portServer");
  		db_user = prop.getProperty("db.user");
  		db_password = prop.getProperty("db.password");
  		db_schema = prop.getProperty("db.schema");
  		
  		//PROPIEDADES DEL ARCHIVO
  		file_inputDirectory = prop.getProperty("file.inputDirectory");
  		
  		//PROPIEDADES DEL LOS LOGS
  		log_outputDirectory = prop.getProperty("log.outputDirectory");
  		
  		//PROPIEDADES DEL TUNEL SSH PARA CONEXION A LA BASE DE DATOS
  		ssh_host = prop.getProperty("ssh.host");
  		ssh_port = prop.getProperty("ssh.port");
  		ssh_user = prop.getProperty("ssh.user");
  		ssh_password = prop.getProperty("ssh.password");
  		  		
  		inputStream.close();		
	}
	
	public static void executeUpdateStatements(String fileName) throws IOException, NumberFormatException, JSchException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Reader reader = Files.newBufferedReader(Paths.get(fileName));
		
		CSVParser parser = new CSVParserBuilder()
			    .withSeparator(';')
			    .withIgnoreQuotations(true)
			    .build();
		
		CSVReader csvReader = new CSVReaderBuilder(reader)
			    .withSkipLines(0)
			    .withCSVParser(parser)
			    .build();
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		CsvToBean<CSVRecordGvpmConsolidated> csvToBean = new CsvToBeanBuilder(reader)
                .withType(CSVRecordGvpmConsolidated.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
		csvToBean.setCsvReader(csvReader);

		System.out.println(dateFormat.format(new Date()) + " - Reading records from file...");
        logger.info("Reading records from file...");
        
        Iterator<CSVRecordGvpmConsolidated> csvRecordIterator = csvToBean.iterator();
 
        gvpmConsolidated = new GvpmConsolidated("UPDATE &table SET &assignmentList WHERE &column = &value");
        System.out.println(dateFormat.format(new Date()) + " - Update template defined: " + gvpmConsolidated.getUpdateStatementTemplate());
        logger.info("Update template defined: " + gvpmConsolidated.getUpdateStatementTemplate());
        
        //ESTABLEZCO CONEXION A LA BASE DE DATOS
        tMySql = new Tn3MySQL();
    	
        if(withSsh.equalsIgnoreCase("Y")) {
	        System.out.println(dateFormat.format(new Date()) + " - Doing SSH tunnel to connect toward database...");
	        logger.info("Doing SSH tunnel to connect toward database...");
	      		
	        tMySql.doSshTunnel(ssh_user, ssh_password, ssh_host, Integer.parseInt(ssh_port), db_ipServer, Integer.parseInt(db_portServer), Integer.parseInt(db_portServer));
	    }
	    
        System.out.println(dateFormat.format(new Date()) + " - Connecting to database...");
        logger.info("Connecting to database...");	
        
        conn = tMySql.startConnection(db_ipServer, db_portServer, db_schema, db_user, db_password);
        
        int totalRecordsUpdated = 0;
        
        //RECORRO CADA LINEA DEL ARCHIVO, ARMO EL UPDATE Y ACTUALIZO EN LA BASE
        while (csvRecordIterator.hasNext()) {
        	
        	CSVRecordGvpmConsolidated csvRecord = csvRecordIterator.next();
            
        	//OBTENGO EL UPDATE SEGUN EL TEMPLATE
        	String updateStatement = getUpdate("GVPM_CONSOLIDATED", csvRecord, csvRecord.getConsolidated_id());
        	System.out.println(dateFormat.format(new Date()) + " - Update statement got: " + updateStatement);
            logger.info("Update statement got: " + updateStatement);
            
            //EJECUTO EL UPDATE
            PreparedStatement preparedStmt = conn.prepareStatement(updateStatement);
            preparedStmt.executeUpdate();
            
            System.out.println(dateFormat.format(new Date()) + " - Update statement executed: " + updateStatement);
            logger.info("Update statement executed: " + updateStatement);	
            
            totalRecordsUpdated++;
        	
        }
        
        //MUESTRO TOTALES
        System.out.println(dateFormat.format(new Date()) + " - Total records updated: " + totalRecordsUpdated);
        logger.info("Total records updated: " + totalRecordsUpdated);	
      	      		
        //CIERRO CONEXION CON LA BASE DE DATOS
        tMySql.finalizarConexion();
        System.out.println(dateFormat.format(new Date()) + " - Connection to database closed.");
        logger.info("Connection to database closed.");	
      		
        if(withSsh.equalsIgnoreCase("Y")) {
        	System.out.println(dateFormat.format(new Date()) + " - SSH tunnel toward database closed.");
        	logger.info("SSH tunnel toward database  closed.");	
        	tMySql.closeSshTunnel();
        }
	}
	
	
	/**
	 * Method gets UPDATE statement
	 * 
	 * @param table - Table name
	 * @param assignmentList - List of assignment
	 * @param whereCondition - Condition
	 * @return The UPDATE statement
	 */
	public static String getUpdate(String table, CSVRecordGvpmConsolidated assignmentList, String whereCondition) {
    		
    		//System.out.println(setTable(table) + setAssignmentList(assignmentList) + setWhereCondition("consolidated_id",whereCondition));
		gvpmConsolidated.init();
		gvpmConsolidated.setTable(table);
		gvpmConsolidated.setAssignmentList(assignmentList);
		gvpmConsolidated.setWhereCondition("consolidated_id",whereCondition);
    	
		//System.out.println("Update generated: " + gvpmConsolidated.getUpdateStatement());
		return gvpmConsolidated.getUpdateStatement();
    }
	
	

	
	
	
}
