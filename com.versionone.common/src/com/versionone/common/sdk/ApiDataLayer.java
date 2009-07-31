package com.versionone.common.sdk;

import com.versionone.apiclient.Asset;

public class ApiDataLayer {

	private static ApiDataLayer instance;

	public static ApiDataLayer getInstance() {
		if (instance == null){
			instance = new ApiDataLayer();
		}
		return instance;
	}

	public boolean ShowAllTasks;

	public boolean IsCurrentUserOwnerAsset(Asset childAsset) {
		// TODO Auto-generated method stub
		return false;
	}

	public Workitem[] getWorkitemTree(){
		return new Workitem[] {new Workitem(null, null)};
	}

	public Double GetEffort(Asset asset) {
		// TODO Auto-generated method stub
		return null;
	}

	public PropertyValues GetListPropertyValues(String type, String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	static void warning(String string, Exception ex) {
		// TODO Auto-generated method stub
		
	}

	public void addEffort(Asset asset, double value) {
	    // TODO Auto-generated method stub
	    
	}
}
