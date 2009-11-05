package com.versionone.common.sdk;

import com.versionone.apiclient.Asset;
import com.versionone.apiclient.IAssetType;

class VirtualWorkitem extends Workitem {

    VirtualWorkitem(IAssetType type, Workitem parent) {
        super(new Asset(type), parent);
        if (parent != null) {
            parent.children.add(this);
        }
    }

    @Override
    public boolean canQuickClose() {
        return false;
    }

    @Override
    public boolean canSignup() {
        return false;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Cannot close non-saved workitem.");
    }

    @Override
    public void quickClose() throws DataLayerException {
        throw new UnsupportedOperationException("Cannot close non-saved workitem.");
    }

    @Override
    public void signup() throws DataLayerException {
        throw new UnsupportedOperationException("Cannot signup on non-saved workitem.");
    }

    @Override
    public void revertChanges() {
        throw new UnsupportedOperationException("Cannot revert non-saved workitem.");
    }

    @Override
    public boolean hasChanges() {
        return true;
    }

    @Override
    public Object getProperty(String propertyName) throws IllegalArgumentException {
        Object res = null;
        try {
            res = super.getProperty(propertyName);
        } catch (IllegalArgumentException e) {
            // Do nothing
        }
        return res;
    }

    @Override
    public boolean isMine() {
        final PropertyValues owners = (PropertyValues) getProperty(OWNERS_PROPERTY);
        return owners==null? false:owners.containsOid(dataLayer.memberOid);
    }

}
