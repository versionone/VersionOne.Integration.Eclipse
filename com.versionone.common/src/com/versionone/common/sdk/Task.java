package com.versionone.common.sdk;

import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.MetaException;

/**
 * This class represents one Task in the VersionOne system
 * @author jerry
 */
public class Task {

	private static final String ID_NUMBER_PROPERTY       = "Number";
	private static final String NAME_PROPERTY            = "Name";
	private static final String PARENT_NAME_PROPERTY     = "Parent.Name";
	private static final String DETAIL_ESTIMATE_PROPERTY = "DetailEstimate";
	private static final String TO_DO_PROPERTY           = "ToDo";	
	private static final String STATUS_ID_PROPERTY     	 = "Status";
	private static final String DONE_PROPERTY            = "Actuals.Value.@Sum";

	private static final float INITIAL_EFFORT = 0;
	
	private static final String TASK_PREFIX = "Task.";
	
	IAttributeDefinition _nameDefinition;
	IAttributeDefinition _estimateDefinition;
	IAttributeDefinition _todoDefinition;
	IAttributeDefinition _statusDefinition;

	Asset _asset;
	Float _effortValue = new Float(INITIAL_EFFORT);
	
	/**
	 * Create  
	 * @param asset - Task asset 
	 * @throws MetaException
	 */
	Task(Asset asset) throws MetaException {
		_asset = asset;
		_nameDefinition     = _asset.getAssetType().getAttributeDefinition(NAME_PROPERTY);
		_estimateDefinition = _asset.getAssetType().getAttributeDefinition(DETAIL_ESTIMATE_PROPERTY);
		_todoDefinition     = _asset.getAssetType().getAttributeDefinition(TO_DO_PROPERTY);
		_statusDefinition   = _asset.getAssetType().getAttributeDefinition(STATUS_ID_PROPERTY);
	}
	
	public String getStoryName() throws Exception {
		return getValue(PARENT_NAME_PROPERTY);
	}

	public String getToken() throws Exception {
		return _asset.getOid().getToken();
	}
	
	public String getName() throws Exception {
		return getValue(NAME_PROPERTY);
	}
	
	public void setName(String value) throws Exception {
		_asset.setAttributeValue(_nameDefinition, value);
	}	

	public String getID() throws Exception {
		return getValue(ID_NUMBER_PROPERTY);
	}

	/**
	 * Detail Estimate
	 * @return value of estimate or -1 if the attribute is blank
	 * @throws Exception
	 */
	public float getEstimate() throws Exception {		
		return getFloatValue(_estimateDefinition);
	}

	/**
	 * Sets the Estimate value
	 * @param value
	 * New value must be greater than or equal to 0 ( value >= 0 )
	 * @param value new value for attribute.  
	 * @throws Exception when an API error occurs
	 * @throws IllegalArgumentException if the value is less than 0
	 */
	public void setEstimate(float value) throws Exception {
		if(0 > value)
			throw new IllegalArgumentException("Estimate cannot be negative");
		_asset.setAttributeValue(_estimateDefinition, value);
	}

	/**
	 * Remaining work
	 * @return value of estimate or -1 if the attribute is blank
	 * @throws Exception
	 */
	public float getToDo() throws Exception {
		return getFloatValue(_todoDefinition);
	}
	
	/**
	 * Sets the ToDo value
	 * New value must be greater than or equal to 0 ( value >= 0 )
	 * @param value new value for attribute.  
	 * @throws Exception when an API error occurs
	 * @throws IllegalArgumentException if the value is less than 0
	 */
	public void setToDo(float value) throws Exception {
		if(0 > value)
			throw new IllegalArgumentException("ToDo cannot be negative");
		_asset.setAttributeValue(_todoDefinition, value);
	}
	
	/**
	 * Object identifier (token) of current status
	 * @return status token
	 * @throws Exception
	 */
	public String getStatus() throws Exception {
		Attribute attrib = _asset.getAttribute(_statusDefinition);
		return attrib.getValue().toString();
	}
	
	/**
	 * Set the Status value
	 * @param value - the OID for the desired status value
	 * @throws Exception
	 */
	public void setStatus(String value) throws Exception {
		_asset.setAttributeValue(_statusDefinition, value);
	}

	/**
	 * How much work has been done
	 * @return
	 * @throws Exception
	 */
	public String getDone() throws Exception {
		return getValue(DONE_PROPERTY);
	}

	/**
	 * Effort on this task
	 * @return value of estimate or 0 if the attribute is blank
	 */
	public float getEffort() {
		return _effortValue;
	}
	
	public void setEffort(float value) throws Exception {
		_effortValue = value;		
	}

	/**
	 * Has this instance been modified? 
	 * @return true if a change was made
	 */
	public boolean isDirty() {
		return (_asset.hasChanged()) || (INITIAL_EFFORT != _effortValue.floatValue());
	}

	/**
	 * Get the value of an attribute
	 * @param key - name of attribute
	 * @return value
	 * @throws Exception
	 */
	String getValue(String key) throws Exception {		
		if(_asset.getAttributes().containsKey(TASK_PREFIX + key)) {
			Object value = _asset.getAttributes().get(TASK_PREFIX + key).getValue();
			if(null == value) {
				return "";
			}
			return value.toString();
		}
		// if this was a 'real' sdk, then you'd go back to the server and retrieve the attribute
		return "";
	}

	/**
	 * Return the float value of an attribute definition
	 * @param attribute
	 * @return value of attribute or -1 if the attribute does not exist
	 * @throws Exception
	 */
	float getFloatValue(IAttributeDefinition attribute) throws Exception {
		float rc = -1;
		Attribute attrib = _asset.getAttribute(attribute);
		if(null != attrib) {
			Float value = ((Float)attrib.getValue());
			if(null != value)
				rc = value.floatValue(); 
		}
		return 	rc;	
	}	
}
