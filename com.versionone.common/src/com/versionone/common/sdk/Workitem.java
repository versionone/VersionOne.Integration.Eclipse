package com.versionone.common.sdk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.versionone.apiclient.Asset;

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
    protected Asset Asset;
    public Workitem Parent;
    
    /**
     * List of child Workitems.
     */
    public final ArrayList<Workitem> Children;

    Workitem(Asset asset, Workitem parent){
    	Parent = parent;
    	Asset = asset;
    	if (asset == null) {//temporary
    		Children = new ArrayList<Workitem>() ;
    		if (parent == null) {    			
        		Children.add(new Workitem(null, this));
    		}
    		return;
    		}
    	Children = new ArrayList<Workitem>(asset.getChildren().size());
    	for(Asset childAsset : asset.getChildren()) {
    		if (getTypePrefix().equals(ProjectPrefix) || dataLayer.ShowAllTasks ||
    				dataLayer.IsCurrentUserOwnerAsset(childAsset)) {
    			Children.add(new Workitem(childAsset, this));
    		}	
    	}
    	Children.trimToSize();
    }


    
    
    
    public String getTypePrefix() {
    	if (Asset == null) {//temporary
    		return "nULL";
    		}
    	return Asset.getAssetType().getToken();
    }

    public String getId() {
    	if (Asset == null) {//temporary
    		return "nULL";
    		}
        return Asset.getOid().getMomentless().getToken();
    }

    public boolean hasChanges() {
        return Asset.hasChanged();
    }

    public boolean IsPropertyReadOnly(String propertyName) {
    	return true;
/*        String fullName = TypePrefix + '.' + propertyName;
        try {
            if (dataLayer.IsEffortTrackingRelated(propertyName)) {
                return IsEffortTrackingPropertyReadOnly(propertyName);
            }

            return false;
        } catch (Exception e) {
            ApiDataLayer.Warning("Cannot get property: " + fullName, e);
            return true;
        }
*/    }

/*    public boolean IsPropertyDefinitionReadOnly(string propertyName) {
        String fullName = TypePrefix + '.' + propertyName;
        try {
            Attribute attribute = Asset.Attributes[fullName];

            if (attribute.Definition.IsReadOnly) {
                return true;
            }

            return false;
        } catch (Exception e) {
            ApiDataLayer.Warning("Cannot get property: " + fullName, e);
            return true;
        }
    }
*/
/*    private bool IsEffortTrackingPropertyReadOnly(string propertyName) {
        if (!dataLayer.IsEffortTrackingRelated(propertyName)) {
            throw new InvalidOperationException("This property is not related to effort tracking.");
        }

        EffortTrackingLevel storyLevel = dataLayer.StoryTrackingLevel;
        EffortTrackingLevel defectLevel = dataLayer.DefectTrackingLevel;

        switch (TypePrefix) {
            case StoryPrefix:
                return storyLevel != EffortTrackingLevel.PrimaryWorkitem && storyLevel != EffortTrackingLevel.Both;
            case DefectPrefix:
                return defectLevel != EffortTrackingLevel.PrimaryWorkitem && defectLevel != EffortTrackingLevel.Both;
            case TaskPrefix:
            case TestPrefix:
                EffortTrackingLevel parentLevel;
                if (Parent.TypePrefix == StoryPrefix) {
                    parentLevel = storyLevel;
                } else if (Parent.TypePrefix == DefectPrefix) {
                    parentLevel = defectLevel;
                } else {
                    throw new InvalidOperationException("Unexpected parent asset type.");
                }
                return parentLevel != EffortTrackingLevel.SecondaryWorkitem && parentLevel != EffortTrackingLevel.Both;
            default:
                throw new NotSupportedException("Unexpected asset type.");
        }
    }
*/

    /// <summary>
    /// Gets property value.
    /// </summary>
    /// <param name="propertyName">Short name of the property to get. Eg. "Name"</param>
    /// <returns>String, Double, ValueId or IList&lt;ValueId&gt;.</returns>
    /// <exception cref="KeyNotFoundException">If no property found.</exception>
    public Object GetProperty(String propertyName) {
    	return "Prop";
/*        if (propertyName == EffortProperty) {
            return dataLayer.GetEffort(Asset);
        }

        Attribute attribute = Asset.Attributes[TypePrefix + '.' + propertyName];

        if (attribute.Definition.IsMultiValue) {
            return GetPropertyValues(propertyName).Subset(attribute.Values);
        }

        object val = attribute.Value;
        if (val is Oid) {
            PropertyValues res = GetPropertyValues(propertyName);
            return res.Find((Oid)val);
        }
        return val;
*/    }

    /// <summary>
    /// Sets property value.
    /// </summary>
    /// <param name="propertyName">Short name of the property to set. Eg. "Name"</param>
    /// <param name="newValue">String, Double, null, ValueId, PropertyValues accepted.</returns>
    public void SetProperty(String propertyName, Object newValue) {
        try {
/*            if (propertyName == EffortProperty) {
                dataLayer.AddEffort(Asset, Convert.ToDouble(newValue, CultureInfo.CurrentCulture));
                return;
            }
            IAttributeDefinition attrDef = Asset.AssetType.GetAttributeDefinition(propertyName);
            Attribute attribute = Asset.Attributes[TypePrefix + '.' + propertyName];
            if (attrDef.IsMultiValue) {
                UpdateValues(propertyName, attribute.ValuesList, (PropertyValues)newValue);
            } else {
                if (newValue is ValueId) {
                    newValue = ((ValueId)newValue).Oid;
                } else if ("".Equals(newValue)) {
                    newValue = null;
                }
                if (attribute.Value == null || !attribute.Value.Equals(newValue)) {
                    Asset.SetAttributeValue(attrDef, newValue);
                }
            }
*/        } catch (Exception ex) {
//            ApiDataLayer.Warning("Cannot set property: " + propertyName, ex);
        }
    }

