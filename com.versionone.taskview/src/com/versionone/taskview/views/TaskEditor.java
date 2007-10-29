package com.versionone.taskview.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.versionone.common.sdk.Task;
import com.versionone.taskview.Activator;

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
			try {
				return ((Task)element).getName();
			} catch (Exception e) {
				Activator.logError(e);
			}
			return "*** Error ***"; 
		}

		@Override
		protected void setValue(Task element, Object value) {
			try {
				element.setName(value.toString());
			} catch (Exception e) {
				Activator.logError(e);
			}			
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
			try {
				return ((Task)element).getEstimate();
			} catch (Exception e) {
				Activator.logError(e);
			}
			return "*** Error ***";
		}

		@Override
		protected void setValue(Task element, Object value) {
			try {
				element.setEstimate(value.toString());
			} catch (Exception e) {
				Activator.logError(e);
			}
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
			try {
				return ((Task)element).getEffort();
			} catch (Exception e) {
				Activator.logError(e);
			}
			return "*** Error ***";
		}

		@Override
		protected void setValue(Task element, Object value) {
			try {
				element.setEffort(value.toString());
			} catch (Exception e) {
				Activator.logError(e);
			}
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
			try {
				return ((Task)element).getToDo();
			} catch (Exception e) {
				Activator.logError(e);
			}
			return "*** Error ***";
		}

		@Override
		protected void setValue(Task element, Object value) {
			try {
				element.setToDo(value.toString());
			} catch (Exception e) {
				Activator.logError(e);
			}
		}
	}	
}
