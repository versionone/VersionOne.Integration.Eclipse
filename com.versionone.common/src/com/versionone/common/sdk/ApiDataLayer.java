package com.versionone.common.sdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.versionone.Oid;
import com.versionone.apiclient.AndFilterTerm;

import com.versionone.apiclient.APIException;

import com.versionone.apiclient.Asset;
import com.versionone.apiclient.AssetState;
import com.versionone.apiclient.ConnectionException;
import com.versionone.apiclient.FilterTerm;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.IFilterTerm;
import com.versionone.apiclient.ILocalizer;
import com.versionone.apiclient.Localizer;
import com.versionone.apiclient.MetaException;
import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.OidException;
import com.versionone.apiclient.OrderBy;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.QueryResult;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1APIConnector;
import com.versionone.apiclient.V1Configuration;
import com.versionone.apiclient.IOperation;
import com.versionone.apiclient.V1Exception;

public class ApiDataLayer {

    private static final String MetaUrlSuffix = "meta.v1/";
    private static final String LocalizerUrlSuffix = "loc.v1/";
    private static final String DataUrlSuffix = "rest-1.v1/";
    private static final String ConfigUrlSuffix = "config.v1/";

    private static final List<String> effortTrackingAttributesList = Arrays.asList(Workitem.DetailEstimateProperty,
            "ToDo", "Done", "Effort", "Actuals");

    private final Map<String, IAssetType> types = new HashMap<String, IAssetType>(5);
    private final Map<Asset, Double> efforts  = new HashMap<Asset, Double>();

    private IAssetType projectType;
    private IAssetType taskType;
    private IAssetType testType;
    private IAssetType defectType;
    private IAssetType storyType;
    private IAssetType workitemType;
    private IAssetType primaryWorkitemType;
    private IAssetType effortType;


    private static ApiDataLayer instance;
    private boolean isConnected;

    public Oid memberOid;
    private String path;
    private String userName;
    private String password;
    private boolean integrated;

    private QueryResult assetList;
    private final List<IAttributeDefinition> alreadyUsedDefinition = new ArrayList<IAttributeDefinition>();
    private static LinkedList<AttributeInfo> attributesToQuery = new LinkedList<AttributeInfo>();
    private Map<String, PropertyValues> listPropertyValues;

    private boolean trackEffort;
    public EffortTrackingLevel defectTrackingLevel;
    public EffortTrackingLevel storyTrackingLevel;

    private MetaModel metaModel;
    private Services services;
    private ILocalizer localizer;

    private String currentProjectId;
    public boolean showAllTasks = true;

    private ApiDataLayer() {
        String[] prefixes = new String[] { Workitem.TaskPrefix, Workitem.DefectPrefix, Workitem.StoryPrefix,
                Workitem.TestPrefix };
        for (String prefix : prefixes) {
            attributesToQuery.addLast(new AttributeInfo("CheckQuickClose", prefix, false));
            attributesToQuery.addLast(new AttributeInfo("CheckQuickSignup", prefix, false));
        }
    }

    public static ApiDataLayer getInstance() {
        if (instance == null) {
            instance = new ApiDataLayer();
        }
        return instance;
    }

    public boolean connect(String path, String userName, String password, boolean integrated) throws Exception {
        isConnected = false;
        this.path = path;
        this.userName = userName;
        this.password = password;
        this.integrated = integrated;
        assetList = null;
        efforts.clear();
        types.clear();
        try {
            V1APIConnector metaConnector = new V1APIConnector(path + MetaUrlSuffix, userName, password);
            metaModel = new MetaModel(metaConnector);

            V1APIConnector localizerConnector = new V1APIConnector(path + LocalizerUrlSuffix, userName, password);
            localizer = new Localizer(localizerConnector);

            V1APIConnector dataConnector = new V1APIConnector(path + DataUrlSuffix, userName, password);
            services = new Services(metaModel, dataConnector);

            V1Configuration v1Config = new V1Configuration(new V1APIConnector(path + ConfigUrlSuffix));

            projectType = getAssetType(Workitem.ProjectPrefix);
            taskType = getAssetType(Workitem.TaskPrefix);
            testType = getAssetType(Workitem.TestPrefix);
            defectType = getAssetType(Workitem.DefectPrefix);
            storyType = getAssetType(Workitem.StoryPrefix);
            workitemType = metaModel.getAssetType("Workitem");
            primaryWorkitemType = metaModel.getAssetType("PrimaryWorkitem");

            trackEffort = v1Config.isEffortTracking();
            if (trackEffort) {
                effortType = metaModel.getAssetType("Actual");
            }

            storyTrackingLevel = EffortTrackingLevel.translate(v1Config.getStoryTrackingLevel());
            defectTrackingLevel = EffortTrackingLevel.translate(v1Config.getDefectTrackingLevel());

            memberOid = services.getLoggedIn();
            listPropertyValues = getListPropertyValues();
            isConnected = true;
            return true;
        } catch (MetaException ex) {
            // throw Warning("Cannot connect to V1 server.", ex); TODO implement
            throw new Exception("Cannot connect to V1 server.", ex);
        }// catch (WebException ex) {
        // isConnected = false;
        // throw Warning("Cannot connect to V1 server.", ex);
        // }
        catch (Exception ex) {
            // throw Warning("Cannot connect to V1 server.", ex); TODO implement
            throw new Exception("Cannot connect to V1 server.", ex);
        }
    }

