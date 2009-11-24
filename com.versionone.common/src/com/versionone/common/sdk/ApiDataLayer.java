package com.versionone.common.sdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import static com.versionone.common.sdk.WorkitemType.*;

import com.versionone.Oid;
import com.versionone.apiclient.AndFilterTerm;
import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.AssetState;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.ConnectionException;
import com.versionone.apiclient.FilterTerm;
import com.versionone.apiclient.IAssetType;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.IFilterTerm;
import com.versionone.apiclient.ILocalizer;
import com.versionone.apiclient.IMetaModel;
import com.versionone.apiclient.IServices;
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
import com.versionone.apiclient.IV1Configuration.TrackingLevel;

public class ApiDataLayer {

    private static final String META_SUFFIX = "meta.v1/";
    private static final String LOCALAIZER_SUFFIX = "loc.v1/";
    private static final String DATA_SUFFIX = "rest-1.v1/";
    private static final String CONFIG_SUFFIX = "config.v1/";

    private static final Map<String, String> propertyAliases = new HashMap<String, String>();
    static {
        propertyAliases.put("DefectStatus", "StoryStatus");
        propertyAliases.put("DefectSource", "StorySource");
        propertyAliases.put("ScopeBuildProjects", "BuildProject");
        propertyAliases.put("TaskOwners", "Member");
        propertyAliases.put("StoryOwners", "Member");
        propertyAliases.put("DefectOwners", "Member");
        propertyAliases.put("TestOwners", "Member");
        propertyAliases.put("TaskScope", "Scope");
        propertyAliases.put("StoryScope", "Scope");
        propertyAliases.put("DefectScope", "Scope");
        propertyAliases.put("TestScope", "Scope");
    }

    private final Map<WorkitemType, IAssetType> types = new HashMap<WorkitemType, IAssetType>(
            WorkitemType.values().length);
    /** All uncommitted Effort records */
    private final Map<Asset, Double> efforts = new HashMap<Asset, Double>();
    private final Set<IAttributeDefinition> alreadyUsedDefinition = new HashSet<IAttributeDefinition>();

    private IAssetType workitemType;
    private IAssetType primaryWorkitemType;
    private IAssetType effortType;

    protected static ApiDataLayer instance;
    private boolean isConnected;
    private boolean testConnection;

    public Oid memberOid;
    private String path;
    private String userName;
    private String password;
    private boolean integrated;

    private List<Asset> assetList;
    /** Set of attributes to be queried in Workitem requests */
    private static Set<AttributeInfo> attributesToQuery = new HashSet<AttributeInfo>();
    private Map<String, PropertyValues> listPropertyValues;

    private boolean trackEffort;
    public final EffortTrackingLevel trackingLevel = new EffortTrackingLevel();

    private IMetaModel metaModel;
    private IServices services;
    private ILocalizer localizer;

    RequiredFieldsValidator requiredFieldsValidator;

    private String currentProjectId;
    private boolean showAllTasks = true;

    protected ApiDataLayer() {
        addProperty("Schedule.EarliestActiveTimebox", Scope, false);
    }

    /**
     * Special method ONLY for testing.
     */
    public void connectFotTesting(Object services, Object metaModel, Object localizer, Object storyTrackingLevel,
            Object defectTrackingLevel) throws Exception {
        this.metaModel = (IMetaModel) metaModel;
        this.services = (IServices) services;
        this.localizer = (ILocalizer) localizer;

        if (storyTrackingLevel != null && defectTrackingLevel != null) {
            trackEffort = true;
            effortType = this.metaModel.getAssetType("Actual");
            trackingLevel.clear();
            trackingLevel.addPrimaryTypeLevel(Story, (TrackingLevel) storyTrackingLevel);
            trackingLevel.addPrimaryTypeLevel(Defect, (TrackingLevel) defectTrackingLevel);
        } else {
            trackEffort = false;
        }

        initTypes();
        testConnection = isConnected = true;
        memberOid = this.services.getLoggedIn();
        listPropertyValues = getListPropertyValues();
        requiredFieldsValidator = new RequiredFieldsValidator(this.metaModel, this.services);
    }

