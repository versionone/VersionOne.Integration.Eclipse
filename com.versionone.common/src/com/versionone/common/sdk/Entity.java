package com.versionone.common.sdk;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import com.versionone.Oid;
import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.IAttributeDefinition;
import com.versionone.apiclient.V1Exception;
import com.versionone.apiclient.IAttributeDefinition.AttributeType;

public abstract class Entity {

    public static final String ID_PROPERTY = "Number";
    public static final String DETAIL_ESTIMATE_PROPERTY = "DetailEstimate";
    public static final String NAME_PROPERTY = "Name";
    public static final String STATUS_PROPERTY = "Status";
    public static final String TYPE_PROPERTY = "Category";
    public static final String EFFORT_PROPERTY = "Actuals";
    public static final String DONE_PROPERTY = "Actuals.Value.@Sum";
    public static final String SCHEDULE_NAME_PROPERTY = "Schedule.Name";
    public static final String OWNERS_PROPERTY = "Owners";
    public static final String TODO_PROPERTY = "ToDo";
    public static final String ESTIMATE_PROPERTY = "Estimate";
    public static final String DESCRIPTION_PROPERTY = "Description";
    public static final String PARENT_NAME_PROPERTY = "Parent.Name";
    public static final String CHECK_QUICK_CLOSE_PROPERTY = "CheckQuickClose";
    public static final String CHECK_QUICK_SIGNUP_PROPERTY = "CheckQuickSignup";

    public static final String OP_SIGNUP = "QuickSignup";
    public static final String OP_CLOSE = "Inactivate";
    public static final String OP_QUICK_CLOSE = "QuickClose";

    public static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    static {
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(6);
    }

    protected final ApiDataLayer dataLayer;
    final Asset asset;

    Entity(ApiDataLayer dataLayer, Asset asset) {
        this.asset = asset;
        this.dataLayer = dataLayer;
    }

    public EntityType getType() {
        return EntityType.valueOf(asset.getAssetType().getToken());
    }

    public String getId() {
        return asset.getOid().getMomentless().getToken();
    }

    public boolean hasChanges() {
        return asset.hasChanged();
    }

    private boolean isEffortTrackingRelated(String property) {
        return property.startsWith("Actuals") || property.equals(DETAIL_ESTIMATE_PROPERTY)
                || property.equals(TODO_PROPERTY);
    }

    public boolean isPropertyReadOnly(String propertyName) {
        if (isEffortTrackingRelated(propertyName) && !dataLayer.trackingLevel.isTracking(this)) {
            return true;
        }
        if (propertyName.equals(EFFORT_PROPERTY)) {
            return false;
        }
        return isPropertyDefinitionReadOnly(propertyName);
    }

    private boolean isPropertyDefinitionReadOnly(String propertyName) {
        final String fullName = getType() + "." + propertyName;
        Attribute attribute = asset.getAttributes().get(fullName);
        if (attribute != null)
            return attribute.getDefinition().isReadOnly();
        else {
            ApiDataLayer.warning("Cannot get property: " + fullName);
            return true;
        }
    }

    private PropertyValues getPropertyValues(String propertyName) {
        return dataLayer.getListPropertyValues(getType(), propertyName);
    }

    /**
     * Checks if property value has changed.
     * 
     * @param propertyName
     *            Name of the property to get, e.g. "Status"
     * @return true if property has changed; false - otherwise.
     */
    public boolean isPropertyChanged(String propertyName) throws IllegalArgumentException {
        if (propertyName.equals(EFFORT_PROPERTY)) {
            return dataLayer.getEffort(asset) != null;
        }
        final String fullName = getType() + "." + propertyName;
        Attribute attribute = asset.getAttributes().get(fullName);
        if (attribute == null) {
            throw new IllegalArgumentException("There is no property: " + fullName);
        }
        return attribute.hasChanged();
    }

    /**
     * Resets property value if it was changed.
     * 
     * @param propertyName
     *            Name of the property to get, e.g. "Status"
     */
    public void resetProperty(String propertyName) throws IllegalArgumentException {
        if (propertyName.equals(EFFORT_PROPERTY)) {
            dataLayer.setEffort(asset, null);
        }
        final String fullName = getType() + "." + propertyName;
        Attribute attribute = asset.getAttributes().get(fullName);
        if (attribute == null) {
            throw new IllegalArgumentException("There is no property: " + fullName);
        }
        attribute.rejectChanges();
    }

    /**
     * Gets property value.
     * 
     * @param propertyName
     *            Name of the property to get, e.g. "Status"
     * @return String, ValueId or PropertyValues.
     * @throws IllegalArgumentException
     *             If property cannot be got or there is no such one.
     * @see #NAME_PROPERTY
     * @see #STATUS_PROPERTY
     * @see #EFFORT_PROPERTY
     * @see #DONE_PROPERTY
     * @see #SCHEDULE_NAME_PROPERTY
     * @see #OWNERS_PROPERTY
     * @see #TODO_PROPERTY
     */
    public Object getProperty(String propertyName) throws IllegalArgumentException {
        if (propertyName.equals(EFFORT_PROPERTY)) {
            final Double effort = dataLayer.getEffort(asset);
            return effort == null ? null : numberFormat.format(effort.doubleValue());
        }
        final String fullName = getType() + "." + propertyName;
        Attribute attribute = asset.getAttributes().get(fullName);

        if (attribute == null) {
            throw new IllegalArgumentException("There is no property: " + fullName);
        }

        final PropertyValues allValues = getPropertyValues(propertyName);
        if (attribute.getDefinition().isMultiValue()) {
            final Object[] currentValues = attribute.getValues();
            return allValues == null ? currentValues : allValues.subset(currentValues);
        }

        try {
            final Object val = attribute.getValue();
            if (val instanceof Oid) {
                return allValues == null ? val : allValues.find((Oid) val);
            } else if (val instanceof Double) {
                return numberFormat.format(((Double) val).doubleValue());
            }
            return val;
        } catch (APIException e) {
            throw new IllegalArgumentException("Cannot get property: " + propertyName, e);
        }
    }

