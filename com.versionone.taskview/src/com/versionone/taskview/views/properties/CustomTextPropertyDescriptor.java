package com.versionone.taskview.views.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.versionone.taskview.views.editors.ReadOnlySupport;

/**
 * Text property descriptor with ability to skip entering edit mode.
 * 
 */
public class CustomTextPropertyDescriptor extends TextPropertyDescriptor {
    private boolean readOnly;

    public CustomTextPropertyDescriptor(Object id, String propertyName) {
        this(id, propertyName, false);
    }

    public CustomTextPropertyDescriptor(Object id, String propertyName, boolean readOnly) {
        super(id, propertyName);
        this.readOnly = readOnly;
    }

    public void setReadOnly(boolean value) {
        readOnly = value;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    @Override
    public CellEditor createPropertyEditor(Composite parent) {
        if (readOnly) {
            return new ReadOnlySupport.ReadOnlyCellEditor(parent);
        }
        return super.createPropertyEditor(parent);
    }
}
