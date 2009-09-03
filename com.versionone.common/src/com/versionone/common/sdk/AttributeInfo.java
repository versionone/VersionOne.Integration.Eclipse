package com.versionone.common.sdk;

public class AttributeInfo {
    public final String attr;
    public final String prefix;
    public final boolean isList;

    public AttributeInfo(String attr, String prefix, boolean isList) {
        if (attr == null || prefix == null) {
            throw new IllegalArgumentException("Parameters cannot be null.");
        }
        this.attr = attr;
        this.prefix = prefix;
        this.isList = isList;
    }

    @Override
    public String toString() {
        return prefix + "." + attr + "(List:" + Boolean.toString(isList) + ")";
    }

    @Override
    public int hashCode() {
        int result = 31 + attr.hashCode();
        result = 31 * result + prefix.hashCode();
        result = 31 * result + (isList ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof AttributeInfo))
            return false;
        AttributeInfo other = (AttributeInfo) obj;
        if (!attr.equals(other.attr))
            return false;
        if (!prefix.equals(other.prefix))
            return false;
        if (isList != other.isList)
            return false;
        return true;
    }

}
