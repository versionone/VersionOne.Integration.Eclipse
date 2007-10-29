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
//	private static final String STATUS_NAME_PROPERTY     = "Status.Name";	
	private static final String STATUS_ID_PROPERTY     	 = "Status";
	private static final String EFFORT_PROPERTY          = "Effort";
	private static final String DONE_PROPERTY            = "Actuals.Value.@Sum";

	private static final String TASK_PREFIX = "Task.";
	
	IAttributeDefinition _nameDefinition;
	IAttributeDefinition _estimateDefinition;
	IAttributeDefinition _todoDefinition;
	IAttributeDefinition _statusDefinition;

	Asset _asset;
	
	Task(Asset asset, boolean trackEffort) throws MetaException {
		_asset = asset;
		_nameDefinition     = _asset.getAssetType().getAttributeDefinition(NAME_PROPERTY);
		_estimateDefinition = _asset.getAssetType().getAttributeDefinition(DETAIL_ESTIMATE_PROPERTY);
		_todoDefinition     = _asset.getAssetType().getAttributeDefinition(TO_DO_PROPERTY);
		_statusDefinition   = _asset.getAssetType().getAttributeDefinition(STATUS_ID_PROPERTY);
	}
	
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

	public String getEstimate() throws Exception {
		return getValue(DETAIL_ESTIMATE_PROPERTY);
	}
	
	public void setEstimate(float value) throws Exception {
		_asset.setAttributeValue(_estimateDefinition, value);
	}

	public String getToDo() throws Exception {
		return getValue(TO_DO_PROPERTY);
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

	public String getEffort() throws Exception {
		return getValue(EFFORT_PROPERTY);
	}
	
	public void setEffort(float value) throws Exception {

	}

	public boolean isDirty() {
		return _asset.hasChanged();
	}
}
