package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.WorkitemType;

public class SingleValueEditor extends ComboBoxCellEditor {
    private final PropertyValues allValues;

    public SingleValueEditor(Composite parent, WorkitemType typePrefix, String propertyName) {
        super(parent, new String[0], SWT.DROP_DOWN | SWT.READ_ONLY);
        allValues = ApiDataLayer.getInstance().getListPropertyValues(typePrefix, propertyName);
        setItems(allValues.toStringArray());
    }

    @Override
    protected void doSetValue(Object value) {
        super.doSetValue(allValues.getStringArrayIndex((ValueId) value));
    }

    @Override
    protected Object doGetValue() {
        Integer index = (Integer) super.doGetValue();
        return allValues.getValueIdByIndex(index);
    }
}
