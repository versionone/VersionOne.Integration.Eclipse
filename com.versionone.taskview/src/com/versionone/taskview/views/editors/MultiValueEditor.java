package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;

import com.versionone.common.sdk.PropertyValues;

public class MultiValueEditor extends DialogCellEditor {

    private final PropertyValues values;

    public MultiValueEditor(Tree tree, PropertyValues values) {
        super(tree, SWT.NONE);
        this.values = values;
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        MultiValueDialog dialog = new MultiValueDialog(cellEditorWindow.getShell(), values);
        Object value = getValue();
        dialog.setValue(value);
        value = dialog.open();
        return dialog.getValue();
    }

}
