package com.versionone.common.sdk;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.AssetState;
import com.versionone.apiclient.FilterTerm;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.ILocalizer;
import com.versionone.apiclient.IMetaModel;
import com.versionone.apiclient.IServices;
import com.versionone.apiclient.Localizer;
import com.versionone.apiclient.MetaException;
import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.QueryResult;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1APIConnector;
import com.versionone.apiclient.V1Exception;
import com.versionone.common.Activator;
import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;

/**
 * Singleton class that wraps APIClient and encapsulates the VersionOne 
 * functionality required by the integration
 * 
 * @author Jerry D. Odenwelder Jr. 
 */
public class V1Server {

	/**
	 * URL Suffix for VersionOne Meta API
	 */
	public static final String META_URL_SUFFIX = "meta.v1/";
	
	/**
	 * URL Suffix for VersionOne Data API
	 */
	public static final String DATA_URL_SUFFIX = "rest-1.v1/";
	
	/**
	 * URL Suffix for VersionOne Localizer API
	 */
	public static final String LOCALIZER_URL_SUFFIX = "loc.v1/";

	// Singleton instance
	private static V1Server _instance = null;

	// APIClient objects
	private IServices   _services  = null;
	private IMetaModel  _metaModel = null;
	private ILocalizer  _localizer = null;

	private IAssetType _actualType = null;


	/**
	 * Construct from configuration
	 * @param config - Eclipse Preferences
	 */
	private V1Server(IPreferenceStore config)
	{
		if(!config.getBoolean(PreferenceConstants.P_ENABLED)) {
			return;
		}
		else if(null != _services)
			_services = null;
		
		String url = config.getString(PreferenceConstants.P_URL);
		V1APIConnector metaConnector = new V1APIConnector(url + META_URL_SUFFIX);
		_metaModel = new MetaModel(metaConnector);		
		
		V1APIConnector dataConnector = null;
		if(config.getBoolean(PreferenceConstants.P_INTEGRATED_AUTH)) {
			dataConnector = new V1APIConnector(url + DATA_URL_SUFFIX);
		}
		else {
			dataConnector = new V1APIConnector( url + DATA_URL_SUFFIX 
					                          , config.getString(PreferenceConstants.P_USER)
					                          , config.getString(PreferenceConstants.P_PASSWORD));
		}
		
		_services = new Services(_metaModel, dataConnector);		

		try {
			setMemberToken(config, _services);
			_actualType = _metaModel.getAssetType("Actual");
		} catch (V1Exception e) {
			Activator.logError(e);
		}
		
		V1APIConnector localizerConnector = new V1APIConnector(url + LOCALIZER_URL_SUFFIX);
		_localizer = new Localizer(localizerConnector);
		
		
	}

	/**
	 * Construct with APIClient objects. 
	 * Used for testing
	 * @param services
	 * @param metaModel
	 * @param localize
	 */
	public V1Server(IServices services, IMetaModel metaModel, ILocalizer localize) {
		_services  = services;
		_metaModel = metaModel;
		_localizer  = localize;
	}

	/**
	 * Request the member token from the server and set this value in the configuration 
	 * @param config - config
	 * @param services - VersionOne Service
	 * @throws V1Exception 
	 */
	public static void setMemberToken(IPreferenceStore config, IServices services) throws V1Exception {
		Oid userOid = services.getLoggedIn();
		config.setValue(PreferenceConstants.P_MEMBER_TOKEN, userOid.getToken());
	}
		
	/**
	 * Create the Singleton instance of V1Server
	 * @return
	 */
	public static V1Server getInstance() {
		if(null == _instance) {
			_instance = new V1Server(Activator.getDefault().getPreferenceStore());
		}
		return _instance;
	}

	/**
	 * Initialize the V1Server with specific connectors.
	 * Used for Testing 
	 * @param services
	 * @param metaModel
	 * @param localizer
	 */
	public static void initialize(IServices services, IMetaModel metaModel, ILocalizer localizer) {
		if(null == _instance) {
			_instance = new V1Server(services, metaModel, localizer);
		}
	}
	
