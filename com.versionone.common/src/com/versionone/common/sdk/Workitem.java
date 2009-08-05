package com.versionone.common.sdk;

import java.text.NumberFormat;
import java.util.ArrayList;

import com.versionone.Oid;
import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.IAttributeDefinition.AttributeType;

public class Workitem {

    public static final String TaskPrefix = "Task";
    public static final String StoryPrefix = "Story";
    public static final String DefectPrefix = "Defect";
    public static final String TestPrefix = "Test";
    public static final String ProjectPrefix = "Scope";

    public static final String IdProperty = "Number";
    public static final String DetailEstimateProperty = "DetailEstimate";
    public static final String NameProperty = "Name";
    public static final String StatusProperty = "Status";
    public static final String EffortProperty = "Actuals";
    public static final String DoneProperty = "Actuals.Value.@Sum";
    public static final String ScheduleNameProperty = "Schedule.Name";
    public static final String OwnersProperty = "Owners";
    public static final String TodoProperty = "ToDo";

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    static {
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(6);
    }

    protected ApiDataLayer dataLayer = ApiDataLayer.getInstance();
    protected Asset asset;
    public Workitem parent;

    /**
     * List of child Workitems.
     */
    public final ArrayList<Workitem> children;

    Workitem(Asset asset, Workitem parent) {
        this.parent = parent;
        this.asset = asset;
        if (asset == null) {// TODO temporary
            children = new ArrayList<Workitem>();
            if (parent == null) {
                children.add(new Workitem(null, this));
            }
            return;
        }
        children = new ArrayList<Workitem>(asset.getChildren().size());
        for (Asset childAsset : asset.getChildren()) {
            if (getTypePrefix().equals(ProjectPrefix) || dataLayer.showAllTasks
                    || dataLayer.isCurrentUserOwnerAsset(childAsset)) {
                children.add(new Workitem(childAsset, this));
            }
        }
        children.trimToSize();
    }

    public String getTypePrefix() {
        if (asset == null) {// temporary
            return "nULL";
        }
        return asset.getAssetType().getToken();
    }

    public String getId() {
        if (asset == null) {// temporary
            return "nULL";
        }
        return asset.getOid().getMomentless().getToken();
    }

    public boolean hasChanges() {
        return asset.hasChanged();
    }

    public boolean isPropertyReadOnly(String propertyName) {
        String fullName = getTypePrefix() + '.' + propertyName;
        try {
            if (dataLayer.isEffortTrackingRelated(propertyName)) {
                return isEffortTrackingPropertyReadOnly(propertyName);
            }

            return false;
        } catch (Exception e) {
            ApiDataLayer.warning("Cannot get property: " + fullName, e);
            return true;
        }
    }

    public boolean isPropertyDefinitionReadOnly(String propertyName) {
        String fullName = getTypePrefix() + '.' + propertyName;
        try {
            Attribute attribute = asset.getAttributes().get(fullName);
            return attribute.getDefinition().isReadOnly();
        } catch (Exception e) {
            ApiDataLayer.warning("Cannot get property: " + fullName, e);
            return true;
        }
    }

    private boolean isEffortTrackingPropertyReadOnly(String propertyName) {
        if (!dataLayer.isEffortTrackingRelated(propertyName)) {
            throw new IllegalArgumentException("This property is not related to effort tracking.");
        }

        EffortTrackingLevel storyLevel = dataLayer.storyTrackingLevel;
        EffortTrackingLevel defectLevel = dataLayer.defectTrackingLevel;

        if (getTypePrefix().equals(StoryPrefix)) {
            return storyLevel != EffortTrackingLevel.PRIMARY_WORKITEM && storyLevel != EffortTrackingLevel.BOTH;
        } else if (getTypePrefix().equals(DefectPrefix)) {
            return defectLevel != EffortTrackingLevel.PRIMARY_WORKITEM && defectLevel != EffortTrackingLevel.BOTH;
        } else if (getTypePrefix().equals(TaskPrefix) || getTypePrefix().equals(TestPrefix)) {
            EffortTrackingLevel parentLevel;
            if (parent.getTypePrefix().equals(StoryPrefix)) {
                parentLevel = storyLevel;
            } else if (parent.getTypePrefix().equals(DefectPrefix)) {
                parentLevel = defectLevel;
            } else {
                throw new IllegalStateException("Unexpected parent asset type.");
            }
            return parentLevel != EffortTrackingLevel.SECONDARY_WORKITEM && parentLevel != EffortTrackingLevel.BOTH;
        } else {
            throw new IllegalStateException("Unexpected asset type.");
        }
    }

    private PropertyValues getPropertyValues(String propertyName) {
        return dataLayer.getListPropertyValues(getTypePrefix(), propertyName);
    }

    /**
     * Gets property value.
     * 
     * @param propertyName
     *            Name of the property to get, e.g. "Status"
     * @return String, Double, ValueId or IList&lt;ValueId&gt;.
     * @throws IllegalArgumentException
     *             If property cannot be got.
     * @see #NameProperty
     * @see #StatusProperty
     * @see #EffortProperty
     * @see #DoneProperty
     * @see #ScheduleNameProperty
     * @see #OwnersProperty
     * @see #TodoProperty
     */
    public Object getProperty(String propertyName) throws IllegalArgumentException {
        if (propertyName.equals(EffortProperty)) {
            return dataLayer.getEffort(asset);
        }

        Attribute attribute = asset.getAttributes().get(getTypePrefix() + '.' + propertyName);

        if (attribute.getDefinition().isMultiValue()) {
            return getPropertyValues(propertyName).subset(attribute.getValues());
        }

        try {
            Object val = attribute.getValue();
            if (val instanceof Oid) {
                return getPropertyValues(propertyName).find((Oid) val);
            }
            return val;
        } catch (APIException e) {
            throw new IllegalArgumentException("Cannot get property: " + propertyName, e);
        }
    }

