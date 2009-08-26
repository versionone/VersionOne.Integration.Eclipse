package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;

import com.versionone.common.sdk.PropertyValues;

public class MultiValueEditor extends DialogCellEditor {

    private final String[] values;

    public MultiValueEditor(Tree tree, String[] values) {
        super(tree, SWT.NONE);
        this.values = values;
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        MultiValueDialog dialog = new MultiValueDialog(cellEditorWindow.getShell(), values);
        int[] value = (int[]) getValue();
        dialog.setSelectedIndices(value);
        int x = dialog.open();
        return dialog.getSelectedIndices();
    }

}
