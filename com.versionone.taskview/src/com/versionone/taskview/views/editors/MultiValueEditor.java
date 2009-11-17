package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.WorkitemType;

public class MultiValueEditor extends DialogCellEditor {

    private final PropertyValues allValues;

    public MultiValueEditor(Composite parent, WorkitemType type, String propertyName) {
        super(parent, SWT.NONE);
        allValues = ApiDataLayer.getInstance().getListPropertyValues(type, propertyName);
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        MultiValueDialog dialog = new MultiValueDialog(cellEditorWindow.getShell(), allValues.toStringArray());
        PropertyValues value = (PropertyValues) getValue();
        dialog.setSelectedIndices(getIndices(value));
        int x = dialog.open();
        if (x == Window.OK){
            return getPropertyValues(dialog.getSelectedIndices());
        }
        return value;
    }

    private int[] getIndices(PropertyValues property) {
        int[] res = new int[property.size()];
        int i=0;
        for (ValueId id : property){
            res[i++] = allValues.getStringArrayIndex(id);
        }
        return res;
    }
    
    private PropertyValues getPropertyValues(int[] indexes) {
        PropertyValues res = new PropertyValues();
        for (int id : indexes){
            res.add(allValues.getValueIdByIndex(id));
        }
        return res;
    }

}