	/**
	 * Get the projects available to this user
	 * 
	 * @return IProjectNode containing the projects available to this user
	 * 
	 * @throws V1Exception
	 */
	public IProjectTreeNode getProjects() throws Exception {
		
		IAssetType scopeType = _metaModel.getAssetType("Scope");
		
		IAttributeDefinition scopeName = scopeType.getAttributeDefinition("Name");

		Query scopeQuery = new Query(scopeType, scopeType.getAttributeDefinition("Parent"));
		
		FilterTerm stateTerm = Query.term(scopeType.getAttributeDefinition("AssetState"));
		stateTerm.NotEqual(AssetState.Closed);
		
		scopeQuery.setFilter(stateTerm);
		scopeQuery.getSelection().add(scopeName);

		QueryResult result = _services.retrieve(scopeQuery);
			
		List<Asset> assets = Arrays.asList(result.getAssets());

		ProjectTreeNode rc = new ProjectTreeNode("", ""); 
		recurseAndAddNodes(rc._children, assets, scopeName);
		return rc;
	}

	private void recurseAndAddNodes( List<IProjectTreeNode> projectTreeNodes, List<Asset> assets, IAttributeDefinition scopeName) throws V1Exception {
		Iterator<Asset> iter = assets.iterator();
		while(iter.hasNext()) {
			Asset oneAsset = iter.next();			
			ProjectTreeNode oneNode = new ProjectTreeNode((String)oneAsset.getAttribute(scopeName).getValue() ,(String)oneAsset.getOid().getToken());
			projectTreeNodes.add(oneNode);
			recurseAndAddNodes(oneNode._children, oneAsset.getChildren(), scopeName);
		}		
	}

	/**
	 * Get Task Assigned to this user.
	 * @return Array of Task assigned to this user.
	 * @throws V1Exception
	 */
	public Task[] getTasks() throws Exception {
		if(0 == PreferencePage.getPreferences().getString(PreferenceConstants.P_PROJECT_TOKEN).length()) {
			return new Task[0];
		}
		IAssetType taskType = _metaModel.getAssetType("Task");
		
		Query query = new Query(taskType);
		addTaskSelection(query, taskType);
		addFilter(query, taskType);
		
		QueryResult result = _services.retrieve(query);
		Asset[] taskAssets = result.getAssets();

		Task[] rc = new Task[taskAssets.length];
		for(int i = 0; i < taskAssets.length; ++i) {
			rc[i] = new Task(taskAssets[i]);
		}
			
		return rc;
	}

	/**
	 * Return a collection of all valid Task Status Values
	 * @return String[] of valid TaskStatus Values
	 */
	public IStatusCodes getTaskStatusValues() throws Exception {
		return new TaskStatusCodes(_metaModel, _services);
	}
	
	
	private void addTaskSelection(Query query, IAssetType taskType) throws MetaException {		
		String[] attributes = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_ATTRIBUTE_SELECTION).split(",");
		for(String oneAttribute : attributes) {
			query.getSelection().add(taskType.getAttributeDefinition(oneAttribute));
		}
	}

	private void addFilter(Query query, IAssetType taskType) throws MetaException {
		FilterTerm[] terms = new FilterTerm[5];
		terms[0] = new FilterTerm(taskType.getAttributeDefinition("Scope.AssetState"),    FilterTerm.Operator.NotEqual, AssetState.Closed);
		terms[1] = new FilterTerm(taskType.getAttributeDefinition("Scope.ParentMeAndUp"), FilterTerm.Operator.Equal,    Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PROJECT_TOKEN));
		terms[2] = new FilterTerm(taskType.getAttributeDefinition("Owners"),              FilterTerm.Operator.Equal,    Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_MEMBER_TOKEN));
		terms[3] = new FilterTerm(taskType.getAttributeDefinition("Timebox.State.Code"),  FilterTerm.Operator.Equal,    "ACTV");
		terms[4] = new FilterTerm(taskType.getAttributeDefinition("AssetState"),          FilterTerm.Operator.NotEqual, AssetState.Closed);		
		query.setFilter(Query.and(terms));
	}

	/**
	 * Save a Task array
	 * @param tasks
	 * @throws Exception
	 */
	public void save(List<Task> tasks) throws Exception {
		Asset[] assets = new Asset[tasks.size()];
		Iterator<Task> iter = tasks.iterator();
		int i = 0;
		while(iter.hasNext()) {
			Task oneTask = iter.next();
			if(0 != oneTask.getEffort()) {
				Asset effort = _services.createNew(_actualType, oneTask._asset.getOid());
				effort.setAttributeValue(_actualType.getAttributeDefinition("Value"), oneTask.getEffort());
				effort.setAttributeValue(_actualType.getAttributeDefinition("Date"), new Date());
				oneTask._asset.getNewAssets().put("Actuals", effort);		
			}
			assets[i++] = oneTask._asset;
		}
		this._services.save(assets);
	}
	
	/**
	 * Return the local value for the given string
	 * @param value
	 * @return
	 */
	public String getLocalString(String value) {
		return _localizer.resolve(value);
	}
}