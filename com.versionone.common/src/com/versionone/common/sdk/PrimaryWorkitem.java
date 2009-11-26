package com.versionone.common.sdk;

import java.util.ArrayList;
import java.util.List;

import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.MetaException;

public class PrimaryWorkitem extends Workitem {

    public static final String PARENT_PROPERTY = "Parent";

    /**
     * List of child SecondaryWorkitems.
     */
    public final List<SecondaryWorkitem> children;

    PrimaryWorkitem(ApiDataLayer dataLayer, Asset asset) {
        super(dataLayer, asset);

        children = new ArrayList<SecondaryWorkitem>(asset.getChildren().size());
        for (Asset childAsset : asset.getChildren()) {
            if (dataLayer.isShowed(childAsset)) {
                children.add(new SecondaryWorkitem(dataLayer, childAsset, this));
            }
        }
    }

    @Override
    public void commitChanges() throws DataLayerException {
        final boolean persistent = isPersistent();
        super.commitChanges();
        if (!persistent && !children.isEmpty()) {
            for (SecondaryWorkitem child : children) {
                try {
                    ApiDataLayer.setAssetAttribute(child.asset, PARENT_PROPERTY, asset.getOid());
                } catch (MetaException e) {
                    throw new DataLayerException("Set attribute Parent to asset: " + child.asset, e);
                } catch (APIException e) {
                    throw new DataLayerException("Set attribute Parent to asset: " + child.asset, e);
                }
            }
        }
    }

    /** Just call {@link ApiDataLayer.createNewSecondaryWorkitem()} */
    public SecondaryWorkitem createChild(EntityType type) throws DataLayerException {
        return dataLayer.createNewSecondaryWorkitem(type, this);
    }
}
