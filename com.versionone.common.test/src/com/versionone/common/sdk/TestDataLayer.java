package com.versionone.common.sdk;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.versionone.apiclient.Asset;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.Entity;

public class TestDataLayer extends ApiDataLayer {

    public static TestDataLayer getInstance() {
        if (!(ApiDataLayer.instance instanceof TestDataLayer)) {
            ApiDataLayer.instance = new TestDataLayer();
        }
        return (TestDataLayer) ApiDataLayer.instance;
    }

    public boolean isEffortTracking;
    public List<Entity> workitemTree = new LinkedList<Entity>();

    private Map<String, PropertyValues> listProperties = new HashMap<String, PropertyValues>();

    @Override
    public void removeWorkitem(Entity item) {
    }

    @Override
    public void addProperty(String attr, WorkitemType type, boolean isList) {
    }

    public void setListProperty(String attr, WorkitemType type, PropertyValues values) {
        listProperties.put(type + attr, values);
    }

    @Override
    public boolean checkConnection(String url, String user, String pass, boolean auth) {
        return true;
    }

    @Override
    public void commitChanges() throws DataLayerException {
    }

    @Override
    public void connect(String path, String userName, String password, boolean integrated) throws DataLayerException {
    }

    @Override
    public void connectFotTesting(Object services, Object metaModel, Object localizer, Object storyTL, Object defectTL)
            throws Exception {
    }

    @Override
    public String getCurrentMemberToken() {
        return "Member:20";
    }

    @Override
    public Entity getCurrentProject() {
        return new WorkitemMock("0", WorkitemType.Scope);
    }

    @Override
    public String getCurrentProjectId() {
        return getCurrentProject().getId();
    }

    @Override
    public PropertyValues getListPropertyValues(WorkitemType type, String propertyName) {
        final PropertyValues res = listProperties.get(type + propertyName);
        return res == null ? new PropertyValuesMock("") : res;
    }

    @Override
    public List<Entity> getProjectTree() throws DataLayerException {
        return Arrays.asList(getCurrentProject());
    }

    @Override
    public List<Entity> getWorkitemTree() throws Exception {
        return workitemTree;
    }

    @Override
    public boolean isAssetClosed(Asset asset) {
        return false;
    }

    @Override
    public boolean isShowed(Asset asset) {
        return false;
    }

    @Override
    public boolean isTrackEffortEnabled() {
        return isEffortTracking;
    }

    @Override
    public String localizerResolve(String key) {
        if (key.startsWith("ColumnTitle'")) {
            if (key.equals("ColumnTitle'DetailEstimate")) {
                return "Detail Estimate";
            } else if (key.equals("ColumnTitle'ToDo")) {
                return "To Do";
            }
            return key.substring("ColumnTitle'".length());
        }
        return key;
    }

    @Override
    public void reconnect() throws DataLayerException {
    }

    @Override
    public void setCurrentProject(Entity value) {
    }

    @Override
    public void setCurrentProjectId(String value) {
    }

    @Override
    public String updateCurrentProjectId() {
        return getCurrentProjectId();
    }

}
