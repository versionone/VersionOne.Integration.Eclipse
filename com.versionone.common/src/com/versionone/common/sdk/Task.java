package com.versionone.common.sdk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Attribute;

/**
 * This class represents one Task in the VersionOne system
 * @author jerry
 */
public class Task {

	private static final String EFFORT_PROPERTY = "Effort";

	private static final String DONE_PROPERTY = "Actuals.Value.@Sum";

	private static final String STATUS_NAME_PROPERTY = "Status.Name";

	private static final String TO_DO_PROPERTY = "ToDo";

	private static final String DETAIL_ESTIMATE_PROPERTY = "DetailEstimate";

	private static final String ID_NUMBER_PROPERTY = "Number";

	private static final String NAME_PROPERTY = "Name";

	private static final String PARENT_NAME_PROPERTY = "Parent.Name";

	private static final String TASK_PREFIX = "Task.";
	
	HashMap<String, String> _values = new HashMap<String, String>();
	HashMap<String, String> _newValues = new HashMap<String, String>();

	/**
	 * Create with uneditable items
	 * @param token
	 * @param id
	 * @param storyName
	 */
	public Task(String token){
		super();
		_values.put("Token", token);
	}
	
	public void setAttributeValues(Map<String, Attribute> value) throws APIException {
		Iterator<String> iter = value.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			Attribute attribute = value.get(key);
			if( (null != attribute) && (null != attribute.getValue())){
				if(key.startsWith(TASK_PREFIX)) {
					key = key.substring(TASK_PREFIX.length());
				}
				_values.put(key, attribute.getValue().toString());
			}
		}
	}
	
	public String getValue(String key) {
		if(_newValues.containsKey(key)) {
			return _newValues.get(key);
		}
		else if(_values.containsKey(key)) {
			return _values.get(key);
		}
		// if this was a 'real' sdk, then you'd go back to the server and retrieve the attribute
		return "";
	}
	
	public String getStoryName() {
		return getValue(PARENT_NAME_PROPERTY);
	}
	
	public String getName() {
		return getValue(NAME_PROPERTY);
	}
	
	public void setName(String value) {
		_newValues.put(NAME_PROPERTY, value);
	}	

	public String getID() {
		return getValue(ID_NUMBER_PROPERTY);
	}

	public String getEstimate() {
		return getValue(DETAIL_ESTIMATE_PROPERTY);
	}
	
	public void setEstimate(String value) {
		_newValues.put(DETAIL_ESTIMATE_PROPERTY, value);
	}

	public String getToDo() {
		return getValue(TO_DO_PROPERTY);
	}

	public void setToDo(String value) {
		_newValues.put(TO_DO_PROPERTY, value);
	}
	
	public String getStatus() {
		return getValue(STATUS_NAME_PROPERTY);
	}
	
	public void setStatus(String value) {
		_newValues.put(STATUS_NAME_PROPERTY, value);
	}

	public String getDone() {
		return getValue(DONE_PROPERTY);
	}

	public String getEffort() {
		return getValue(EFFORT_PROPERTY);
	}
	
	public void setEffort(String value) {
		_newValues.put(EFFORT_PROPERTY, value);
	}

	public boolean isDirty() {
		return 0 != _newValues.size();
	}
}
