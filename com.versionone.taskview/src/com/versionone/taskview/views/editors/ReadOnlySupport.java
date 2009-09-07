package com.versionone.taskview.views.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

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
