package com.versionone.common.sdk;

import com.versionone.Oid;
import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.IOperation;

public class ApiDataLayer {

	private static ApiDataLayer instance;

	public static ApiDataLayer getInstance() {
		if (instance == null){
			instance = new ApiDataLayer();
		}
		return instance;
	}

	public boolean showAllTasks;
	public EffortTrackingLevel storyTrackingLevel;
	public EffortTrackingLevel defectTrackingLevel;
	public Oid memberOid;

	public boolean isCurrentUserOwnerAsset(Asset childAsset) {
		// TODO Auto-generated method stub
		return false;
	}

	public Workitem[] getWorkitemTree(){
		return new Workitem[] {new Workitem(null, null)};
	}

	public Double getEffort(Asset asset) {
		// TODO Auto-generated method stub
		return null;
	}

	public PropertyValues getListPropertyValues(String type, String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	static DataLayerException warning(String string, Exception ex) {
		// TODO Auto-generated method stub
		return new DataLayerException();
	}

	public void addEffort(Asset asset, double value) {
	    // TODO Auto-generated method stub
	    
	}

	public boolean isEffortTrackingRelated(String propertyName) {
	    // TODO Auto-generated method stub
	    return false;
	}

	public void commitAsset(Asset asset) throws APIException {
	    // TODO Auto-generated method stub
	    
	}

	public void executeOperation(Asset asset, IOperation operation) throws APIException{
	    // TODO Auto-generated method stub
	    
	}

	public void refreshAsset(Workitem workitem) {
	    // TODO Auto-generated method stub
	    
	}
}
