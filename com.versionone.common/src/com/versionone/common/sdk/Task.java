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

	private static final String TASK_PREFIX = "Task.";
	
	IAttributeDefinition _nameDefinition;
	IAttributeDefinition _estimateDefinition;
	IAttributeDefinition _todoDefinition;
	IAttributeDefinition _statusDefinition;

	Asset _asset;
	Float _effortValue = new Float(0);
	
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
	
	public String getName() throws Exception {
		return getValue(NAME_PROPERTY);
	}
	
	public void setName(String value) throws Exception {
		_asset.setAttributeValue(_nameDefinition, value);
	}	

	public String getID() throws Exception {
		return getValue(ID_NUMBER_PROPERTY);
	}

	public float getEstimate() throws Exception {		
		return getFloatValue(_estimateDefinition);
	}
	
	public void setEstimate(float value) throws Exception {
		_asset.setAttributeValue(_estimateDefinition, value);
	}

	public float getToDo() throws Exception {
		return getFloatValue(_todoDefinition);
	}
	
	public void setToDo(float value) throws Exception {
		_asset.setAttributeValue(_todoDefinition, value);
	}
	
	public String getStatus() throws Exception {
		Attribute attrib = _asset.getAttribute(_statusDefinition);
		return attrib.getValue().toString();
	}
	
	public void setStatus(String value) throws Exception {
		_asset.setAttributeValue(_statusDefinition, value);
	}

	public String getDone() throws Exception {
		return getValue(DONE_PROPERTY);
	}

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
		return (_asset.hasChanged()) || (0 != _effortValue.floatValue());
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
	 * @return
	 * @throws Exception
	 */
	float getFloatValue(IAttributeDefinition attribute) throws Exception {
		float rc = 0;
		Attribute attrib = _asset.getAttribute(attribute);
		if(null != attrib) {
			Float value = ((Float)attrib.getValue());
			if(null != value)
				rc = value.floatValue(); 
		}
		return 	rc;	
	}	
}
