package com.versionone.taskview.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.versionone.common.sdk.Task;

/**
 * Abstract base class for editing Task Attributes
 * @author Jerry D. Odenwelder Jr.
 *
 */
abstract public class TaskEditor extends EditingSupport {

	private TextCellEditor _editor;
	
	public TaskEditor(TableViewer viewer) {
		super(viewer);
		_editor = new TextCellEditor(viewer.getTable());
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
	protected void setValue(Object element, Object value) {
		setValue((Task)element, value);
		getViewer().update(element, null);
	}		

	/**
	 * Derived class must update this method and set the appropriate property on the Task
	 * @param element
	 * @param value
	 */
	abstract protected void setValue(Task element, Object value);
	
	/**
	 * Edit the Task Name
	 */
	public static class NameEditor extends TaskEditor {
		
		public NameEditor(TableViewer viewer) {
			super(viewer);
		}

		@Override
		protected Object getValue(Object element) {
			return ((Task)element).getName();
		}

		@Override
		protected void setValue(Task element, Object value) {
			element.setName(value.toString());			
		}

	}
	
	/**
	 * Edit the Detail Estimate
	 */
	public static class EstimateEditor extends TaskEditor {
		
		public EstimateEditor(TableViewer viewer) {
			super(viewer);
		}

		@Override
		protected Object getValue(Object element) {
			return ((Task)element).getEstimate();
		}

		@Override
		protected void setValue(Task element, Object value) {
			element.setEstimate(value.toString());
			
		}		
	}
	
	/**
	 * Edit the Effort
	 */
	public static class EffortEditor extends TaskEditor {
		
		public EffortEditor(TableViewer viewer) {
			super(viewer);
		}

		@Override
		protected Object getValue(Object element) {
			return ((Task)element).getEffort();
		}

		@Override
		protected void setValue(Task element, Object value) {
			element.setEffort(value.toString());
		}

	}

	/**
	 * Edit the ToDo
	 */
	public static class ToDoEditor extends TaskEditor {
		
		public ToDoEditor(TableViewer viewer) {
			super(viewer);
		}

		@Override
		protected Object getValue(Object element) {
			return ((Task)element).getToDo();
		}

		@Override
		protected void setValue(Task element, Object value) {
			element.setToDo(value.toString());
		}
	}	
}
