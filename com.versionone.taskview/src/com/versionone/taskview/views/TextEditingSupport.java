package com.versionone.taskview.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.Task;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

public class TextEditingSupport extends EditingSupport {

    private static final String ERROR_VALUE = "*** Error ***";

    private final CellEditor editor;
    private final String property;
    private String oldValue;

    public TextEditingSupport(String propertyName, TreeViewer viewer) {
        super(viewer);
        editor = new TextCellEditor(viewer.getTree());
        property = propertyName;
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return editor;
    }

    @Override
    protected void setValue(Object element, Object value) {
        try {
            if (!oldValue.equals(value)) {
                ((Workitem) element).setProperty(property, value);
            }
            getViewer().update(element, null);
        } catch (IllegalArgumentException e) {
            editor.setValue(""); // prevents two error dialogs
            Activator.logError(e);
            MessageDialog.openError(this.getViewer().getControl().getShell(), "Task View", e.getMessage());
        } catch (Exception e) {
            editor.setValue(""); // prevents two error dialogs
            Activator.logError(e);
            MessageDialog.openError(this.getViewer().getControl().getShell(), "Task View",
                    "Error updating field. Check Error log for more information.");
        }
    }

    @Override
    protected Object getValue(Object element) {
        try {
            return oldValue = ((Workitem) element).getProperty(property).toString();
        } catch (Exception e) {
            Activator.logError(e);
            return ERROR_VALUE;
        }
    }

}
