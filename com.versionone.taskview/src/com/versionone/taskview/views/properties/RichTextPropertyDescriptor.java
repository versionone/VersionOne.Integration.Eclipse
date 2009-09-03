package com.versionone.taskview.views.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import com.versionone.taskview.views.editors.RichTextEditor;

/***
 * 
 * Descriptor for Rich editor for properties
 *
 */
public class RichTextPropertyDescriptor extends PropertyDescriptor {

    private final String richText;
    
    public RichTextPropertyDescriptor(Object id, String displayName, String string) {
        super(id, displayName);
        
        this.richText = string;
    }
    
    
    @Override
    public CellEditor createPropertyEditor(Composite parent) {
        return new RichTextEditor(parent, richText);
    }
}
