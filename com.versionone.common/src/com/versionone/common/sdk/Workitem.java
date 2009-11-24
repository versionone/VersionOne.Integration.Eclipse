package com.versionone.common.sdk;

import com.versionone.apiclient.Asset;
import com.versionone.apiclient.V1Exception;

public abstract class Workitem extends Entity {

    Workitem(ApiDataLayer dataLayer, Asset asset) {
        super(dataLayer, asset);
    }

    public boolean canQuickClose() {
        try {
            return isPersistent() && (Boolean) getProperty("CheckQuickClose");
        } catch (IllegalArgumentException e) {
            ApiDataLayer.warning("QuickClose not supported.", e);
            return false;
        } catch (NullPointerException e) {
            ApiDataLayer.warning("QuickClose not supported.", e);
            return false;
        }
    }

    /**
     * Performs 'QuickClose' operation.
     * 
     * @throws DataLayerException
     */
    public void quickClose() throws DataLayerException {
        checkPersistance("quickClose");
        commitChanges();
        try {
            dataLayer.executeOperation(asset, asset.getAssetType().getOperation(OP_QUICK_CLOSE));
            dataLayer.removeWorkitem(this);
        } catch (V1Exception e) {
            throw ApiDataLayer.warning("Failed to QuickClose workitem: " + this, e);
        }
    }

    public boolean canSignup() {
        try {
            return isPersistent() && (Boolean) getProperty("CheckQuickSignup");
        } catch (IllegalArgumentException e) {
            ApiDataLayer.warning("QuickSignup not supported.", e);
            return false;
        } catch (NullPointerException e) {
            ApiDataLayer.warning("QuickClose not supported.", e);
            return false;
        }
    }

    /**
     * Performs 'QuickSignup' operation.
     * 
     * @throws DataLayerException
     */
    public void signup() throws DataLayerException {
        checkPersistance("signup");
        try {
            dataLayer.executeOperation(asset, asset.getAssetType().getOperation(OP_SIGNUP));
            dataLayer.refreshWorkitem(this);
        } catch (V1Exception e) {
            throw ApiDataLayer.warning("Failed to QuickSignup workitem: " + this, e);
        }
    }

    /**
     * Perform 'Inactivate' operation.
     * 
     * @throws DataLayerException
     */
    public void close() throws DataLayerException {
        checkPersistance("close");
        try {
            dataLayer.executeOperation(asset, asset.getAssetType().getOperation(OP_CLOSE));
            dataLayer.removeWorkitem(this);
        } catch (V1Exception e) {
            throw ApiDataLayer.warning("Failed to Close workitem: " + this, e);
        }
    }
}
