package com.versionone.common.sdk;

import com.versionone.apiclient.IV1Configuration.TrackingLevel;

public enum EffortTrackingLevel {
    PRIMARY_WORKITEM(TrackingLevel.On), 
    SECONDARY_WORKITEM(TrackingLevel.Off), 
    BOTH(TrackingLevel.Mix);

    private TrackingLevel level;

    EffortTrackingLevel(TrackingLevel level) {
        this.level = level;
    }

    static EffortTrackingLevel translate(TrackingLevel level) {
        for (EffortTrackingLevel myLevel : EffortTrackingLevel.values()){
            if (myLevel.level == level){
                return myLevel;
            }
        }
        throw new UnsupportedOperationException("Unknown tracking level");
    }
}
