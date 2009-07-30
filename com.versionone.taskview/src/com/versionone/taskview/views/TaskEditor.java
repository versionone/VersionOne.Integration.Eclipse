package com.versionone.taskview.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.Task;
import com.versionone.taskview.Activator;

/**
 * Abstract base class for editing Task Attributes
 * 
 * @author Jerry D. Odenwelder Jr.
 *
 */
abstract public class TaskEditor extends EditingSupport {

	static final String ERROR_VALUE = "*** Error ***";
	
	// the editor
	protected TextCellEditor _editor;
	
	public TaskEditor(TreeViewer viewer) {
		super(viewer);
		_editor = new TextCellEditor(viewer.getTree());
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
		try {
			setValue((Task)element, value);
			getViewer().update(element, null);
		} catch (IllegalArgumentException e) {
			_editor.setValue("");		// prevents two error dialogs
			Activator.logError(e);
			MessageDialog.openError(this.getViewer().getControl().getShell(), "Task View", e.getMessage());
		} catch (Exception e) {
			_editor.setValue("");		// prevents two error dialogs
			Activator.logError(e);
			MessageDialog.openError(this.getViewer().getControl().getShell(), "Task View", "Error updating field. Check Error log for more information.");
		}
	}	
	
	/**
	 * Derived class must update this method and set the appropriate property on the Task
	 * @param element
	 * @param value
	 */
	abstract protected void setValue(Task element, Object value) throws Exception;
	
///////////////////////////////////////////////////////////////////////////////
// Concrete Editors ///////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

	/**
	 * Edit the Task Name
	 */
	public static class NameEditor extends TaskEditor {
		
		String oldValue;
		public NameEditor(TreeViewer viewer) {
			super(viewer);
		}

		@Override
		protected Object getValue(Object element) {
			try {
				oldValue = ((Task)element).getName(); 
				return oldValue;
			} catch (Exception e) {
				Activator.logError(e);
			}
			return ERROR_VALUE; 
		}

		@Override
		protected void setValue(Task element, Object value) throws Exception {
			String newValue = value.toString();
			if(!oldValue.equals(newValue)) {
				element.setName(newValue);
			}
		}
	}

	/**
	 * Edit 'float' type attributes
	 */
	public static abstract class FloatEditor extends TaskEditor {

		double oldValue;

		public FloatEditor(TreeViewer viewer) {
			super(viewer);
		}

		@Override
		protected Object getValue(Object element) {
			try {
				oldValue = getValue(((Task)element));
				if(-1 == oldValue)
					return "";
				return String.valueOf(oldValue);
			} catch (Exception e) {
				Activator.logError(e);
			}
			return ERROR_VALUE;
		}
		
		@Override
		protected void setValue(Task element, Object value) throws Exception {
			if(0 != value.toString().length()) {
				float newValue = Float.parseFloat(value.toString());
				if(oldValue != newValue)
					setFloatValue(element, newValue);
			}
		}
		

		/**
		 * Get the value from the task
		 * @param task - instance to use
		 * @return float value or -1 if an error occurs
		 */
		abstract double getValue(Task task) throws Exception;
		
		abstract void setFloatValue(Task task, double value) throws Exception;
	}
		
	/**
	 * Edit the Detail Estimate
	 */
	public static class EstimateEditor extends FloatEditor {
		
		public EstimateEditor(TreeViewer viewer) {
			super(viewer);
		}

		protected double getValue(Task task) throws Exception {
			return task.getEstimate();
		}

		@Override
		protected void setFloatValue(Task element, double value) throws Exception {
			element.setEstimate(value);
		}
	}
	
	/**
	 * Edit the Effort
	 */
	public static class EffortEditor extends FloatEditor {

		public EffortEditor(TreeViewer viewer) {
			super(viewer);
		}

		protected double getValue(Task element) throws Exception {
			double effort = element.getEffort(); 
			if(0 == effort)
				return -1;
			return effort;
		}

		@Override
		protected void setFloatValue(Task element, double value) throws Exception {
			element.setEffort(value);
		}
	}

	/**
	 * Edit the ToDo
	 */
	public static class ToDoEditor extends FloatEditor {

		public ToDoEditor(TreeViewer viewer) {
			super(viewer);
		}

		protected double getValue(Task element) throws Exception {
			return element.getToDo();
		}

		@Override
		protected void setFloatValue(Task element, double value) throws Exception {
			element.setToDo(value);
		}
	}
}
