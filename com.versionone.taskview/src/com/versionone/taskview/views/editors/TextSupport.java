package com.versionone.taskview.views.editors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.properties.WorkitemPropertySource;

public class TextSupport extends EditingSupport {

    private static final String ERROR_VALUE = "*** Error ***";

    private final CellEditor editor;
    private final String property;
    private String oldValue;
    private final ISelectionProvider selectionProvider;

    public TextSupport(String propertyName, TreeViewer viewer, ISelectionProvider selectionProvider) {
        super(viewer);
        property = propertyName;
        editor = createEditor(viewer);
        this.selectionProvider = selectionProvider; 
    }

    protected CellEditor createEditor(TreeViewer viewer) {
        return new TextCellEditor(viewer.getTree());
    }

    @Override
    protected boolean canEdit(Object element) {
        Workitem workitem = (Workitem) element;
                
        return !Workitem.isPropertyReadOnly(workitem, property.equals(Workitem.EFFORT_PROPERTY), property); 
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

            selectionProvider.setSelection(new StructuredSelection(new WorkitemPropertySource((Workitem) element, getViewer())));
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
            return oldValue = ((Workitem) element).getPropertyAsString(property);
        } catch (Exception e) {
            Activator.logError(e);
            return ERROR_VALUE;
        }
    }

}
