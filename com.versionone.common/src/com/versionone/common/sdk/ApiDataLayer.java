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
}
