package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

public class MultiValueSupport extends EditingSupport {

    private static final String ERROR_VALUE = "*** Error ***";
    private String propertyName;
    private TreeViewer viewer;
    private final ApiDataLayer dataLayer;
    private PropertyValues currentValue;

    public MultiValueSupport(String propertyName, TreeViewer viewer) {
        super(viewer);
        this.propertyName = propertyName;
        this.viewer = viewer;
        dataLayer = ApiDataLayer.getInstance();
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        Workitem workitem = ((Workitem) element);
        PropertyValues values = dataLayer.getListPropertyValues(workitem.getTypePrefix(), propertyName);
        return new MultiValueEditor(viewer.getTree(), values);
    }

    @Override
    protected Object getValue(Object element) {
        try {
            Workitem workitem = ((Workitem) element);
            return currentValue = (PropertyValues) workitem.getProperty(propertyName);
        } catch (Exception e) {
            Activator.logError(e);
            return ERROR_VALUE;
        }
    }

    @Override
    protected void setValue(Object element, Object value) {
        Workitem workitem = ((Workitem) element);
        PropertyValues newValue = (PropertyValues) value;
        if (currentValue == null || !currentValue.equals(newValue)) {
            workitem.setProperty(propertyName, newValue);
        }
        viewer.refresh();
    }

}
