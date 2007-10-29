package com.versionone.taskview.views;

import org.eclipse.jface.dialogs.MessageDialog;
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
		if(validate(value.toString())) {
			setValue((Task)element, value);
			getViewer().update(element, null);
		}
		else {
			MessageDialog.openError(this.getViewer().getControl().getShell(), "Task View", errorMessage);
		}
		
	}		

	protected String errorMessage = "Invalid Value";
	protected boolean validate(String testMe) {
		return true;
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
				element.setEstimate(Integer.parseInt(value.toString()));
			} catch (Exception e) {
				Activator.logError(e);
			}
		}
		
		@Override
		protected boolean validate(String testMe) {
			if(0 == testMe.length()) {
				errorMessage = "Estimate must contain a value";
				return false;
			}
			else if (0.0 >= Float.parseFloat(testMe.toString())) {
				errorMessage = "Estimate must be greater than 0";
				return false;
			}
			return true;
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
				if(0 != value.toString().length()) {
					element.setEffort(Float.parseFloat(value.toString()));
				}
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
				element.setToDo(Float.parseFloat(value.toString()));
			} catch (Exception e) {
				Activator.logError(e);
			}
		}

		@Override
		protected boolean validate(String testMe) {
			if(0 == testMe.length()) {
				errorMessage = "To-Do must contain a value";
				return false;
			}
			else if (0.0 >= Float.parseFloat(testMe.toString())) {
				errorMessage = "To-Do cannot be less than 0";
				return false;				
			}
			return true;
		}
		
	}	
}