    public List<Workitem> getProjectTree() throws DataLayerException {
        try {
            Query scopeQuery = new Query(projectType, projectType.getAttributeDefinition("Parent"));
            FilterTerm stateTerm = new FilterTerm(projectType.getAttributeDefinition("AssetState"));
            stateTerm.NotEqual(AssetState.Closed);
            scopeQuery.setFilter(stateTerm);
            // clear all diffinitions which was used in previous queries
            alreadyUsedDefinition.clear();
            addSelection(scopeQuery, Workitem.ProjectPrefix);
            QueryResult result = services.retrieve(scopeQuery);
            List<Workitem> roots = new ArrayList<Workitem>(result.getAssets().length);
            for (Asset oneAsset : result.getAssets()) {
                roots.add(new Workitem(oneAsset, null));
            }
            return roots;
        }/*
          * catch (WebException ex) { isConnected = false; throw
          * Warning("Can't get projects list.", ex); }
          */catch (Exception ex) {
            throw warning("Can't get projects list.", ex);
        }
    }

    private IAssetType getAssetType(String token) {
        IAssetType type = metaModel.getAssetType(token);
        types.put(token, type);
        return type;
    }

    public boolean isCurrentUserOwnerAsset(Asset childAsset) {
        // TODO Auto-generated method stub
        return false;
    }

    public Workitem[] getWorkitemTree() throws Exception {
        checkConnection();
        if (currentProjectId == null) {
            // throw new DataLayerException("Current project is not selected");
            // // TODO implement
            // throw new Exception("Current project is not selected");
            currentProjectId = "Scope:0";

        }

        if (assetList == null) {
            try {
                IAttributeDefinition parentDef = workitemType.getAttributeDefinition("Parent");

                Query query = new Query(workitemType, parentDef);
                // Query query = new Query(taskType, parentDef);
                // clear all definitions which was used in previous queries
                alreadyUsedDefinition.clear();
                addSelection(query, Workitem.TaskPrefix);
                addSelection(query, Workitem.StoryPrefix);
                addSelection(query, Workitem.DefectPrefix);
                addSelection(query, Workitem.TestPrefix);

                query.setFilter(getScopeFilter(workitemType));

                query.getOrderBy().majorSort(primaryWorkitemType.getDefaultOrderBy(), OrderBy.Order.Ascending);
                query.getOrderBy().minorSort(workitemType.getDefaultOrderBy(), OrderBy.Order.Ascending);

                assetList = services.retrieve(query);
            } catch (MetaException ex) {
                throw new Exception(ex.getMessage());
                // throw Warning("Unable to get workitems.", ex);
            }
            /*
             * catch (WebException ex) { isConnected = false; throw
             * Warning("Unable to get workitems.", ex); }
             */
            catch (Exception ex) {
                throw new Exception(ex.getMessage());
                // throw Warning("Unable to get workitems.", ex);
            }
        }

        IAttributeDefinition definition = workitemType.getAttributeDefinition(Workitem.OwnersProperty);
        List<Workitem> res = new ArrayList<Workitem>(assetList.getAssets().length);

        for (Asset asset : assetList.getAssets()) {
            // if (ShowAllTasks || IsCurrentUserOwnerAsset(asset, definition)) {
            // TODO need for show all/own tasks
            res.add(new Workitem(asset, null));
            // }
        }
        return res.toArray(new Workitem[res.size()]);
        // return new Workitem[] {new Workitem(null, null)};
    }