    public static ApiDataLayer getInstance() {
        if (instance == null) {
            instance = new ApiDataLayer();
        }
        return instance;
    }

    public void connect(String path, String userName, String password, boolean integrated) throws DataLayerException {
        if (testConnection) {
            return;
        }
        isConnected = false;
        boolean isUserChanged = true;
        if ((this.userName != null || integrated) && this.path != null) {
            isUserChanged = (this.userName != null && !this.userName.equals(userName)) || integrated != this.integrated
                    || !this.path.equals(path);
        }

        this.path = path;
        this.userName = userName;
        this.password = password;
        this.integrated = integrated;
        assetList = null;
        boolean isUpdateData = isUserChanged || metaModel == null || localizer == null || services == null;

        try {
            if (isUpdateData) {
                cleanConnectionData();

                V1APIConnector metaConnector = new V1APIConnector(path + META_SUFFIX, userName, password);
                metaModel = new MetaModel(metaConnector);

                V1APIConnector localizerConnector = new V1APIConnector(path + LOCALAIZER_SUFFIX, userName, password);
                localizer = new Localizer(localizerConnector);

                V1APIConnector dataConnector = new V1APIConnector(path + DATA_SUFFIX, userName, password);
                services = new Services(metaModel, dataConnector);

            }
            if (types.isEmpty()) {
                initTypes();
            }
            processConfig(path);

            memberOid = services.getLoggedIn();
            listPropertyValues = getListPropertyValues();
            isConnected = true;
            updateCurrentProjectId();
            requiredFieldsValidator = new RequiredFieldsValidator(metaModel, services);
            requiredFieldsValidator.init();

            return;
        } catch (MetaException e) {
            throw warning("Cannot connect to V1 server.", e);
        } catch (Exception e) {
            throw warning("Cannot connect to V1 server.", e);
        }
    }

    private void processConfig(String path) throws ConnectionException, APIException {
        V1Configuration v1Config = new V1Configuration(new V1APIConnector(path + CONFIG_SUFFIX));

        trackEffort = v1Config.isEffortTracking();
        if (trackEffort) {
            effortType = metaModel.getAssetType("Actual");
        }

        trackingLevel.clear();
        trackingLevel.addPrimaryTypeLevel(Story, v1Config.getStoryTrackingLevel());
        trackingLevel.addPrimaryTypeLevel(Defect, v1Config.getDefectTrackingLevel());
    }

    private void cleanConnectionData() {
        efforts.clear();
        types.clear();
        workitemType = null;
        primaryWorkitemType = null;
    }

    private void initTypes() {
        for (WorkitemType type : WorkitemType.values()) {
            types.put(type, metaModel.getAssetType(type.name()));
        }
        workitemType = metaModel.getAssetType("Workitem");
        primaryWorkitemType = metaModel.getAssetType("PrimaryWorkitem");
    }

    /**
     * Reconnect with settings, used in last Connect() call.
     * 
     * @throws DataLayerException
     */
    public void reconnect() throws DataLayerException {
        connect(path, userName, password, integrated);
    }

    public List<Entity> getProjectTree() throws DataLayerException {
        checkConnection();
        try {
            final IAssetType projectType = types.get(Scope);
            final Query scopeQuery = new Query(projectType, projectType.getAttributeDefinition("Parent"));
            final FilterTerm stateTerm = new FilterTerm(projectType.getAttributeDefinition("AssetState"));
            stateTerm.NotEqual(AssetState.Closed);
            scopeQuery.setFilter(stateTerm);
            // clear all definitions used in previous queries
            alreadyUsedDefinition.clear();
            addSelection(scopeQuery, Scope);
            final QueryResult result = services.retrieve(scopeQuery);
            final List<Entity> roots = new ArrayList<Entity>(result.getAssets().length);
            for (Asset oneAsset : result.getAssets()) {
                roots.add(new Entity(oneAsset, null));
            }
            return roots;
        } catch (Exception ex) {
            throw warning("Can't get projects list.", ex);
        }
    }

