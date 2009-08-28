package com.versionone.taskview.views;

import java.util.ArrayList;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.versionone.common.sdk.Workitem;

public class WorkitemPropertySource implements IPropertySource {

    private final Workitem item;

    WorkitemPropertySource(Workitem item) {
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
        ArrayList<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>();

        list.add(new TextPropertyDescriptor(Workitem.NAME_PROPERTY, "Title"));
        list.add(new TextPropertyDescriptor(Workitem.ID_PROPERTY, "ID"));
        list.add(new TextPropertyDescriptor(Workitem.OWNERS_PROPERTY, "Owner"));

        IPropertyDescriptor[] res = new IPropertyDescriptor[list.size()];
        return list.toArray(res);
    }

    public Object getPropertyValue(Object id) {
        return item.getProperty((String) id);
    }

    public boolean isPropertySet(Object id) {
        // TODO Auto-generated method stub
        return false;
    }

    public void resetPropertyValue(Object id) {
        // TODO Auto-generated method stub

    }

    public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub

    }
}
