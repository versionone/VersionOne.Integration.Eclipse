package com.versionone.taskview.views.editors;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Workitem;

public class SingleValueEditor extends ComboBoxCellEditor {
	private ApiDataLayer dataLayer;
	
	public SingleValueEditor(Composite parent, String[] items, Workitem workitem) {
		super(parent, null, SWT.DROP_DOWN | SWT.READ_ONLY);
		dataLayer = ApiDataLayer.getInstance();
	}

}
