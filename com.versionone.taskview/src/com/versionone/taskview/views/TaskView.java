package com.versionone.taskview.views;


import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.IProjectTreeNode;
import com.versionone.common.sdk.IStatusCodes;
import com.versionone.common.sdk.ProjectTreeNode;
import com.versionone.common.sdk.Task;
import com.versionone.common.sdk.V1Server;
import com.versionone.taskview.Activator;

/**
 * VersionOne Task View
 * 
 * @author Jerry D. Odenwelder Jr.
 *
 */
public class TaskView extends ViewPart implements IPropertyChangeListener {

	/**
	 * These constants are the VersionOne names for the column titles.  
	 * We localize these values and use that name as column titles
	 */
	private static final String V1_COLUMN_TITLE_ID              = "ColumnTitle'ID";
	private static final String V1_COLUMN_TITLE_PARENT          = "Story";
	private static final String V1_COLUMN_TITLE_TITLE           = "Task";
	private static final String V1_COLUMN_TITLE_DETAIL_ESTIMATE = "ColumnTitle'DetailEstimate";
	private static final String V1_COLUMN_TITLE_TO_DO           = "ColumnTitle'ToDo";
	private static final String V1_COLUMN_TITLE_STATUS          = "ColumnTitle'Status";
	private static final String V1_COLUMN_TITLE_DONE            = "ColumnTitle'Done";
	private static final String V1_COLUMN_TITLE_EFFORT          = "ColumnTitle'Effort";
	
	
	private TableViewer viewer;
	private StatusEditor statusEditor;
	private Action selectProjectAction = null;
	private Action refreshAction = null;
	private Action saveAction = null;
		
	/**
	 * The constructor.
	 */
	public TaskView() {
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		PreferencePage.getPreferences().addPropertyChangeListener(this);

		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		
		if(isEnabled()) {
			configureTable();
		}
		
		makeActions();
		contributeToActionBars();		
		hookDoubleClickAction();
		selectProvider();			

	}

	/**
	 * Select the content and label providers
	 */
	private void selectProvider() {

		boolean isEnabled = isEnabled();
		
		selectProjectAction.setEnabled(isEnabled);
		refreshAction.setEnabled(isEnabled);
		viewer.getTable().setEnabled(isEnabled);
		viewer.getTable().setLinesVisible(isEnabled);
		viewer.getTable().setHeaderVisible(isEnabled);
		viewer.getTable().setEnabled(isEnabled);

		viewer.setContentProvider(new ViewContentProvider());
		
		if(isEnabled) {		
			loadTable();			
		}
		else {
			viewer.getTable().clearAll();
			viewer.setSorter(null);
			viewer.setInput(getViewSite());
		}
	}	