    public List<Entity> getWorkitemTree() throws Exception {
        checkConnection();
        if (currentProjectId == null) {
            currentProjectId = getDefaultProjectId();
        }
        if (assetList == null) {
            try {
                IAttributeDefinition parentDef = workitemType.getAttributeDefinition("Parent");
                Query query = new Query(workitemType, parentDef);

                // clear all definitions used in previous queries
                alreadyUsedDefinition.clear();
                for (WorkitemType type : WorkitemType.values()) {
                    if (type.isWorkitem()) {
                        addSelection(query, type);
                    }
                }

                query.setFilter(getScopeFilter(workitemType));
                query.getOrderBy().majorSort(primaryWorkitemType.getDefaultOrderBy(), OrderBy.Order.Ascending);
                query.getOrderBy().minorSort(workitemType.getDefaultOrderBy(), OrderBy.Order.Ascending);

                QueryResult result = services.retrieve(query);
                assetList = new ArrayList<Asset>(result.getAssets().length + 20);
                assetList.addAll(Arrays.asList(result.getAssets()));
            } catch (MetaException ex) {
                throw warning("Unable to get workitems.", ex);
            } catch (Exception ex) {
                throw warning("Unable to get workitems.", ex);
            }
        }
        List<Entity> res = new ArrayList<Entity>(assetList.size());
        for (Asset asset : assetList) {
            if (isShowed(asset)) {
                res.add(new Entity(asset, null));
            }
        }
        return res;
    }

    /**
     * Sets visibility for workitems
     * 
     * @param showAllTasks
     *            true - all workitems can be shown false - only changed, new
     *            and workitem with current owner can be shown
     */
    public void setShowAllTasks(boolean showAllTasks) {
        this.showAllTasks = showAllTasks;
    }

