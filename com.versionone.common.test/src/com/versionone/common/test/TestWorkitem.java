package com.versionone.common.test;

import java.util.HashMap;
import java.util.Map;

import com.versionone.common.sdk.Workitem;


public class TestWorkitem extends Workitem {

    public String id;
    public String prefix;
    public boolean hasChanges;
    public Map<String, Object> properties = new HashMap<String, Object>(); 

    public TestWorkitem() {
	super(null, null);
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
