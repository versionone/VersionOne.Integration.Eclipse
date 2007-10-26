package com.versionone.taskview.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import com.versionone.common.sdk.Task;

public class StatusEditor extends EditingSupport {

	private ComboBoxCellEditor _editor;
	
	public StatusEditor(TableViewer viewer, String[] items) {
		super(viewer);
		_editor = new ComboBoxCellEditor(viewer.getTable(), items);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return _editor;
	}

	@Override
	protected Object getValue(Object element) {
		String status = ((Task)element).getStatus();
		String[] values = _editor.getItems();
		Integer index = 0;
		for(int i=0;i<values.length; ++i) {
			if(status.equals(values[i])) {
				index = i;
			}
		}
		return index;
	}

	@Override
	protected void setValue(Object element, Object value) {
		((Task)element).setStatus(_editor.getItems()[(Integer)value]);
		_editor.setValue(value);
		getViewer().update(element, null);
	}
}
