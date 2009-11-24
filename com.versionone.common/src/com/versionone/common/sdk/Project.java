package com.versionone.common.sdk;

import java.util.ArrayList;
import java.util.List;

import com.versionone.apiclient.Asset;

public class Project extends Entity {

    public final Project parent;
    /** List of child Projects. */
    public final List<Project> children;

    Project(ApiDataLayer dataLayer, Asset asset) {
        this(dataLayer, asset, null);
    }
    
    Project(ApiDataLayer dataLayer, Asset asset, Project parent) {
        super(dataLayer, asset);
        this.parent = parent;
        children = new ArrayList<Project>(asset.getChildren().size());
        for (Asset childAsset : asset.getChildren()) {
            children.add(new Project(dataLayer, childAsset, this));
        }
    }
}