    private void checkConnection() throws DataLayerException {
        if (!isConnected) {
            throw warning("Connection is not set.");
        }
    }

    private IFilterTerm getScopeFilter(IAssetType assetType) {
        List<FilterTerm> terms = new ArrayList<FilterTerm>(5);
        FilterTerm term = new FilterTerm(assetType.getAttributeDefinition("Scope.AssetState"));
        term.NotEqual(AssetState.Closed);
        terms.add(term);
        term = new FilterTerm(assetType.getAttributeDefinition("Scope.ParentMeAndUp"));
        term.Equal(currentProjectId);
        terms.add(term);
        term = new FilterTerm(assetType.getAttributeDefinition("Timebox.State.Code"));
        term.Equal("ACTV");
        terms.add(term);
        term = new FilterTerm(assetType.getAttributeDefinition("AssetState"));
        term.NotEqual(AssetState.Closed);
        terms.add(term);
        return new AndFilterTerm(terms.toArray(new FilterTerm[terms.size()]));
    }

    // need to make AlreadyUsedDefinition.Clear(); before first call of this
    // method
    private void addSelection(Query query, String typePrefix) throws Exception {
        for (AttributeInfo attrInfo : attributesToQuery) {
            if (attrInfo.prefix == typePrefix) {
                try {
                    IAttributeDefinition def = types.get(attrInfo.prefix).getAttributeDefinition(attrInfo.attr);
                    if (!alreadyUsedDefinition.contains(def)) {
                        query.getSelection().add(def);
                        alreadyUsedDefinition.add(def);
                    }
                } catch (MetaException e) {
                    // Warning("Wrong attribute: " + attrInfo, e); /TODO warning
                    throw new Exception("Wrong attribute: " + attrInfo);
                }
            }
        }
    }

    public void addProperty(String attr, String prefix, boolean isList) {
        attributesToQuery.addLast(new AttributeInfo(attr, prefix, isList));
    }

    private Map<String, PropertyValues> getListPropertyValues() throws Exception { // ConnectionException,
                                                                                   // APIException,
                                                                                   // OidException,
                                                                                   // MetaException
                                                                                   // {
        Map<String, PropertyValues> res = new HashMap<String, PropertyValues>(attributesToQuery.size());
        for (AttributeInfo attrInfo : attributesToQuery) {
            if (!attrInfo.isList) {
                continue;
            }

            String propertyAlias = attrInfo.prefix + attrInfo.attr;
            if (!res.containsKey(propertyAlias)) {
                String propertyName = resolvePropertyKey(propertyAlias);

                PropertyValues values;
                if (res.containsKey(propertyName)) {
                    values = res.get(propertyName);
                } else {
                    values = queryPropertyValues(propertyName);
                    res.put(propertyName, values);
                }

                if (!res.containsKey(propertyAlias)) {
                    res.put(propertyAlias, values);
                }
            }
        }
        return res;
    }

    private static String resolvePropertyKey(String propertyAlias) {
        if (propertyAlias.equals("DefectStatus")) {
            return "StoryStatus";
        } else if (propertyAlias.equals("DefectSource")) {
            return "StorySource";
        } else if (propertyAlias.equals("ScopeBuildProjects")) {
            return "BuildProject";
        } else if (propertyAlias.equals("TaskOwners") || propertyAlias.equals("StoryOwners")
                || propertyAlias.equals("DefectOwners") || propertyAlias.equals("TestOwners")) {
            return "Member";
        }

        return propertyAlias;
    }

    private PropertyValues queryPropertyValues(String propertyName) throws ConnectionException, APIException,
            OidException, MetaException {
        PropertyValues res = new PropertyValues();
        IAssetType assetType = metaModel.getAssetType(propertyName);
        IAttributeDefinition nameDef = assetType.getAttributeDefinition(Workitem.NameProperty);
        IAttributeDefinition inactiveDef;

        Query query = new Query(assetType);
        query.getSelection().add(nameDef);
        // if (assetType.TryGetAttributeDefinition("Inactive", out inactiveDef))
        // {
        inactiveDef = assetType.getAttributeDefinition("Inactive");
        if (inactiveDef != null) {
            FilterTerm filter = new FilterTerm(inactiveDef);
            filter.Equal("False");
            query.setFilter(filter);
        }

        query.getOrderBy().majorSort(assetType.getDefaultOrderBy(), OrderBy.Order.Ascending);

        res.addInternal(new ValueId());
        for (Asset asset : services.retrieve(query).getAssets()) {
            String name = (String) asset.getAttribute(nameDef).getValue();
            res.addInternal(new ValueId(asset.getOid(), name));
        }
        return res;
    }

