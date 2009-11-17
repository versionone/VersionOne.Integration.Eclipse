package com.versionone.common.sdk;

public enum WorkitemType {

    Story(true),
    Defect(true),
    Test(false),
    Task(false),
    Scope;

    public final Boolean isPrimary;

    private WorkitemType() {
        isPrimary = null;
    }

    private WorkitemType(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public boolean isWorkitem() {
        return isPrimary != null;
    }
    
    public boolean isPrimary() {
        return isWorkitem() && isPrimary;
    }

    public boolean isSecondary() {
        return isWorkitem() && !isPrimary;
    }
}
