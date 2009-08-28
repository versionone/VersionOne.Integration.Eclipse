package com.versionone.taskview.views;

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
        PropertyDescriptor sizeDescriptor = new PropertyDescriptor("v1.size", "Size");
        // set a custom label provider for a point
        sizeDescriptor.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                Point point = (Point) element;
                StringBuffer buf = new StringBuffer();
                buf.append("Height:");
                buf.append(point.y);
                buf.append("  ");
                buf.append("Width:");
                buf.append(point.x);
                return buf.toString();
            }
        });
        sizeDescriptor.setCategory("Category1");

        PropertyDescriptor textDescriptor = new TextPropertyDescriptor("v1.text", "Text");
        textDescriptor.setCategory("Category2");

        return new PropertyDescriptor[] { sizeDescriptor, textDescriptor };
    }

    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        return null;
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