    public PropertyValues getListPropertyValues(String type, String propertyName) {
        String propertyKey = resolvePropertyKey(type + propertyName);
        return listPropertyValues.get(propertyKey);
    }

    static DataLayerException warning(String string, Exception ex) {
        // TODO Auto-generated method stub
        return new DataLayerException();
    }

    static DataLayerException warning(String string) {
        // TODO Auto-generated method stub
        return new DataLayerException();
    }
    
    public boolean isTrackEffortEnabled() {
        return trackEffort;
    }

    /**
     * Reconnect with settings, used in last Connect() call.
     * 
     * @throws Exception
     */
    public void reconnect() throws Exception {
        connect(path, userName, password, integrated);
    }

    Double getEffort(Asset asset) {
        return efforts.get(asset);
    }

    void setEffort(Asset asset, Double value) {
        if (value == null || value == 0){
            efforts.remove(asset);
        } else {
            efforts.put(asset, value);
        }
    }

    public boolean isEffortTrackingRelated(String propertyName) {
        return effortTrackingAttributesList.contains(propertyName);
    }

    void commitAsset(Asset asset) throws V1Exception {
        services.save(asset);
        commitEffort(asset);
    }

    private void commitEffort(Asset asset) throws V1Exception {
        if (efforts.containsKey(asset)) {
            Asset effort = services.createNew(effortType, asset.getOid());
            effort.setAttributeValue(effortType.getAttributeDefinition("Value"), efforts.get(asset));
            effort.setAttributeValue(effortType.getAttributeDefinition("Date"), new Date());
            services.save(effort);
            efforts.remove(asset);
        }
    }

    void revertAsset(Asset asset) {
        asset.rejectChanges();
        efforts.remove(asset);
    }

    public void commitChanges() throws DataLayerException {
        checkConnection();
        try {
            commitAssetsRecursively(Arrays.asList(assetList.getAssets()));
        } catch (V1Exception e) {
            throw warning("Cannot commit changes.", e);
        }
    }

    private void commitAssetsRecursively(List<Asset> assets) throws V1Exception {
        for (Asset asset : assets){
            commitAsset(asset);
            commitAssetsRecursively(asset.getChildren());
        }
    }

    void executeOperation(Asset asset, IOperation operation) throws V1Exception {
        // TODO Auto-generated method stub
    }

    void refreshAsset(Workitem workitem) {
        // TODO Auto-generated method stub

    }

    public void setCurrentProjectId(String value) {
        currentProjectId = value;
        assetList = null;
    }

    public String getCurrentProjectId() {
        return currentProjectId;
    }

    public void setCurrentProject(Workitem value) {
        currentProjectId = value.getId();
        assetList = null;
    }

    public Workitem getCurrentProject() throws Exception {
        if (currentProjectId == null) {
            currentProjectId = "Scope:0";
        }
        return getProjectById(currentProjectId);
    }

    private Workitem getProjectById(String id) throws Exception {
        if (!isConnected) {
            return null;
        }
        if (currentProjectId == null) {
            throw new DataLayerException();// "Current project is not selected"
        }

        Query query = new Query(Oid.fromToken(id, metaModel));
        // clear all diffinitions which was used in previous queries
        alreadyUsedDefinition.clear();
        addSelection(query, Workitem.ProjectPrefix);
        QueryResult result;
        try {
            result = services.retrieve(query);
        } catch (MetaException ex) {
            isConnected = false;
            throw new DataLayerException();// "Unable to get projects", ex
        } catch (Exception ex) {
            throw new DataLayerException();// "Unable to get projects", ex
        }

        if (result.getTotalAvaliable() == 1) {
            return new Workitem(result.getAssets()[0], null);
        }
        return null;
    }
    
    public String localizerResolve(String key) throws DataLayerException {
        try {
            return localizer.resolve(key);
        } catch (Exception ex) {
            throw new DataLayerException();//TODO "Failed to resolve key.", ex
        }
    }

    public boolean tryLocalizerResolve(String key, String result) {
        result = null;

        if (localizer != null) {
            result = localizer.resolve(key);
            return true;
        }

        return false;
    }

}
