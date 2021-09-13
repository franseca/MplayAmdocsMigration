package com.tecnotree.mplay.mplayAmdocs.relational;

import java.util.HashMap;

import com.tecnotree.mplay.mplayAmdocs.csvRecord.CSVRecordGvpmConsolidated;
import com.tecnotree.mplay.mplayAmdocs.fields.FieldsGvpmConsolidated;

public class GvpmConsolidated {

	private static HashMap<String, String> setHashMap = new HashMap<String, String>();
	private String updateStatementTemplate = "";
	private String updateStatement = "";
	
	public GvpmConsolidated(){}
	
	public GvpmConsolidated(String updateStatementTemplate){
		this.updateStatementTemplate = updateStatementTemplate;
		this.updateStatement = updateStatementTemplate;
		initializeAssignmentList();
	}
	
	/**
	 * Method set table name
	 * 
	 * @param table - Table name
	 * @return The UPDATE statement with the table name
	 */
	public String setTable(String table) {
		this.updateStatement = updateStatement.replaceAll("&table", table);
		
		return updateStatement;
		
	}
	
	/**
	 * Method sets assignment list
	 * 
	 * @param assignmentList - List of assignment
	 * @return The UPDATE statement with the list of assignment
	 */
	public String setAssignmentList(CSVRecordGvpmConsolidated assignmentList) {
				
		setHashMap.put(FieldsGvpmConsolidated.CREATION_DATE, assignmentList.getCreation_date());
		setHashMap.put(FieldsGvpmConsolidated.MODIFIED_DATE, assignmentList.getModified_date());
		setHashMap.put(FieldsGvpmConsolidated.SYSTEM, assignmentList.getSystem());
		setHashMap.put(FieldsGvpmConsolidated.ACCOUNT_NUM, assignmentList.getAccount_num());
		setHashMap.put(FieldsGvpmConsolidated.CLIENT_NUM, assignmentList.getClient_num());
		setHashMap.put(FieldsGvpmConsolidated.INSCRIPTION_NUM, assignmentList.getInscription_num());
		setHashMap.put(FieldsGvpmConsolidated.SERVICE_CODE, assignmentList.getService_code());
		setHashMap.put(FieldsGvpmConsolidated.TELEPHONE, assignmentList.getTelephone());
		setHashMap.put(FieldsGvpmConsolidated.DOC_TYPE, assignmentList.getDoc_type());
		setHashMap.put(FieldsGvpmConsolidated.DOCUMENT, assignmentList.getDocument());
		setHashMap.put(FieldsGvpmConsolidated.GVP_ALIAS, assignmentList.getGvp_alias());
		setHashMap.put(FieldsGvpmConsolidated.EMAIL, assignmentList.getEmail());
		setHashMap.put(FieldsGvpmConsolidated.FULL_NAME, assignmentList.getFull_name());
		setHashMap.put(FieldsGvpmConsolidated.REGISTER_DATE, assignmentList.getRegister_date());
		setHashMap.put(FieldsGvpmConsolidated.DELETED, assignmentList.getDeleted());
		setHashMap.put(FieldsGvpmConsolidated.STATUS, assignmentList.getStatus());
		setHashMap.put(FieldsGvpmConsolidated.COMM_PRODUCT, assignmentList.getComm_product());
		setHashMap.put(FieldsGvpmConsolidated.BILLING_CYCLE, assignmentList.getBilling_cycle());
		setHashMap.put(FieldsGvpmConsolidated.BAMLTE, assignmentList.getBamlte());
		
		
		//NUMERO DE REGISTROS CON VALORES
		int recordsWithValue = 0;
		for (String key : setHashMap.keySet()) 
			if( ((String)setHashMap.get(key)) != null) 
				recordsWithValue++;
		
		//PONGO LOS CAMPOS Y VALORES QUE NO SON NULL EN UN ARREGLO
		String[] recordsWithValues = new String[recordsWithValue];
		
		int i = 0;
		for (String key : setHashMap.keySet()) {
			if( ((String)setHashMap.get(key)) != null) {
				recordsWithValues[i] =  key+"='"+setHashMap.get(key)+"'";
				i++;
			}
		}
		
		//ARMO EL SET DEL UPDATE
		StringBuffer setBuffer = new StringBuffer("");
		
		for(int j = 0; j < recordsWithValues.length; j++) {
			
			if(j == (recordsWithValues.length-1))
				setBuffer.append(recordsWithValues[j]);
			
			else
				setBuffer.append(recordsWithValues[j]+",");
		}
		
		updateStatement = updateStatement.replaceAll("&assignmentList", setBuffer.toString());
		
		return updateStatement;
	}
	
