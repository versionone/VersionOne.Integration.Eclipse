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
 * 
 * Note: I choose to use my own validation over ICellEditorValidator because
 *       the latter approach wanted to validate as I typed and I want 
 *       validation when the user presses enter or tabs out of the field
 *       
 * @author Jerry D. Odenwelder Jr.
 *
 */
abstract public class TaskEditor extends EditingSupport {

	static final String ERROR_VALUE = "*** Error ***";
	
	// the editor
	protected TextCellEditor _editor;
	
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
		String errorMessage = validate(value.toString());
		if(null == errorMessage) {
			setValue((Task)element, value);
			getViewer().update(element, null);
		}
		else {
			MessageDialog.openError(this.getViewer().getControl().getShell(), "Task View", errorMessage);
		}		
	}		

	/**
	 * Validate the data
	 * @param testMe string to evaluate
	 * @return null if everything is okay, otherwise return the message to display
	 */
	protected String validate(String testMe) {
		return null;
	}

	/**
	 * Derived class must update this method and set the appropriate property on the Task
	 * @param element
	 * @param value
	 */
	abstract protected void setValue(Task element, Object value);
	
	/**
	 * Edit 'float' type attributes
	 */
	public static abstract class FloatEditor extends TaskEditor {
		
		public FloatEditor(TableViewer viewer) {
			super(viewer);
		}

		@Override
		protected Object getValue(Object element) {
			try {
				float temp = getValue(((Task)element));
				if(0 == temp)
					return "";
				return String.valueOf(temp);
			} catch (Exception e) {
				Activator.logError(e);
			}
			return ERROR_VALUE;
		}

		/**
		 * Get the value from the task
		 * @param task - instance to use
		 * @return float value or -1 if an error occurs
		 */
		abstract float getValue(Task task);
	}
	
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
			return ERROR_VALUE; 
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
	public static class EstimateEditor extends FloatEditor {
		
		public EstimateEditor(TableViewer viewer) {
			super(viewer);
		}

		protected float getValue(Task task) {
			try {
				return task.getEstimate();
			} catch (Exception e) {
				Activator.logError(e);
			}
			return -1;
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
		protected String validate(String testMe) {
			String rc = null;
			if(0 == testMe.length()) {
				rc = "Estimate must contain a value";
			}
			else if (0.0 >= Float.parseFloat(testMe.toString())) {
				rc = "Estimate must be greater than 0";
			}
			return rc;
		}
	}
	
	/**
	 * Edit the Effort
	 */
	public static class EffortEditor extends FloatEditor {
		
		public EffortEditor(TableViewer viewer) {
			super(viewer);
		}

		protected float getValue(Task element) {
			return element.getEffort();
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
	public static class ToDoEditor extends FloatEditor {
		
		public ToDoEditor(TableViewer viewer) {
			super(viewer);
		}

		protected float getValue(Task element) {
			try {
				return element.getToDo();
			} catch (Exception e) {
				Activator.logError(e);
			}
			return -1;
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
		protected String validate(String testMe) {
			String rc = null;
			if(0 == testMe.length()) {
				rc = "To-Do must contain a value";
			}
			else if (0.0 >= Float.parseFloat(testMe.toString())) {
				rc = "To-Do cannot be less than 0";				
			}
			return rc;
		}
	}
	
//	public static class IdCellEditor extends TaskEditor {
//
//		public IdCellEditor(TableViewer viewer) {
//			super(viewer);
//		}
//
//		@Override
//		protected void setValue(Task element, Object value) {
//			// TODO Auto-generated method stub
//		}
//
//		@Override
//		protected Object getValue(Object element) {
//			try {
//				return ((Task)element).getID();
//			}
//			catch(Exception e) {
//				Activator.logError(e);
//			}
//			return ERROR_VALUE;
//		}
//		
//	}
}