    public String getPropertyAsString(String propertyName) throws IllegalArgumentException {
        Object value = getProperty(propertyName);
        if (value == null) {
            return "";
        } else if (value instanceof Double) {
            return numberFormat.format(value);
        }
        return value.toString();
    }

    /**
     * Sets property value.
     * 
     * @param propertyName
     *            Short name of the property to set, e.g. "Name".
     * @param newValue
     *            String, Double, null, ValueId, PropertyValues accepted.
     */
    public void setProperty(String propertyName, Object newValue) {
        try {
            if (propertyName.equals(EffortProperty)) {
                final Double effort;
                if ("".equals(newValue))
                    effort = null;
                else
                    effort = numberFormat.parse((String) newValue).doubleValue();
                dataLayer.setEffort(asset, effort);
                return;
            }
            IAttributeDefinition attrDef = asset.getAssetType().getAttributeDefinition(propertyName);
            if (newValue == null || newValue.equals("")) {
                asset.setAttributeValue(attrDef, null);
                return;
            }
            Attribute attribute = asset.getAttributes().get(getTypePrefix() + '.' + propertyName);
            if (attrDef.isMultiValue()) {
                updateValues(propertyName, attribute.getValues(), (PropertyValues) newValue);
                return;
            }
            if (newValue instanceof ValueId) {
                newValue = ((ValueId) newValue).oid;
            } else if (attrDef.getAttributeType() == AttributeType.Numeric) {
                newValue = numberFormat.parse((String) newValue);
            }

            if (!newValue.equals(attribute.getValue())) {
                asset.setAttributeValue(attrDef, newValue);
            }
        } catch (Exception ex) {
            ApiDataLayer.warning("Cannot set property " + propertyName + " of " + this, ex);
        }
    }

    private void updateValues(String propertyName, Object[] oldValues, PropertyValues newValues) throws APIException {
        IAttributeDefinition attrDef = asset.getAssetType().getAttributeDefinition(propertyName);
        for (Object oldOid : oldValues) {
            if (!newValues.containsOid((Oid) oldOid)) {
                asset.removeAttributeValue(attrDef, oldOid);
            }
        }
        for (ValueId newValue : newValues) {
            if (!checkContains(oldValues, newValue.oid)) {
                asset.addAttributeValue(attrDef, newValue.oid);
            }
        }
    }

    private boolean checkContains(Object[] array, Object value) {
        for (Object item : array) {
            if (item.equals(value))
                return true;
        }
        return false;
    }

    public boolean propertyChanged(String propertyName) {
        IAttributeDefinition attrDef = asset.getAssetType().getAttributeDefinition(propertyName);
        return asset.getAttribute(attrDef).hasChanged();
    }

    public void commitChanges() throws DataLayerException {
        try {
            dataLayer.commitAsset(asset);
        } catch (APIException e) {
            throw ApiDataLayer.warning("Failed to commit changes.", e);
        }
    }

    public boolean isMine() {
        PropertyValues owners = (PropertyValues) getProperty(OwnersProperty);
        return owners.containsOid(dataLayer.memberOid);
    }

    public boolean canQuickClose() {
        try {
            return (Boolean) getProperty("CheckQuickClose");
        } catch (IllegalArgumentException e) {
            ApiDataLayer.warning("QuickClose not supported.", e);
            return false;
        }
    }

    /*
     * Performs QuickClose operation.
     */
    public void quickClose() throws DataLayerException {
        commitChanges();
        try {
            dataLayer.executeOperation(asset, asset.getAssetType().getOperation("QuickClose"));
            dataLayer.refreshAsset(this);
        } catch (APIException e) {
            throw ApiDataLayer.warning("Failed to QuickClose.", e);
        }
    }

    public boolean canSignup() {
        try {
            return (Boolean) getProperty("CheckQuickSignup");
        } catch (IllegalArgumentException e) {
            ApiDataLayer.warning("QuickSignup not supported.", e);
            return false;
        }
    }

    /*
     * Performs QuickSignup operation.
     */
    public void signup() throws DataLayerException {
        try {
            dataLayer.executeOperation(asset, asset.getAssetType().getOperation("QuickSignup"));
            dataLayer.refreshAsset(this);
        } catch (APIException e) {
            throw ApiDataLayer.warning("Failed to QuickSignup.", e);
        }
    }

    public void close() throws APIException {
        dataLayer.executeOperation(asset, asset.getAssetType().getOperation("Inactivate"));
        dataLayer.refreshAsset(this);
    }

    public void revertChanges() {
        dataLayer.revertAsset(asset);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Workitem)) {
            return false;
        }
        Workitem other = (Workitem) obj;
        if (other.asset.getOid() != asset.getOid()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return asset.getOid().hashCode();
    }

    @Override
    public String toString() {
        return getId() + (asset.hasChanged() ? " (Changed)" : "");
    }
}
