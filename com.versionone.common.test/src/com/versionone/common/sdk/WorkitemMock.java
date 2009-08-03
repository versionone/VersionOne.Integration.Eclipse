package com.versionone.common.sdk;

import java.util.HashMap;
import java.util.Map;

import com.versionone.common.sdk.Workitem;


public class WorkitemMock extends Workitem {

    public String id;
    public String prefix;
    public boolean hasChanges;
    public Map<String, Object> properties = new HashMap<String, Object>(); 

    public WorkitemMock() {
        super(null, null);
    }
    
    public WorkitemMock(String id, String prefix) {
	this();
	this.id = id;
	this.prefix = prefix;
    }

    @Override
    public String getId() {
	return id;
    }

    @Override
    public Object getProperty(String propertyName) {
	return properties.get(propertyName);
    }

    @Override
    public String getTypePrefix() {
	return prefix;
    }

    @Override
    public boolean hasChanges() {
	return hasChanges;
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
	properties.put(propertyName, newValue);
    }

}