	/**
	 * Method initializes the list of assignment
	 */
	private void initializeAssignmentList() {
		setHashMap = new HashMap<String,String>();
		setHashMap.put(FieldsGvpmConsolidated.CREATION_DATE, "");
		setHashMap.put(FieldsGvpmConsolidated.MODIFIED_DATE, "");
		setHashMap.put(FieldsGvpmConsolidated.SYSTEM, "");
		setHashMap.put(FieldsGvpmConsolidated.ACCOUNT_NUM, "");
		setHashMap.put(FieldsGvpmConsolidated.CLIENT_NUM, "");
		setHashMap.put(FieldsGvpmConsolidated.INSCRIPTION_NUM, "");
		setHashMap.put(FieldsGvpmConsolidated.SERVICE_CODE, "");
		setHashMap.put(FieldsGvpmConsolidated.TELEPHONE, "");
		setHashMap.put(FieldsGvpmConsolidated.DOC_TYPE, "");
		setHashMap.put(FieldsGvpmConsolidated.DOCUMENT, "");
		setHashMap.put(FieldsGvpmConsolidated.GVP_ALIAS, "");
		setHashMap.put(FieldsGvpmConsolidated.EMAIL, "");
		setHashMap.put(FieldsGvpmConsolidated.FULL_NAME, "");
		setHashMap.put(FieldsGvpmConsolidated.REGISTER_DATE, "");
		setHashMap.put(FieldsGvpmConsolidated.DELETED, "");
		setHashMap.put(FieldsGvpmConsolidated.STATUS, "");
		setHashMap.put(FieldsGvpmConsolidated.COMM_PRODUCT, "");
		setHashMap.put(FieldsGvpmConsolidated.BILLING_CYCLE, "");
		setHashMap.put(FieldsGvpmConsolidated.BAMLTE, "");
	}
	
	/**
	 * @return the setHashMap
	 */
	public static HashMap<String, String> getSetHashMap() {
		return setHashMap;
	}

	/**
	 * @param setHashMap the setHashMap to set
	 */
	public static void setSetHashMap(HashMap<String, String> setHashMap) {
		GvpmConsolidated.setHashMap = setHashMap;
	}

	/**
	 * Method sets WHERE condition
	 * Only set one WHERE condition
	 * 
	 * @param column - Column name of the condition 
	 * @param whereCondition - Value of the condition
	 * @return WHERE condition
	 */
	public String setWhereCondition(String column, String whereCondition) {
		updateStatement = updateStatement.replaceAll("&column", column);
		updateStatement = updateStatement.replaceAll("&value", whereCondition);
		
		return updateStatement;
		
	}
	
	/**
	 * Method initializes variables
	 */
	public void init() {
		this.updateStatement = updateStatementTemplate;
		initializeAssignmentList();
	}

	/**
	 * @return the updateStatementTemplate
	 */
	public String getUpdateStatementTemplate() {
		return updateStatementTemplate;
	}

	/**
	 * @param updateStatementTemplate the updateStatementTemplate to set
	 */
	public void setUpdateStatementTemplate(String updateStatementTemplate) {
		this.updateStatementTemplate = updateStatementTemplate;
	}

	/**
	 * @return the updateStatement
	 */
	public String getUpdateStatement() {
		return updateStatement;
	}

	/**
	 * @param updateStatement the updateStatement to set
	 */
	public void setUpdateStatement(String updateStatement) {
		this.updateStatement = updateStatement;
	}

	
}