	/**
	 * Configure the table
	 */
	private void configureTable() {

		TableViewerColumn column = createTableViewerColumn(V1_COLUMN_TITLE_ID, 70, SWT.LEFT);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				try {
					return ((Task)element).getID();
				} catch (Exception e) {
					Activator.logError(e);
				}
				return "*** Error ***";
			}
			
			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get(Activator.TASK_IMAGE_ID);
			}
		});
		column.setEditingSupport(new TaskIdEditor(viewer));

		createTableViewerColumn(V1_COLUMN_TITLE_PARENT, 200, SWT.LEFT).setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				try {
					return ((Task)element).getStoryName();
				} catch (Exception e) {
					Activator.logError(e);
				}
				return "*** Error ***";
			}
		});
		
		column = createTableViewerColumn(V1_COLUMN_TITLE_TITLE, 150, SWT.LEFT);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				try {
					return ((Task)element).getName();
				} catch (Exception e) {
					Activator.logError(e);
				}
				return "*** Error ***";
			}
		});
		column.setEditingSupport(new TaskEditor.NameEditor(viewer));

		column = createTableViewerColumn(V1_COLUMN_TITLE_DETAIL_ESTIMATE, 100, SWT.CENTER);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				try {
					float estimate = ((Task)element).getEstimate();
					if(-1 == estimate)
						return "";
					return String.valueOf(estimate);
				} catch (Exception e) {
					Activator.logError(e);
				}
				return "*** Error ***";
			}			
		});
		column.setEditingSupport(new TaskEditor.EstimateEditor(viewer));
		
		column = createTableViewerColumn(V1_COLUMN_TITLE_TO_DO, 50, SWT.CENTER);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				try {
					float todo = ((Task)element).getToDo();
					if(-1 == todo)
						return "";
					return String.valueOf(todo);
				} catch (Exception e) {
					Activator.logError(e);
				}
				return "*** Error ***";
			}			
		});
		column.setEditingSupport(new TaskEditor.ToDoEditor(viewer));
		
		column = createTableViewerColumn(V1_COLUMN_TITLE_STATUS, 100, SWT.LEFT);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				try {
					return getStatusValues().getDisplayFromOid(((Task)element).getStatus());
				} catch (Exception e) {
					Activator.logError(e);
				}
				return "*** Error ***";
			}			
		});

		if(this.isTrackEffort()) {
			addEffortColumns();
		}		

		statusEditor = new StatusEditor(viewer, getStatusValues()); 
		column.setEditingSupport(statusEditor);
	}

	/**
	 * Adds the columns needed to track effort
	 */
	private void addEffortColumns() {

		createTableViewerColumn(V1_COLUMN_TITLE_DONE, 50, SWT.CENTER, 4).setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element)  {
				try {
					return ((Task)element).getDone();
				} catch (Exception e) {
					Activator.logError(e);
				}
				return "*** Error ***";
			}			
		});
		
		TableViewerColumn column = createTableViewerColumn(V1_COLUMN_TITLE_EFFORT, 50, SWT.CENTER, 5);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				try {
					float effort = ((Task)element).getEffort();
					if(0 == effort)
						return "";
					return String.valueOf(effort);
				} catch (Exception e) {
					Activator.logError(e);
				}
				return "*** Error ***";
			}			
		});
		column.setEditingSupport(new TaskEditor.EffortEditor(viewer));
		
		viewer.refresh();
	}

	/**
	 * removes the columns needed when tracking effort
	 */
	private void removeEffortColumns() {

		viewer.getTable().getColumn(5).dispose();
		viewer.getTable().getColumn(4).dispose();		
		viewer.refresh();
	}

	/**
	 * Create the action menus
	 */
	private void makeActions() {
		selectProjectAction  = new Action() {
			public void run() {
				IProjectTreeNode root = getProjectTreeNode();
				IProjectTreeNode selectNode = null;
				String projectToken = PreferencePage.getPreferences().getString(PreferenceConstants.P_PROJECT_TOKEN); 
				if(!projectToken.equals("")) {
					selectNode = new ProjectTreeNode("", projectToken);
				}
				ProjectSelectDialog projectSelectDialog = new ProjectSelectDialog(viewer.getControl().getShell(), root, selectNode);
				projectSelectDialog.create();
				projectSelectDialog.open();
				loadTable();
			}
		};
		selectProjectAction.setText("Select Project");
		selectProjectAction.setToolTipText("Select Project");
		selectProjectAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.FILTER_IMAGE_ID));
		
		refreshAction = new Action() {
			public void run() {
				loadTable();
				statusEditor.setStatusCodes(getStatusValues());
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refresh");
		refreshAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.REFRESH_IMAGE_ID));
		
		saveAction = new Action() {
			public void run() {
				if(viewer.isCellEditorActive()) {
					viewer.getTable().getShell().traverse(SWT.TRAVERSE_TAB_NEXT);
				}
				TableItem[] rows = viewer.getTable().getItems();
				ArrayList<Task> saveUs = new ArrayList<Task>();
				for(int i = 0; i < rows.length; ++i) {
					if(((Task)rows[i].getData()).isDirty()) {
						saveUs.add((Task) rows[i].getData());
					}
				}
				if(0 != saveUs.size()) {
					try {
						V1Server.getInstance().save(saveUs);
					}
					catch(Exception e) {
						Activator.logError(e);
						showMessage("Error saving task. Check Error log for more information.");
					}		
					loadTable();
					statusEditor.setStatusCodes(getStatusValues());
				}
			}			
		};
		saveAction.setText("Save");
		saveAction.setToolTipText("Save");
		saveAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.SAVE_IMAGE_ID));	
	}

	// add actions to Action bars and pull down menu
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(selectProjectAction);
		manager.add(refreshAction);
		manager.add(saveAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(selectProjectAction);
		manager.add(refreshAction);
		manager.add(saveAction);
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * This method added for testing.  It provides access to the underlying TableViewer
	 * @return TableViewer used in this control
	 */	
	public TableViewer getViewer() {return this.viewer;}

	/**
	 * Determine if VersionOne Task List is tracking effort
	 * @return
	 */
	private boolean isTrackEffort() {
		return PreferencePage.getPreferences().getBoolean(PreferenceConstants.P_TRACK_EFFORT);
	}
	
	/**
	 * Determine if VersionOne Task List is enabled 
	 * @return
	 */
	private boolean isEnabled() {
		return PreferencePage.getPreferences().getBoolean(PreferenceConstants.P_ENABLED);
	}

	/**
	 * ContentProvider for VersionOne Task
	 * @author jerry
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {}
		public void dispose() {}
		public Object[] getElements(Object parent) {
			if(parent instanceof Task[]) {
				return (Object[]) parent;
			} else {
				return new Object[]{};
			}
		}
	}
	
	/**
	 * Called when preferences change
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if(property.equals(PreferenceConstants.P_ENABLED)) {
			if(0 == viewer.getTable().getColumnCount())
				configureTable();
			selectProvider();
		}
		else if(property.equals(PreferenceConstants.P_TRACK_EFFORT)) {
			if(isTrackEffort()) {
				this.addEffortColumns();
			}
			else {
				this.removeEffortColumns();
			}
		}
		else if(property.equals(PreferenceConstants.P_MEMBER_TOKEN)) {
			refreshAction.run();
		}
	}

	/**
	 * Get the projects from VersionOne 
	 * @return
	 */
	private IProjectTreeNode getProjectTreeNode() {
		try {
			return V1Server.getInstance().getProjects();
		} catch (Exception e) {
			Activator.logError(e);
			MessageDialog.openError(viewer.getControl().getShell(), "Project View Error", "Error Occurred Retrieving Projects. Check ErrorLog for more Details");			
			return new ProjectTreeNode("root", "0");
		}
	}

	/**
	 * Load the Viewer with Task data
	 */
	private void loadTable() {
		try {
			viewer.setInput(V1Server.getInstance().getTasks());
		} catch (Exception e) {
			Activator.logError(e);
			MessageDialog.openError(viewer.getControl().getShell(), "Task View Error", "Error Occurred Retrieving Task. Check ErrorLog for more Details");
		}
	}
	

	/**
	 * Retrieve the StatusCodes from the server 
	 * @return StatusCodes from the server or an empty collection
	 */
	private IStatusCodes getStatusValues() {
		try {
			return V1Server.getInstance().getTaskStatusValues();
		} catch (Exception e) {
			Activator.logError(e);
			showMessage("Error retrieving Task Status from server. Additional informaiton available in Error log.");
			return new IStatusCodes() {
				String[] _data = new String[]{}; 

				public String getDisplayValue(int index) {
					return "";
				}

				public String[] getDisplayValues() {
					return _data;
				}

				public int getOidIndex(String oid) {
					return 0;
				}

				public String getID(int value) {
					return "";
				}

				public String getDisplayFromOid(String oid) {
					return "";
				}				
			};
		}
	}
	

	/**
	 * Create a TableViewerColumn with specified properties and append it to the end of the table
	 * 
	 * Calls createTableViewerColumn(String label, int width, int alignment, int index) with a -1 as the index
	 * 
	 * @param label - Column label
	 * @param width - Column Width
	 * @param alignment - Column alignment
	 * @return new TableViewerColumn 
	 */
	TableViewerColumn createTableViewerColumn(String label, int width, int alignment) {
		return createTableViewerColumn(label, width, alignment, -1);
	}

	/**
	 * Create a TableViewerColumn at a specific column location
	 * 
	 * @param label - Column label
	 * @param width - Column Width
	 * @param alignment - Column alignment 
	 * @param index - location for column.  -1 indicates the column goes at the end
	 * @return new TableViewerColumn
	 */
	TableViewerColumn createTableViewerColumn(String label, int width, int alignment, int index) {
		TableViewerColumn rc = null;
		if(-1 == index) {
			rc = new TableViewerColumn(viewer,SWT.NONE);	
		}
		else {
			rc = new TableViewerColumn(viewer,SWT.NONE, index);
		}		
		rc.getColumn().setWidth(width);
		rc.getColumn().setAlignment(alignment);
		rc.getColumn().setText(V1Server.getInstance().getLocalString(label));
		return rc;
	}

	private void hookDoubleClickAction() {

//
// Code to launch a browser when the user double clicks on a task.  The browser is instructed to navigate
// to the asset detail page for that task.
// This code is currently commented out because 
//		a) with integrated authentication the server responds with a "forbidden" message.
//      b) with v1 authentication, the user is always prompted for credentials.
// 
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
//				String oid = null;
//				try {
//					oid = ((Task)selection.getFirstElement()).getToken();
//					StringBuffer v1Url = new StringBuffer(PreferencePage.getPreferences().getString(PreferenceConstants.P_URL));
//					v1Url.append("assetdetail.v1?Oid=");
//					v1Url.append(oid);
//					URL url = new URL(v1Url.toString());
//					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
//				} catch (Exception e) {
//					Activator.logError(e);
//				}
//			}
//		});
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Task View", message);
	}
	
}
