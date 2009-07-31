package com.versionone.common.sdk;

import java.util.ArrayList;

import com.versionone.Oid;
import com.versionone.apiclient.APIException;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.IAttributeDefinition;

public class Workitem {
    public static final String TaskPrefix = "Task";
    public static final String StoryPrefix = "Story";
    public static final String DefectPrefix = "Defect";
    public static final String TestPrefix = "Test";
    public static final String ProjectPrefix = "Scope";

    public static final String IdProperty = "ID";
    public static final String DetailEstimateProperty = "DetailEstimate";
    public static final String NameProperty = "Name";
    public static final String StatusProperty = "Status";
    public static final String EffortProperty = "Actuals";
    public static final String DoneProperty = "Actuals.Value.@Sum";
    public static final String ScheduleNameProperty = "Schedule.Name";
    public static final String OwnersProperty = "Owners";
    public static final String TodoProperty = "ToDo";

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
	    if (getTypePrefix().equals(ProjectPrefix) || dataLayer.ShowAllTasks
		    || dataLayer.IsCurrentUserOwnerAsset(childAsset)) {
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

    public boolean IsPropertyReadOnly(String propertyName) {
	return true;
	/*
	 * String fullName = TypePrefix + '.' + propertyName; try { if
	 * (dataLayer.IsEffortTrackingRelated(propertyName)) { return
	 * IsEffortTrackingPropertyReadOnly(propertyName); }
	 * 
	 * return false; } catch (Exception e) {
	 * ApiDataLayer.Warning("Cannot get property: " + fullName, e); return
	 * true; }
	 */}

    /*
     * public boolean IsPropertyDefinitionReadOnly(string propertyName) { String
     * fullName = TypePrefix + '.' + propertyName; try { Attribute attribute =
     * Asset.Attributes[fullName];
     * 
     * if (attribute.Definition.IsReadOnly) { return true; }
     * 
     * return false; } catch (Exception e) {
     * ApiDataLayer.Warning("Cannot get property: " + fullName, e); return true;
     * } }
     */
    /*
     * private bool IsEffortTrackingPropertyReadOnly(string propertyName) { if
     * (!dataLayer.IsEffortTrackingRelated(propertyName)) { throw new
     * InvalidOperationException
     * ("This property is not related to effort tracking."); }
     * 
     * EffortTrackingLevel storyLevel = dataLayer.StoryTrackingLevel;
     * EffortTrackingLevel defectLevel = dataLayer.DefectTrackingLevel;
     * 
     * switch (TypePrefix) { case StoryPrefix: return storyLevel !=
     * EffortTrackingLevel.PrimaryWorkitem && storyLevel !=
     * EffortTrackingLevel.Both; case DefectPrefix: return defectLevel !=
     * EffortTrackingLevel.PrimaryWorkitem && defectLevel !=
     * EffortTrackingLevel.Both; case TaskPrefix: case TestPrefix:
     * EffortTrackingLevel parentLevel; if (Parent.TypePrefix == StoryPrefix) {
     * parentLevel = storyLevel; } else if (Parent.TypePrefix == DefectPrefix) {
     * parentLevel = defectLevel; } else { throw new
     * InvalidOperationException("Unexpected parent asset type."); } return
     * parentLevel != EffortTrackingLevel.SecondaryWorkitem && parentLevel !=
     * EffortTrackingLevel.Both; default: throw new
     * NotSupportedException("Unexpected asset type."); } }
     */

    private PropertyValues getPropertyValues(String propertyName) {
	return dataLayer.GetListPropertyValues(getTypePrefix(), propertyName);
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
    public Object GetProperty(String propertyName)
	    throws IllegalArgumentException {
	if (propertyName.equals(EffortProperty)) {
	    return dataLayer.GetEffort(asset);
	}

	Attribute attribute = asset.getAttributes().get(
		getTypePrefix() + '.' + propertyName);

	if (attribute.getDefinition().isMultiValue()) {
	    return getPropertyValues(propertyName)
		    .Subset(attribute.getValues());
	}

	try {
	    Object val = attribute.getValue();
	    if (val instanceof Oid) {
		PropertyValues res = getPropertyValues(propertyName);
		return res.Find((Oid) val);
	    }
	    return val;
	} catch (APIException e) {
	    throw new IllegalArgumentException("Cannot get property: "
		    + propertyName, e);
	}
    }

    /**
     * Sets property value.
     * 
     * @param propertyName
     *            Short name of the property to set, e.g. "Name".
     * @param newValue
     *            String, Double, null, ValueId, PropertyValues accepted.
     */
    public void SetProperty(String propertyName, Object newValue) {
	try {
	    if (propertyName.equals(EffortProperty)) {
		dataLayer.addEffort(asset, Double.valueOf((String) newValue));
		return;
	    }
	    IAttributeDefinition attrDef = asset.getAssetType()
		    .getAttributeDefinition(propertyName);
	    Attribute attribute = asset.getAttributes().get(
		    getTypePrefix() + '.' + propertyName);
	    if (attrDef.isMultiValue()) {
		updateValues(propertyName, attribute.getValues(),
			(PropertyValues) newValue);
	    } else {
		if (newValue instanceof ValueId) {
		    newValue = ((ValueId) newValue).oid;
		} else if ("".equals(newValue)) {
		    newValue = null;
		}
		if (attribute.getValue() == null
			|| !attribute.getValue().equals(newValue)) {
		    asset.setAttributeValue(attrDef, newValue);
		}
	    }
	} catch (Exception ex) {
	    ApiDataLayer.warning("Cannot set property: " + propertyName, ex);
	}
    }

    private void updateValues(String propertyName, Object[] oldValues,
	    PropertyValues newValues) throws APIException {
	IAttributeDefinition attrDef = asset.getAssetType()
		.getAttributeDefinition(propertyName);
	for (Object oldOid : oldValues) {
	    if (!newValues.ContainsOid((Oid) oldOid)) {
		asset.removeAttributeValue(attrDef, oldOid);
	    }
	}
	for (ValueId newValue : newValues.values()) {
	    if (!checkContains(oldValues, newValue.oid)) {
		asset.addAttributeValue(attrDef, newValue.oid);
	    }
	}
    }

    private boolean checkContains(Object[] array, Object value) {
	for (Object item : array){
	    if (item.equals(value))
		return true;
	}
	return false;
    }

    /*
     * public bool PropertyChanged(string propertyName) { IAttributeDefinition
     * attrDef = Asset.AssetType.GetAttributeDefinition(propertyName); return
     * Asset.GetAttribute(attrDef).HasChanged; }
     * 
     * public void CommitChanges() { try { dataLayer.CommitAsset(Asset); } catch
     * (APIException e) { throw
     * ApiDataLayer.Warning("Failed to commit changes.", e); } }
     * 
     * public bool IsMine() { PropertyValues owners =
     * (PropertyValues)GetProperty(OwnersProperty); return
     * owners.ContainsOid(dataLayer.MemberOid); }
     * 
     * public bool CanQuickClose { get { try { return
     * (bool)GetProperty("CheckQuickClose"); } catch (KeyNotFoundException e) {
     * ApiDataLayer.Warning("QuickClose not supported.", e); return false; } } }
     */
    // / <summary>
    // / Performs QuickClose operation.
    // / </summary>
    /*
     * public void QuickClose() { CommitChanges(); try {
     * dataLayer.ExecuteOperation(Asset,
     * Asset.AssetType.GetOperation("QuickClose"));
     * dataLayer.RefreshAsset(this); } catch (APIException e) { throw
     * ApiDataLayer.Warning("Failed to QuickClose.", e); } }
     */
    /*
     * public bool CanSignup { get { try { return
     * (bool)GetProperty("CheckQuickSignup"); } catch (KeyNotFoundException e) {
     * ApiDataLayer.Warning("QuickSignup not supported.", e); return false; } }
     * }
     */
    // / <summary>
    // / Performs QuickSignup operation.
    // / </summary>
    /*
     * public void Signup() { try { dataLayer.ExecuteOperation(Asset,
     * Asset.AssetType.GetOperation("QuickSignup"));
     * dataLayer.RefreshAsset(this); } catch (APIException e) { throw
     * ApiDataLayer.Warning("Failed to QuickSignup.", e); } }
     * 
     * public void Close() { dataLayer.ExecuteOperation(Asset,
     * Asset.AssetType.GetOperation("Inactivate"));
     * dataLayer.RefreshAsset(this); }
     * 
     * public void RevertChanges() { dataLayer.RevertAsset(Asset); }
     */

    /*
     * public override bool Equals(object obj) { if (obj == null) { return
     * false; } if (!(obj is Workitem)) { return false; } Workitem other =
     * (Workitem)obj; if (other.Asset.Oid != Asset.Oid) { return false; } return
     * true; }
     * 
     * public override int GetHashCode() { return Asset.Oid.GetHashCode(); }
     * 
     * public static bool operator ==(Workitem t1, Workitem t2) { if
     * (ReferenceEquals(t1, t2)) { return true; } if (ReferenceEquals(t1, null)
     * || ReferenceEquals(t2, null)) { return false; } return t1.Equals(t2); }
     * 
     * public static bool operator !=(Workitem t1, Workitem t2) { return !(t1 ==
     * t2); }
     * 
     * public override String ToString() { return Id + (Asset.HasChanged ?
     * " (Changed)" : ""); }
     */
}
