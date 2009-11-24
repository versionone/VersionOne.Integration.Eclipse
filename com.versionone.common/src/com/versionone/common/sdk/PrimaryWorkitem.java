package com.versionone.common.sdk;

import java.util.ArrayList;
import java.util.List;

import com.versionone.apiclient.Asset;

public class PrimaryWorkitem extends Workitem {

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

    /** Just call {@link ApiDataLayer.createNewSecondaryWorkitem()} */
    public SecondaryWorkitem createChild(EntityType type) throws DataLayerException {
        return dataLayer.createNewSecondaryWorkitem(type, this);
    }
}
