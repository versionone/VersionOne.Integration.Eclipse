package com.versionone.taskview.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import com.versionone.common.sdk.IStatusCodes;
import com.versionone.common.sdk.Task;
import com.versionone.taskview.Activator;

/**
 * Support editing the Task Status
 * @author jerry
 *
 */
public class StatusEditor extends EditingSupport {

	private ComboBoxCellEditor _editor;
	private IStatusCodes _statusCodes; 
	
	public StatusEditor(TableViewer viewer, IStatusCodes codes) {
		super(viewer);
		_statusCodes = codes;
		_editor = new ComboBoxCellEditor(viewer.getTable(), _statusCodes.getDisplayValues());
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
		String currentStatus = "";
		try {
			currentStatus = ((Task)element).getStatus();
		} catch (Exception e) {
			Activator.logError(e);
		}
		return _statusCodes.getIndex(currentStatus);
	}

	@Override
	protected void setValue(Object element, Object value) {
		try {
			((Task)element).setStatus(_statusCodes.getDisplayValue((Integer)value));
		} catch (Exception e) {
			Activator.logError(e);
		}
		_editor.setValue(value);
		getViewer().update(element, null);
	}

	public void setStatusCodes(IStatusCodes value) {
		_statusCodes = value;
		_editor.setItems(_statusCodes.getDisplayValues());
	}
}
