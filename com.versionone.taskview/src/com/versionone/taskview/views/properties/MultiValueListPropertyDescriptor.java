package com.versionone.taskview.views.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.versionone.taskview.views.editors.MultiValueEditor;

/**
 * Property descriptor providing dialog editor for multivalue properties. 
 */
public class MultiValueListPropertyDescriptor extends PropertyDescriptor {
    private final String typePrefix;
    private final String property;
    
    public MultiValueListPropertyDescriptor(Object id, String typePrefix, String propertyName) {
        super(id, propertyName);
        this.typePrefix = typePrefix;
        this.property = (String) id;
    }
    
    @Override
    public CellEditor createPropertyEditor(Composite parent) {
        return new MultiValueEditor(parent, typePrefix, property);
    }
}
