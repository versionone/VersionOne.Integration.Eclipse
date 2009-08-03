package com.versionone.common.sdk;

import com.versionone.Oid;

public class ValueId {

    public Oid oid;
    private final String name;
    
    public ValueId() {
        this(Oid.Null, "");        
    }
    
    protected ValueId(Oid oid, String name) {
        oid = oid.getMomentless();
        this.name = name;
    }

}
