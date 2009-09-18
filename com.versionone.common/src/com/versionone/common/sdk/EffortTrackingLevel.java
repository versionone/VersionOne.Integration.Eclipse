package com.versionone.common.sdk;

import java.util.HashSet;
import java.util.Set;

import com.versionone.apiclient.IV1Configuration.TrackingLevel;

public class EffortTrackingLevel {

    /**
     * Set of workitem's tokens to be tracked on. Token for primary workitem is
     * just a it's type prefix, e.g. "Story". Token for secondary workitem is a
     * parent type prefix + dot + own type prefix, e.g. "Story.Task".
     */
    private final Set<String> tokens = new HashSet<String>(6);
    private String[] secondaryTypes = {};

    public EffortTrackingLevel(String... secondaryTypes) {
        setSecondaryTypes(secondaryTypes);
    }

    boolean isTracking(Workitem item) {
        String token = item.getTypePrefix();
        if (item.parent != null) {
            token = item.parent.getTypePrefix() + "." + token;
        }
        return tokens.contains(token);
    }

    public void clear() {
        tokens.clear();
    }

    public void setSecondaryTypes(String... typePrefixes) {
        secondaryTypes = typePrefixes;
    }

    public void addPrimaryTypeLevel(String typePrefix, TrackingLevel trackingLevel) {
        switch (trackingLevel) {
        case On:
            tokens.add(typePrefix);
            break;
        case Off:
            addSecondaryTypeLevel(typePrefix);
            break;
        case Mix:
            tokens.add(typePrefix);
            addSecondaryTypeLevel(typePrefix);
            break;
        }
    }

    private void addSecondaryTypeLevel(String parentType) {
        for (String type : secondaryTypes) {
            tokens.add(parentType + "." + type);
        }
    }
}
