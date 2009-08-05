package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
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
    private ComboBoxCellEditor editor;
    private TreeViewer viewer;
    private String[] valueList;
    private final ApiDataLayer dataLayer;
    private ValueId currentValue;
    
    public ListEditor(TreeViewer viewer, String propertyName) {
        super(viewer);
        this.propertyName = propertyName;
        this.viewer = viewer;
        dataLayer = ApiDataLayer.getInstance();
        valueList = dataLayer.getListPropertyValues("Story", propertyName).toStringArray();        
    }
    
    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {      
        editor = new ComboBoxCellEditor(viewer.getTree(), valueList);
        return editor;
    }

    @Override
    protected Object getValue(Object element) {
        try {
            ValueId value = (ValueId)((Workitem) element).getProperty(propertyName);
            currentValue = value;
            return dataLayer.getListPropertyValues("Story", propertyName).getPropertyListIndex(value);
            //return getCurrentId(((ValueId)((Workitem) element).getProperty(propertyName)).toString());//;
        } catch (Exception e) {
            Activator.logError(e);
            return ERROR_VALUE;
        }
    }

    @Override
    protected void setValue(Object element, Object value) {
        Workitem workitem = ((Workitem) element);
        ValueId newValue = (ValueId)workitem.getProperty(propertyName);
        if (dataLayer.getListPropertyValues("Story", propertyName).contains(newValue) &&
                !currentValue.equals(newValue)) {
            workitem.setProperty(propertyName, value);
        }
        
    }

}
