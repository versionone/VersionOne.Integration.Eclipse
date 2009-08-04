package com.versionone.common.sdk;

import java.util.ArrayList;
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

public class ApiDataLayer {

    private final String MetaUrlSuffix = "meta.v1/";
    private final String LocalizerUrlSuffix = "loc.v1/";
    private final String DataUrlSuffix = "rest-1.v1/";
    private final String ConfigUrlSuffix = "config.v1/";

    private Map<String, IAssetType> types;
    private IAssetType projectType;
    private IAssetType taskType;
    private IAssetType testType;
    private IAssetType defectType;
    private IAssetType storyType;
    private IAssetType workitemType;
    private IAssetType primaryWorkitemType;
    private IAssetType effortType;

    private Map<Asset, Double> efforts;

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
    private List<Asset> allAssets;
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
        String[] prefixes = new String[] {
            Workitem.TaskPrefix, 
            Workitem.DefectPrefix, 
            Workitem.StoryPrefix, 
            Workitem.TestPrefix
        };
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
        try {
            V1APIConnector metaConnector = new V1APIConnector(path + MetaUrlSuffix, userName, password);
            metaModel = new MetaModel(metaConnector);

            V1APIConnector localizerConnector = new V1APIConnector(path + LocalizerUrlSuffix, userName, password);
            localizer = new Localizer(localizerConnector);

            V1APIConnector dataConnector = new V1APIConnector(path + DataUrlSuffix, userName, password);
            services = new Services(metaModel, dataConnector);

            V1Configuration v1Config = new V1Configuration(new V1APIConnector(path + ConfigUrlSuffix));

            types = new HashMap<String, IAssetType>(4);
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
                efforts = new HashMap<Asset, Double>();
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
        // CheckConnection();
        if (currentProjectId == null) {
            // throw new DataLayerException("Current project is not selected");
            // // TODO implement
            //throw new Exception("Current project is not selected");
            currentProjectId = "Scope:0";

        }

        if (assetList == null) {
            try {
                IAttributeDefinition parentDef = workitemType.getAttributeDefinition("Parent");

                Query query = new Query(workitemType, parentDef);
                // Query query = new Query(taskType, parentDef);
                // clear all diffinitions which was used in previous queries
                alreadyUsedDefinition.clear();
                addSelection(query, Workitem.TaskPrefix);
                addSelection(query, Workitem.StoryPrefix);
                addSelection(query, Workitem.DefectPrefix);
                addSelection(query, Workitem.TestPrefix);

                query.setFilter(getScopeFilter(workitemType));

                query.getOrderBy().majorSort(primaryWorkitemType.getDefaultOrderBy(), OrderBy.Order.Ascending);
                query.getOrderBy().minorSort(workitemType.getDefaultOrderBy(), OrderBy.Order.Ascending);

                assetList = services.retrieve(query);
                addRecursive(assetList.getAssets(), allAssets); // TODO implement getting all
                // assets as list (for
                // example create AssetList
                // class from C# SDK)
            } catch (MetaException ex) {
                // throw Warning("Unable to get workitems.", ex);
            }
            /*
             * catch (WebException ex) { isConnected = false; throw
             * Warning("Unable to get workitems.", ex); }
             */
            catch (Exception ex) {
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

    private void addRecursive(Asset[] assets, List<Asset> target) {
        for (Asset asset : assets) {
            target.add(asset);
            if (asset.getChildren().size() > 0) {
                addRecursive(asset.getChildren().toArray(new Asset[asset.getChildren().size()]), target);
            }
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
    private void addSelection(Query query, String typePrefix) {
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
                }
            }
        }
    }

    public void addProperty(String attr, String prefix, boolean isList) {
        attributesToQuery.addLast(new AttributeInfo(attr, prefix, isList));
    }

    public Double getEffort(Asset asset) {
        // TODO Auto-generated method stub
        return null;
    }
    
    private Map<String, PropertyValues> getListPropertyValues() throws ConnectionException, APIException, OidException, MetaException {
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
        } else if (propertyAlias.equals("TaskOwners") || propertyAlias.equals("StoryOwners") || propertyAlias.equals("DefectOwners")
                || propertyAlias.equals("TestOwners")) {
            return "Member";
        }

        return propertyAlias;
    }

    private PropertyValues queryPropertyValues(String propertyName) throws ConnectionException, APIException, OidException, MetaException {
        PropertyValues res = new PropertyValues();
        IAssetType assetType = metaModel.getAssetType(propertyName);
        IAttributeDefinition nameDef = assetType.getAttributeDefinition(Workitem.NameProperty);
        IAttributeDefinition inactiveDef;

        Query query = new Query(assetType);
        query.getSelection().add(nameDef);        
        //if (assetType.TryGetAttributeDefinition("Inactive", out inactiveDef)) {
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

    public void executeOperation(Asset asset, IOperation operation) throws APIException {
        // TODO Auto-generated method stub

    }

    public void refreshAsset(Workitem workitem) {
        // TODO Auto-generated method stub

    }

    public void revertAsset(Asset asset) {
        // TODO Auto-generated method stub

    }
}