    /**
     * Determines whether this Asset can be showed or no.
     * 
     * @param asset
     *            to determine visibility status.
     * @return true if Asset can be showed at the moment; otherwise - false.
     */
    public boolean isShowed(Asset asset) {
        if (showAllTasks || asset.hasChanged() || asset.getOid().isNull()) {
            return true;
        }

        final Attribute attribute = asset.getAttribute(workitemType.getAttributeDefinition(Entity.OWNERS_PROPERTY));
        final Object[] owners = attribute.getValues();
        for (Object oid : owners) {
            if (memberOid.equals(oid)) {
                return true;
            }
        }

        for (Asset child : asset.getChildren()) {
            if (isShowed(child)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkConnection(String url, String user, String pass, boolean auth) {
        boolean result = true;

        V1APIConnector metaConnector = new V1APIConnector(url.toString() + META_SUFFIX);
        MetaModel model = new MetaModel(metaConnector);

        V1APIConnector dataConnector = null;
        if (auth) {
            dataConnector = new V1APIConnector(url.toString() + DATA_SUFFIX);
        } else {
            dataConnector = new V1APIConnector(url.toString() + DATA_SUFFIX, user, pass);
        }

        Services v1Service = new Services(model, dataConnector);

        try {
            v1Service.getLoggedIn();
        } catch (V1Exception e) {
            result = false;
        } catch (MetaException e) {
            result = false;
        }

        return result;
    }

    private void checkConnection() throws DataLayerException {
        if (!isConnected) {
            reconnect();
            if (!isConnected) {
                throw warning("Connection is not set.");
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

    // TODO refactor
    // need to make AlreadyUsedDefinition.Clear(); before first call of this
    // method
    private void addSelection(Query query, WorkitemType type) throws DataLayerException {
        for (AttributeInfo attrInfo : attributesToQuery) {
            if (attrInfo.type == type) {
                try {
                    IAttributeDefinition def = types.get(attrInfo.type).getAttributeDefinition(attrInfo.attr);
                    if (!alreadyUsedDefinition.contains(def)) {
                        query.getSelection().add(def);
                        alreadyUsedDefinition.add(def);
                    }
                } catch (MetaException e) {
                    warning("Wrong attribute: " + attrInfo, e);
                }
            }
        }
        if (requiredFieldsValidator.getFields(type) == null) {
            return;
        }

        for (RequiredFieldsDTO field : requiredFieldsValidator.getFields(type)) {
            try {
                IAttributeDefinition def = types.get(type).getAttributeDefinition(field.name);
                if (!alreadyUsedDefinition.contains(def)) {
                    query.getSelection().add(def);
                    alreadyUsedDefinition.add(def);
                }
            } catch (MetaException e) {
                warning("Wrong attribute: " + field.name, e);
            }
        }
    }

    private void addSelection(Query query, WorkitemType type, boolean clearDefinitions) throws DataLayerException {
        if (clearDefinitions) {
            alreadyUsedDefinition.clear();
        }
        addSelection(query, type);
    }

    public void addProperty(String attr, WorkitemType type, boolean isList) {
        attributesToQuery.add(new AttributeInfo(attr, type, isList));
    }

    private Map<String, PropertyValues> getListPropertyValues() throws Exception {
        Map<String, PropertyValues> res = new HashMap<String, PropertyValues>(attributesToQuery.size());
        for (AttributeInfo attrInfo : attributesToQuery) {
            if (!attrInfo.isList) {
                continue;
            }

            String propertyAlias = attrInfo.type + attrInfo.attr;
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

    static String resolvePropertyKey(String propertyAlias) {
        if (propertyAliases.containsKey(propertyAlias)) {
            return propertyAliases.get(propertyAlias);
        }

        return propertyAlias;
    }

    PropertyValues queryPropertyValues(String propertyName) throws ConnectionException, APIException, OidException,
            MetaException {
        PropertyValues res = new PropertyValues();
        IAssetType assetType = metaModel.getAssetType(propertyName);
        IAttributeDefinition nameDef = assetType.getAttributeDefinition(Entity.NAME_PROPERTY);
        IAttributeDefinition inactiveDef = null;

        Query query = new Query(assetType);
        query.getSelection().add(nameDef);

        try {// Some properties may not have INACTIVE attribute
            inactiveDef = assetType.getAttributeDefinition("Inactive");
        } catch (Exception ex) {
            // do nothing
        }
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

    public PropertyValues getListPropertyValues(WorkitemType type, String propertyName) {
        String propertyKey = resolvePropertyKey(type + propertyName);
        return listPropertyValues.get(propertyKey);
    }

    static DataLayerException warning(String string, Exception ex) {
        System.out.println(string);
        ex.printStackTrace();
        return new DataLayerException(string, ex);
    }

    static DataLayerException warning(String string) {
        System.out.println(string);
        return new DataLayerException(string);
    }

    public boolean isTrackEffortEnabled() {
        return trackEffort;
    }

    Double getEffort(Asset asset) {
        return efforts.get(asset);
    }

    void setEffort(Asset asset, Double value) {
        if (value == null || value == 0) {
            efforts.remove(asset);
        } else {
            efforts.put(asset, value);
        }
    }

    void commitAsset(Asset asset) throws V1Exception, DataLayerException, ValidatorException {
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
        Map<Asset, List<RequiredFieldsDTO>> requiredData = null;
        try {
            requiredData = requiredFieldsValidator.validate(assetList);

            if (!requiredData.isEmpty()) {
                String message = requiredFieldsValidator.createErrorMessage(requiredData);
                throw new ValidatorException(message);
            }
        } catch (APIException e) {
            throw warning("Cannot validate required fields.", e);
        }

        try {
            commitAssetsRecursively(assetList);
        } catch (V1Exception e) {
            throw warning("Cannot commit changes.", e);
        }
    }

    private void commitAssetsRecursively(List<Asset> assets) throws V1Exception, DataLayerException {
        for (Asset asset : assets) {
            commitAsset(asset);
            commitAssetsRecursively(asset.getChildren());
        }
    }

    void executeOperation(Asset asset, IOperation operation) throws V1Exception {
        services.executeOperation(operation, asset.getOid());
    }

    boolean isAssetClosed(Asset asset) {
        try {
            IAttributeDefinition stateDef = asset.getAssetType().getAttributeDefinition("AssetState");
            AssetState state = AssetState.valueOf((Integer) asset.getAttribute(stateDef).getValue());
            return state == AssetState.Closed;
        } catch (MetaException e) {
        } catch (APIException e) {
        }
        return false;
    }

    /**
     * Update specified Workitem in cache from the server. Information about
     * children isn't queried and isn't updated.
     * 
     * @param workitem
     *            to update
     * @throws DataLayerException
     */
    void refreshWorkitem(Entity workitem) throws DataLayerException {
        try {
            final Asset oldAsset = workitem.asset;
            final IAttributeDefinition stateDef = oldAsset.getAssetType().getAttributeDefinition("AssetState");
            final Query query = new Query(oldAsset.getOid().getMomentless(), false);
            addSelection(query, workitem.getType(), true);
            query.getSelection().add(stateDef);
            final QueryResult queryRes = services.retrieve(query);
            Assert.isTrue(queryRes.getTotalAvaliable() == 1, "Query should return exactly one asset.");
            final Asset newAsset = queryRes.getAssets()[0];

            if (workitem.getType().isPrimary()) {
                Assert.isTrue(Collections.replaceAll(assetList, oldAsset, newAsset), "assetList must contains asset:"
                        + oldAsset);
            } else {
                Assert.isTrue(Collections.replaceAll(workitem.parent.asset.getChildren(), oldAsset, newAsset),
                        "parent Asset:" + workitem.parent.asset + " must contains asset:" + oldAsset
                                + " in its children.");
            }
            newAsset.getChildren().addAll(oldAsset.getChildren());
        } catch (MetaException ex) {
            throw warning("Unable to get workitems.", ex);
        } catch (Exception ex) {
            throw warning("Unable to get workitems.", ex);
        }
    }

    /**
     * Removes Workitem from Workitem cache. So on next getWorkitemTree call it
     * won't returned.
     * 
     * @param item
     *            to remove.
     */
    void removeWorkitem(Entity item) {
        if (item.getType().isPrimary()) {
            assetList.remove(item.asset);
        } else if (item.getType().isSecondary()) {
            item.parent.asset.getChildren().remove(item.asset);
        } else {
            throw new IllegalArgumentException("Only Workitems can be removed.");
        }
    }

    public void setCurrentProjectId(String value) {
        if (isProjectExist(value)) {
            currentProjectId = value;
        } else {
            currentProjectId = getDefaultProjectId();
        }
        assetList = null;
    }

    public String getCurrentProjectId() {
        return currentProjectId;
    }

    public void setCurrentProject(Entity value) {
        currentProjectId = value.getId();
        assetList = null;
    }

    public Entity getCurrentProject() throws DataLayerException {
        if (currentProjectId == null || currentProjectId.equals("")) {
            currentProjectId = getDefaultProjectId();
        }
        return getProjectById(currentProjectId);
    }

    public String getCurrentMemberToken() {
        return memberOid != null ? memberOid.getToken() : null;
    }

    private String getDefaultProjectId() {
        String id = "";

        Query query = new Query(types.get(Scope));

        QueryResult result = null;
        try {
            result = services.retrieve(query);
        } catch (Exception ex) {
        }

        if (result != null && result.getTotalAvaliable() > 0) {
            id = result.getAssets()[0].getOid().getMomentless().getToken();
        }

        return id;
    }

    /***
     * Update current project Id to the root project Id from current server
     */
    public String updateCurrentProjectId() {
        currentProjectId = getDefaultProjectId();
        return currentProjectId;
    }

    private boolean isProjectExist(String id) {
        try {
            final Query query = new Query(Oid.fromToken(id, metaModel));
            alreadyUsedDefinition.clear();
            addSelection(query, Scope);
            final QueryResult result = services.retrieve(query);
            return result.getTotalAvaliable() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    private Entity getProjectById(String id) throws DataLayerException {
        if (!isConnected || id == null || id.equals("")) {
            return null;
        }

        QueryResult result;
        try {
            Query query = new Query(Oid.fromToken(id, metaModel));
            // clear all definitions used in previous queries
            alreadyUsedDefinition.clear();
            addSelection(query, Scope);
            result = services.retrieve(query);
        } catch (MetaException ex) {
            isConnected = false;
            throw warning("Unable to get projects", ex);
        } catch (Exception ex) {
            throw warning("Unable to get projects", ex);
        }

        if (result.getTotalAvaliable() == 1) {
            return new Entity(result.getAssets()[0], null);
        }
        return null;
    }

    public String localizerResolve(String key) {
        return localizer.resolve(key);
    }

    /**
     * 
     * @param type
     * @param parent
     * @return newly created Workitem.
     * @throws DataLayerException
     * @throws IllegalArgumentException
     *             when prefix or parent inn't a Workitem, or trying to create a
     *             wrong Workitem hierarchy.
     */
    public Entity createNewWorkitem(WorkitemType type, Entity parent) throws DataLayerException {
        try {
            if (!type.isWorkitem()) {
                throw new IllegalArgumentException("Can only create Workitems, " + "but received: " + type
                        + " for parent: " + parent.getType());
            }
            final Asset asset = new Asset(types.get(type));
            for (AttributeInfo attrInfo : attributesToQuery) {
                if (attrInfo.type == type) {
                    setAssetAttribute(asset, attrInfo.attr, null);
                }
            }

            loadAssetAttribute(asset, "Scope.Name", getCurrentProject().getProperty(Entity.NAME_PROPERTY));

            if (type.isPrimary()) {
                if (parent != null) {
                    throw new IllegalArgumentException("Cannot create " + type + " as children of " + parent.getType());
                }
                return createPrimaryWorkitem(asset);
            }
            return createSecondaryWorkitem(asset, parent);
        } catch (MetaException e) {
            throw new DataLayerException("Cannot create workitem: " + type, e);
        } catch (APIException e) {
            throw new DataLayerException("Cannot create workitem: " + type, e);
        }
    }

    private Entity createPrimaryWorkitem(Asset asset) throws MetaException, APIException, DataLayerException {
        final Entity item = new Entity(asset, null);
        setAssetAttribute(asset, "Scope", currentProjectId);
        setAssetAttribute(asset, "Timebox", getCurrentProject().getProperty("Schedule.EarliestActiveTimebox"));
        loadAssetAttribute(asset, "Timebox.Name", getCurrentProject()
                .getProperty("Schedule.EarliestActiveTimebox.Name"));
        assetList.add(asset);
        return item;
    }

    private Entity createSecondaryWorkitem(Asset asset, Entity parent) throws MetaException, APIException,
            DataLayerException {
        if (parent == null || parent.getType().isSecondary()) {
            throw new IllegalArgumentException("Cannot create " + asset.getAssetType() + " as children of " + parent);
        }
        setAssetAttribute(asset, "Parent", parent.asset.getOid());

        loadAssetAttribute(asset, "Parent.Name", parent.getProperty(Entity.NAME_PROPERTY));
        loadAssetAttribute(asset, "Timebox.Name", parent.getProperty("Timebox.Name"));

        final Entity item = new Entity(asset, parent);
        parent.children.add(item);
        parent.asset.getChildren().add(item.asset);
        return item;
    }

    /**
     * Set or ensure Asset attribute value.
     * 
     * @param value
     *            of the attribute; if null or Oid.Null then attribute will be
     *            just ensured.
     * @throws MetaException
     *             if something wrong with attribute name.
     * @throws APIException
     *             if something wrong with attribute setting/ensuring.
     */
    private static void setAssetAttribute(final Asset asset, final String attrName, final Object value)
            throws MetaException, APIException {
        final IAssetType type = asset.getAssetType();
        IAttributeDefinition def = type.getAttributeDefinition(attrName);
        if (value == null || (value instanceof Oid && ((Oid) value).isNull())) {
            asset.ensureAttribute(def);
        } else {
            asset.setAttributeValue(def, value);
        }
    }

    private void loadAssetAttribute(Asset asset, String string, Object property) throws APIException {
        final IAttributeDefinition def = asset.getAssetType().getAttributeDefinition(string);
        asset.loadAttributeValue(def, property);
    }
}