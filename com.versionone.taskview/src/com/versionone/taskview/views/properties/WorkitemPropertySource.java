package com.versionone.taskview.views.properties;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.views.properties.Configuration.AssetDetailSettings;
import com.versionone.taskview.views.properties.Configuration.ColumnSetting;

public class WorkitemPropertySource implements IPropertySource {

    public static final String COLUMN_TITLE_PREFIX = "ColumnTitle'";

    private final Workitem item;
    private final ISelectionProvider proxy;

    public WorkitemPropertySource(Workitem item, ISelectionProvider proxy) {
        this.item = item;
        this.proxy = proxy;
    }

    public Object getEditableValue() {
        // Workitem is not editable as whole object.
        return null;
    }

    public Workitem getItem() {
        return item;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        final Configuration cfg = Configuration.getInstance();
        final ColumnSetting[] columns = cfg.getColumns(item.getType());
        final ArrayList<IPropertyDescriptor> res = new ArrayList<IPropertyDescriptor>(columns.length);
        for (ColumnSetting column : columns) {
            if (!column.effortTracking || ApiDataLayer.getInstance().isTrackEffortEnabled()) {
                res.add(createPropertyDescriptor(column));
            }
        }
        return res.toArray(new IPropertyDescriptor[res.size()]);
    }

    private PropertyDescriptor createPropertyDescriptor(ColumnSetting col) {
        String localName;
        localName = ApiDataLayer.getInstance().localizerResolve(col.name);
        if (localName.startsWith(COLUMN_TITLE_PREFIX))
            localName = localName.substring(COLUMN_TITLE_PREFIX.length());
        final PropertyDescriptor desc;
        if (col.type.equals(AssetDetailSettings.STRING_TYPE) || col.type.equals(AssetDetailSettings.EFFORT_TYPE)) {
            desc = new CustomTextPropertyDescriptor(col.attribute, localName, col.readOnly
                    || item.isPropertyReadOnly(col.attribute));
        } else if (col.type.equals(AssetDetailSettings.LIST_TYPE)) {
            desc = new ListPropertyDescriptor(col.attribute, localName, item);
        } else if (col.type.equals(AssetDetailSettings.MULTI_VALUE_TYPE)) {
            desc = new MultiValueListPropertyDescriptor(col.attribute, item.getType(), localName);
        } else if (col.type.equals(AssetDetailSettings.RICH_TEXT_TYPE)) {
            desc = new RichTextPropertyDescriptor(col.attribute, localName, item.getPropertyAsString(col.attribute));
        } else {
            desc = new PropertyDescriptor(col.attribute, localName);
        }
        desc.setCategory(col.category);
        return desc;
    }

    public Object getPropertyValue(Object id) {
        return item.getProperty((String) id) == null ? "" : item.getProperty((String) id);
    }

    public boolean isPropertySet(Object id) {
        return item.isPropertyChanged((String) id);
    }

    public void resetPropertyValue(Object id) {
        item.resetProperty((String) id);
        ((Viewer) proxy).refresh();
    }

    public void setPropertyValue(Object id, Object value) {
        try {
            item.setProperty((String) id, value);
            ((Viewer) proxy).refresh();
        } catch (Exception e) {
            // Do nothing
        }
    }
}
