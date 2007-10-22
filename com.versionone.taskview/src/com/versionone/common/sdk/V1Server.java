package com.versionone.common.sdk;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import com.versionone.Oid;
import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.AssetState;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.FilterTerm;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.IMetaModel;
import com.versionone.apiclient.IServices;
import com.versionone.apiclient.MetaException;
import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.QueryResult;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1APIConnector;
import com.versionone.apiclient.V1Exception;
import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.Activator;

/**
 * Singleton class that encapulates all VersionOne Functionality
 * 
 * @author Jerry D. Odenwelder 
 *
 */
public class V1Server {

	/**
	 * URL Suffix for VersionOne MetaData
	 */
	public static final String META_URL_SUFFIX = "meta.v1/";
	
	/**
	 * URL Suffix for VersionOne Data
	 */
	public static final String DATA_URL_SUFFIX = "rest-1.v1/";
	
	private static V1Server _instance = null;

	private IServices _services = null;
	private IMetaModel _metaModel = null;

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
		} catch (V1Exception e) {
			Activator.logError(e);
		}		
	}

	public V1Server(IServices services, IMetaModel metaModel) {
		_services = services;
		_metaModel = metaModel;
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
	 * Initialize the V1Server with specific connectors 
	 * @param services
	 * @param metaModel
	 */
	public static void initialize(IServices services, IMetaModel metaModel) {
		if(null == _instance) {
			_instance = new V1Server(services, metaModel);
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
		if(0 == Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PROJECT_TOKEN).length()) {
			return new Task[0];
		}
		IAssetType taskType = _metaModel.getAssetType("Task");
		
		Query query = new Query(taskType);
		addTaskSelection(query, taskType);
		addFilter(query, taskType);
		
		QueryResult result = _services.retrieve(query);
		Asset[] taskAssets = result.getAssets();

		Task[] rc = new Task[taskAssets.length];
		for(int i = 0; i < taskAssets.length; ++i)
			rc[i] = createTask(taskAssets[i], taskType);
		return rc;
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
		
	private Task createTask(Asset asset, IAssetType taskType) throws APIException {
		Map<String, Attribute> attributes = asset.getAttributes();		
		Task rc = new Task(asset.getOid().getToken());
		rc.setAttributeValues(attributes);
		return rc;
	}
	
}