/*    private void UpdateValues(string propertyName, IList oldValues, PropertyValues newValues) {
        IAttributeDefinition attrDef = Asset.AssetType.GetAttributeDefinition(propertyName);
        foreach (Oid oldValue in oldValues) {
            if (!newValues.ContainsOid(oldValue)) {
                Asset.RemoveAttributeValue(attrDef, oldValue);
            }
        }
        foreach (ValueId newValue in newValues) {
            if (!oldValues.Contains(newValue.Oid)) {
                Asset.AddAttributeValue(attrDef, newValue.Oid);
            }
        }
    }
*/
/*    public bool PropertyChanged(string propertyName) {
        IAttributeDefinition attrDef = Asset.AssetType.GetAttributeDefinition(propertyName);
        return Asset.GetAttribute(attrDef).HasChanged;
    }

    public void CommitChanges() {
        try {
            dataLayer.CommitAsset(Asset);
        } catch (APIException e) {
            throw ApiDataLayer.Warning("Failed to commit changes.", e);
        }
    }

    public bool IsMine() {
        PropertyValues owners = (PropertyValues)GetProperty(OwnersProperty);
        return owners.ContainsOid(dataLayer.MemberOid);
    }

    public bool CanQuickClose {
        get {
            try {
                return (bool)GetProperty("CheckQuickClose");
            } catch (KeyNotFoundException e) {
                ApiDataLayer.Warning("QuickClose not supported.", e);
                return false;
            }
        }
    }
*/
    /// <summary>
    /// Performs QuickClose operation.
    /// </summary>
/*    public void QuickClose() {
        CommitChanges();
        try {
            dataLayer.ExecuteOperation(Asset, Asset.AssetType.GetOperation("QuickClose"));
            dataLayer.RefreshAsset(this);
        } catch (APIException e) {
            throw ApiDataLayer.Warning("Failed to QuickClose.", e);
        }
    }
*/
/*    public bool CanSignup {
        get {
            try {
                return (bool)GetProperty("CheckQuickSignup");
            } catch (KeyNotFoundException e) {
                ApiDataLayer.Warning("QuickSignup not supported.", e);
                return false;
            }
        }
    }
*/
    /// <summary>
    /// Performs QuickSignup operation.
    /// </summary>
/*    public void Signup() {
        try {
            dataLayer.ExecuteOperation(Asset, Asset.AssetType.GetOperation("QuickSignup"));
            dataLayer.RefreshAsset(this);
        } catch (APIException e) {
            throw ApiDataLayer.Warning("Failed to QuickSignup.", e);
        }
    }

    public void Close() {
        dataLayer.ExecuteOperation(Asset, Asset.AssetType.GetOperation("Inactivate"));
        dataLayer.RefreshAsset(this);
    }

    public void RevertChanges() {
        dataLayer.RevertAsset(Asset);
    }
*/

/*    public override bool Equals(object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj is Workitem)) {
            return false;
        }
        Workitem other = (Workitem)obj;
        if (other.Asset.Oid != Asset.Oid) {
            return false;
        }
        return true;
    }

    public override int GetHashCode() {
        return Asset.Oid.GetHashCode();
    }

    public static bool operator ==(Workitem t1, Workitem t2) {
        if (ReferenceEquals(t1, t2)) {
            return true;
        }
        if (ReferenceEquals(t1, null) || ReferenceEquals(t2, null)) {
            return false;
        }
        return t1.Equals(t2);
    }

    public static bool operator !=(Workitem t1, Workitem t2) {
        return !(t1 == t2);
    }

    public override String ToString() {
        return Id + (Asset.HasChanged ? " (Changed)" : "");
    }
*/
}
