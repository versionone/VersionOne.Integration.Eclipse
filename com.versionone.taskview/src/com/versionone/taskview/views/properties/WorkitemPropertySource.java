package com.versionone.taskview.views.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.views.Configuration;
import com.versionone.taskview.views.Configuration.ColumnSetting;

public class WorkitemPropertySource implements IPropertySource {

    private final Workitem item;

    public WorkitemPropertySource(Workitem item) {
        this.item = item;
    }

    public Object getEditableValue() {
        // Workitem is not editable as whole object.
        return null;
    }

    public Workitem getItem() {
        return item;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        Configuration cfg = Configuration.getInstance();
        ColumnSetting[] columns = cfg.assetDetailSettings.getColumns(item.getTypePrefix());
        IPropertyDescriptor[] res = new IPropertyDescriptor[columns.length];
        for (int i = 0; i < columns.length; i++) {
            res[i] = createPropertyDescriptor(columns[i]);
        }
        return res;
    }

    private PropertyDescriptor createPropertyDescriptor(ColumnSetting col) {
        String localName;
        try {
            localName = ApiDataLayer.getInstance().localizerResolve(col.name);
        } catch (DataLayerException e) {
            localName = col.name;
        }
        final PropertyDescriptor desc;
        if (col.type.equals("String")) {
            desc = new CustomTextPropertyDescriptor(col.attribute, localName, col.readOnly);
        } else if(col.type.equals("List")) {
        	desc = new ListPropertyDescriptor(col.attribute, localName, item);
        } else {
            desc = new PropertyDescriptor(col.attribute, localName);
        }
        desc.setCategory(col.category);
        return desc;
    }

    public Object getPropertyValue(Object id) {
        return item.getProperty((String) id);
    }

    public boolean isPropertySet(Object id) {
        return item.isPropertyChanged((String) id);
    }

    public void resetPropertyValue(Object id) {
        item.resetProperty((String) id);
    }

    public void setPropertyValue(Object id, Object value) {
        try {
            item.setProperty((String) id, value);
        } catch (Exception e) {
            // Do nothing
        }
    }
}
