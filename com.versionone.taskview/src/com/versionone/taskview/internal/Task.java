package com.versionone.taskview.internal;

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

	private static final String TASK_PREFIX = "Task.";
	
	HashMap<String, String> _values = new HashMap<String, String>();

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
//			else {
//				Activator.logInfo("No value for " + key);
//			}
		}
	}
	
	public String getValue(String key) {
		if(_values.containsKey(key)) {
			return _values.get(key);
		}
		return "";
	}	
}
