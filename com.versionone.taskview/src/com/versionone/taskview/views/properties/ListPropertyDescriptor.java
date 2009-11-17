package com.versionone.taskview.views.properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.views.editors.SingleValueEditor;

public class ListPropertyDescriptor extends PropertyDescriptor {
    private final Workitem workitem;
	
    public ListPropertyDescriptor(Object id, String propertyName, Workitem workitem) {
        super(id, propertyName);
        Assert.isNotNull(workitem);
        this.workitem = workitem;
    }
    
    @Override
    public CellEditor createPropertyEditor(Composite parent) {
    	return new SingleValueEditor(parent, workitem.getType(), getId().toString());
    }
}
