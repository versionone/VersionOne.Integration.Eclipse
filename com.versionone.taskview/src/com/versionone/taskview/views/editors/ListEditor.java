package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

public class ListEditor extends EditingSupport {

    private static final String ERROR_VALUE = "*** Error ***";
    private String propertyName;
    private TreeViewer viewer;
    private final ApiDataLayer dataLayer;
    private ValueId currentValue;
    
    public ListEditor(String propertyName, TreeViewer viewer) {
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
        String[] valueList = dataLayer.getListPropertyValues(workitem.getTypePrefix(), propertyName).toStringArray();
        return new ComboBoxCellEditor(viewer.getTree(), valueList);
    }

    @Override
    protected Object getValue(Object element) {
        try {            
            Workitem workitem = ((Workitem) element);
            //String[] valueList = dataLayer.getListPropertyValues(workitem.getTypePrefix(), propertyName).toStringArray();
            ValueId value = (ValueId)((Workitem) element).getProperty(propertyName);
            currentValue = value;
            return dataLayer.getListPropertyValues(workitem.getTypePrefix(), propertyName).getStringArrayIndex(value);
            //return getCurrentId(((ValueId)((Workitem) element).getProperty(propertyName)).toString());//;
        } catch (Exception e) {
            Activator.logError(e);
            return ERROR_VALUE;
        }
    }

    @Override
    protected void setValue(Object element, Object value) {
        Workitem workitem = ((Workitem) element);
        //ValueId currentValue = (ValueId)workitem.getProperty(propertyName);
        //ValueId newValue = valueList[value];
        ValueId newValue = dataLayer.getListPropertyValues(workitem.getTypePrefix(), propertyName).getValueIdByIndex((Integer)value);
        if (dataLayer.getListPropertyValues(workitem.getTypePrefix(), propertyName).contains(newValue) &&
                !currentValue.equals(newValue) && newValue != null) {
            workitem.setProperty(propertyName, newValue);
        }
        viewer.refresh();
    }

}
