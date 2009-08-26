package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.List;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

public class MultiValueSupport extends EditingSupport {

    private static final String ERROR_VALUE = "*** Error ***";
    
    private final String propertyName;
    private final TreeViewer viewer;
    private final PropertyValues allValues;
    private PropertyValues currentValue;

    public MultiValueSupport(String propertyName, TreeViewer viewer) {
        super(viewer);
        this.propertyName = propertyName;
        this.viewer = viewer;
        allValues = ApiDataLayer.getInstance().getListPropertyValues(Workitem.STORY_PREFIX, propertyName);
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
//        Workitem workitem = ((Workitem) element);
        return new MultiValueEditor(viewer.getTree(), allValues);
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
        int[] newValue = (int[]) value;
        
        if (currentValue == null || !currentValue.equals(newValue)) {
            workitem.setProperty(propertyName, newValue);
        }
        viewer.refresh();
    }

    /*
     * Fill Owners list.
     */
    private static void fillList(List list, PropertyValues values, Object value) {
        int[] selectedIndexes = new int[values.size()];
        int i = 0;
        int currentIndex = 0;
        for (String owner : values.toStringArray()) {
            list.add(owner);
            if (values.contains(values.getValueIdByIndex(i))) {
                selectedIndexes[currentIndex] = i;
                currentIndex++;
            }
            i++;
        }

        list.select(selectedIndexes);
    }

}