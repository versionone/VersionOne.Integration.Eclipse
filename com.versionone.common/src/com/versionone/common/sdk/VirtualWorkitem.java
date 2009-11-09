package com.versionone.common.sdk;

import com.versionone.apiclient.Asset;

class VirtualWorkitem extends Workitem {

    VirtualWorkitem(Asset asset, Workitem parent) {
        super(asset, parent);
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
    
    /**
     * 
     * @return always true for this object
     */
    public boolean isNew() {
        return true;
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
    public boolean equals(Object o) {
        return this == o;
    }
    
    @Override
    public int hashCode() {
        return this.asset.hashCode();
    }
}