    public String getPropertyAsString(String propertyName) throws IllegalArgumentException {
        Object value = getProperty(propertyName);
        return value == null ? "" : value.toString();
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
        final boolean isEffort = propertyName.equals(EFFORT_PROPERTY);
        try {
            if ("".equals(newValue)) {
                newValue = null;
            }

            if ((isEffort || isNumeric(propertyName))) {
                setNumericProperty(propertyName, newValue);
            } else if (isMultiValue(propertyName)) {
                setMultiValueProperty(propertyName, (PropertyValues) newValue);
            } else {// List & String types
                if (newValue instanceof ValueId) {
                    newValue = ((ValueId) newValue).oid;
                }
                setPropertyInternal(propertyName, newValue);
            }

        } catch (Exception ex) {
            ApiDataLayer.warning("Cannot set property " + propertyName + " of " + this, ex);
        }
    }

    private boolean isMultiValue(String propertyName) {
        final IAttributeDefinition attrDef = asset.getAssetType().getAttributeDefinition(propertyName);
        return attrDef.isMultiValue();
    }

    private boolean isNumeric(String propertyName) {
        final IAttributeDefinition attrDef = asset.getAssetType().getAttributeDefinition(propertyName);
        return attrDef.getAttributeType() == AttributeType.Numeric;
    }

    private void setNumericProperty(String propertyName, Object newValue) throws APIException, ParseException {
        Double doubleValue = null;
        if (newValue instanceof String) {
            doubleValue = numberFormat.parse((String) newValue).doubleValue();
        } else if (newValue instanceof Double) {
            doubleValue = (Double) newValue;
        }

        if (propertyName.equals(EFFORT_PROPERTY)) {
            dataLayer.setEffort(asset, doubleValue);
        } else {
            if (doubleValue != null && doubleValue < 0) {
                throw new IllegalArgumentException("The field cannot be negative");
            }
            setPropertyInternal(propertyName, doubleValue);
        }
    }

    private void setPropertyInternal(String propertyName, Object newValue) throws APIException {
        final Attribute attribute = asset.getAttributes().get(getType() + "." + propertyName);
        if (attribute == null || !areEqual(attribute.getValue(), newValue)) {
            asset.setAttributeValue(asset.getAssetType().getAttributeDefinition(propertyName), newValue);
        }
    }

    private static boolean areEqual(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    private void setMultiValueProperty(String propertyName, PropertyValues newValues) throws APIException {
        final Attribute attribute = asset.getAttributes().get(getType() + "." + propertyName);
        final Object[] oldValues = attribute.getValues();
        final IAttributeDefinition attrDef = asset.getAssetType().getAttributeDefinition(propertyName);
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

    public void commitChanges() throws DataLayerException {
        validateRequiredFields();

        try {
            dataLayer.commitAsset(asset);
        } catch (V1Exception e) {
            throw ApiDataLayer.warning("Failed to commit changes of workitem: " + this, e);
        }
    }

    public void validateRequiredFields() throws DataLayerException {
        final RequiredFieldsValidator validator = dataLayer.requiredFieldsValidator;
        try {
            final List<RequiredFieldsDTO> requiredData = validator.validate(asset);

            if (requiredData.size() > 0) {
                String message = validator.getMessageOfUnfilledFieldsList(requiredData, "\n", ", ");
                throw new ValidatorException(message);
            }
        } catch (APIException e) {
            throw ApiDataLayer.warning("Cannot validate required fields.", e);
        }
    }

    public boolean isMine() {
        PropertyValues owners = (PropertyValues) getProperty(OWNERS_PROPERTY);
        return owners.containsOid(dataLayer.memberOid);
    }

    protected void checkPersistance(String job) {
        if (!isPersistent()) {
            throw new UnsupportedOperationException("Cannot " + job + " non-saved workitem.");
        }
    }

    public void revertChanges() {
        checkPersistance("revertChanges");
        dataLayer.revertAsset(asset);
    }

    /**
     * Defines whether this workitem exist on server. Otherwise this workitem
     * created on client and still not committed to server.
     * 
     * @return true if this workitem was persisted to server; false - otherwise.
     */
    public boolean isPersistent() {
        return !asset.getOid().isNull();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity other = (Entity) obj;
        if (isPersistent()) {
            return other.asset.getOid().equals(asset.getOid());
        }
        return asset.equals(other.asset);
    }

    @Override
    public int hashCode() {
        return asset.getOid().hashCode();
    }

    @Override
    public String toString() {
        return getId() + (hasChanges() ? " (Changed)" : "");
    }
}