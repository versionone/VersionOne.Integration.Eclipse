package com.versionone.common.sdk;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.versionone.common.sdk.Workitem;

public class WorkitemMock extends Workitem {

    public final String id;
    public final WorkitemType type;
    public final Map<String, Object> properties = new HashMap<String, Object>();
    public boolean hasChanges;

    public WorkitemMock() {
        this(null, null);
    }

    public WorkitemMock(String id, WorkitemType prefix) {
        this(prefix, id, null);
    }

    public WorkitemMock(WorkitemType type, String id, Workitem parent) {
        super(new LinkedList<Workitem>(), parent);
        this.type = type;
        this.id = id == null ? "" : type.name();
        if (parent != null)
            parent.children.add(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getProperty(String propertyName) {
        final Object res = properties.get(propertyName);
        return res == null ? "***Not defined***" : res;
    }

    @Override
    public WorkitemType getType() {
        return type;
    }

    @Override
    public boolean hasChanges() {
        return hasChanges;
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        properties.put(propertyName, newValue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = (hasChanges ? 1231 : 1237);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        WorkitemMock other = (WorkitemMock) obj;
        if (hasChanges != other.hasChanges)
            return false;
        if (!id.equals(other.id))
            return false;
        if (!type.equals(other.type))
            return false;
        return true;
    }

}
