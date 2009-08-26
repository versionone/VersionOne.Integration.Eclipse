package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;

import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;

public class MultiValueEditor extends DialogCellEditor {

    private final PropertyValues allValues;

    public MultiValueEditor(Tree tree, PropertyValues allValues) {
        super(tree, SWT.NONE);
        this.allValues = allValues;
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        MultiValueDialog dialog = new MultiValueDialog(cellEditorWindow.getShell(), allValues.toStringArray());
        PropertyValues value = (PropertyValues) getValue();
        dialog.setSelectedIndices(getIndices(value));
        int x = dialog.open();
        return dialog.getSelectedIndices();
    }

    private int[] getIndices(PropertyValues property) {
        int[] res = new int[property.size()];
        int i=0;
        for (ValueId id : property){
            res[i++] = allValues.getStringArrayIndex(id);
        }
        return res;
    }

}
