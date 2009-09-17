package com.versionone.taskview.views.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.properties.WorkitemPropertySource;

public class TextSupport extends EditingSupport {

    private static final String ERROR_VALUE = "*** Error ***";

    private final CellEditor editor;
    private final String property;
    private String oldValue;
    private final ISelectionProvider selectionProvider;
    private final CellEditor non_edit_editor;

    public TextSupport(String propertyName, TreeViewer viewer, ISelectionProvider selectionProvider) {
        super(viewer);
        property = propertyName;
        editor = createEditor(viewer);
        non_edit_editor = createNonEditableEditor(viewer);
        this.selectionProvider = selectionProvider; 
    }

    private CellEditor createNonEditableEditor(TreeViewer viewer) {
        return new ReadOnlyCellEditor(viewer.getTree(), SWT.COLOR_DARK_GRAY);
    }

    private CellEditor createEditor(TreeViewer viewer) {
        return new TextCellEditor(viewer.getTree());
    }
    
    private boolean isEditable(Workitem workitem) {
        return !workitem.isPropertyReadOnly(property);
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;  
    }

    @Override
    protected CellEditor getCellEditor(Object element) {        
        if (isEditable((Workitem) element)) { 
            return editor;
        } else {
            return non_edit_editor;
        }
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (!isEditable((Workitem) element)) {
            return;
        }
        
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

    /**
     * This is the cell editor used to edit the Task ID cell. It was created
     * based on the TextCellEditor. The objective here is to only have support
     * for Copy, and nothing else.
     * 
     * @author jerry
     */
    public static class ReadOnlyCellEditor extends CellEditor {

        protected Text text;

        public ReadOnlyCellEditor(Composite parent, int style) {
            super(parent, style);
        }

        public ReadOnlyCellEditor(Composite parent) {
            this(parent, SWT.SINGLE);
        }

        @Override
        protected Control createControl(Composite parent) {
            text = new Text(parent, getStyle());
            text.setFont(parent.getFont());
            //text.setBackground(parent.getBackground());
            text.setEditable(false);
            text.setMenu(createMenu());
            return text;
        }

        @Override
        protected Object doGetValue() {
            return text.getText();
        }

        @Override
        protected void doSetFocus() {
            if (null != text) {
                text.selectAll();
                text.setFocus();
            }
        }

        @Override
        protected void doSetValue(Object value) {
            Assert.isTrue(value != null && (value instanceof String));
            text.setText((String) value);
        }

        private Menu createMenu() {
            Menu rc = new Menu(text);
            MenuItem copyItem = new MenuItem(rc, SWT.PUSH);
            copyItem.setText("Copy");
            copyItem.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                    text.copy();
                }

                public void widgetSelected(SelectionEvent e) {
                    text.copy();
                }
            });
            return rc;
        }
    }
}
