package com.versionone.taskview.views.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.versionone.common.sdk.Entity;
import com.versionone.taskview.views.editors.SingleValueEditor;

public class ListPropertyDescriptor extends PropertyDescriptor {
    private final Entity workitem;
	
    public ListPropertyDescriptor(Object id, String propertyName, Entity workitem) {
        super(id, propertyName);
        Assert.isNotNull(workitem);
        this.workitem = workitem;
    }
    
    @Override
    public CellEditor createPropertyEditor(Composite parent) {
    	return new SingleValueEditor(parent, workitem.getType(), getId().toString());
    }
}
