package com.versionone.common.sdk;

import com.versionone.Oid;

public class ValueId {

    public Oid oid;
    private final String name;
    
    public ValueId() {
        this(Oid.Null, "");        
    }
    
    protected ValueId(Oid oid, String name) {
        this.oid = oid.getMomentless();
        this.name = name;
    }

    @Override
    public boolean equals(Object value) {
        if (value == this) {
            return true;
        }
        if (!(value instanceof ValueId)) {
            return false;
        }
        
        ValueId newValue = (ValueId)value;
        
        return this.oid.equals(newValue.oid);
    }
    
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + oid.hashCode();
        return hash;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
