package com.versionone.common.sdk;

public class AttributeInfo {
    public final String attr;
    public final String prefix;
    public final boolean isList;

    public AttributeInfo(String attr, String prefix, boolean isList) {
        this.attr = attr;
        this.prefix = prefix;
        this.isList = isList;
    }

    @Override
    public String toString() {
        return prefix + "." + attr + "(List:" + Boolean.toString(isList) + ")";
    }
}
