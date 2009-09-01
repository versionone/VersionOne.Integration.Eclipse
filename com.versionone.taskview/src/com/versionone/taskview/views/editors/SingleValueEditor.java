package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValueId;

public class SingleValueEditor extends ComboBoxCellEditor {
	private final PropertyValues allValues;
	
	public SingleValueEditor(Composite parent, String typePrefix, String propertyName) {
		super(parent, null, SWT.DROP_DOWN | SWT.READ_ONLY);
		allValues = ApiDataLayer.getInstance().getListPropertyValues(typePrefix, propertyName);
		setItems(allValues.toStringArray());
	}
	
	@Override
	protected void doSetValue(Object value) {
		super.doSetValue(getIndex((PropertyValues)value));
	}
	
	@Override
	protected Object doGetValue() {
		Integer index = (Integer)super.doGetValue();
		return getPropertyValues(index);
	}
	
	private PropertyValues getPropertyValues(int index) {
        PropertyValues res = new PropertyValues();
        res.add(allValues.getValueIdByIndex(index));
        return res;
    }
	
	private Integer getIndex(PropertyValues property) {
        Integer index = -1;
        for (ValueId id : property){
            index = allValues.getStringArrayIndex(id);
        }
        return index;
    }
}
