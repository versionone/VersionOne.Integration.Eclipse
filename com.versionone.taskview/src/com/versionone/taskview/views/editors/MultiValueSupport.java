package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.properties.WorkitemPropertySource;

public class MultiValueSupport extends EditingSupport {

    private static final String ERROR_VALUE = "*** Error ***";
    
    private final String propertyName;
    private PropertyValues currentValue;
    private final ISelectionProvider selectionProvider;

    public MultiValueSupport(String propertyName, TreeViewer viewer, ISelectionProvider selectionProvider) {
        super(viewer);
        this.propertyName = propertyName;
        this.selectionProvider = selectionProvider;
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        Workitem workitem = ((Workitem) element);
        return new MultiValueEditor(((TreeViewer)getViewer()).getTree(), workitem.getTypePrefix(), propertyName);
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
        getViewer().update(element, null);
        selectionProvider.setSelection(new StructuredSelection(new WorkitemPropertySource(workitem, getViewer())));
    }

    /*
     * Fill Owners list.
     */
    /*
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
     */
}
