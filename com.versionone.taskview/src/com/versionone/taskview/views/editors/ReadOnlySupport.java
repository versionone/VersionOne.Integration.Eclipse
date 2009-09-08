package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;

//TODO review and remove this class
public class ReadOnlySupport extends TextSupport {

    public ReadOnlySupport(String propertyName, TreeViewer viewer) {
        super(propertyName, viewer, null);
    }

    protected CellEditor createEditor(TreeViewer viewer) {
        return new ReadOnlyCellEditor(viewer.getTree());
    }

    @Override
    protected void setValue(Object element, Object value) {
        // Do nothing
    }
    
    @Override
    protected boolean canEdit(Object element) {   
        return true;
    }


}
