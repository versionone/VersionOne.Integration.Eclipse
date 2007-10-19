package com.versionone.taskview.views;


import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.versionone.apiclient.IMetaModel;
import com.versionone.apiclient.IServices;
import com.versionone.apiclient.V1Exception;
import com.versionone.taskview.Activator;
import com.versionone.taskview.internal.IProjectTreeNode;
import com.versionone.taskview.internal.ProjectTreeNode;
import com.versionone.taskview.internal.Task;
import com.versionone.taskview.internal.V1Server;
import com.versionone.taskview.preferences.PreferenceConstants;

/**
 * VersionOne Task View
 * 
 * @author Jerry D. Odenwelder Jr.
 *
 */
public class TaskView extends ViewPart implements IPropertyChangeListener {
	
	public static final String EFFORT_COLUMN_PROPERTY = "Effort";	
	
	private TableViewer viewer;
	private Action selectProjectAction = null;
	private Action refreshAction = null;
		
	/**
	 * The constructor.
	 */
	public TaskView() {
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		configureTable();

		makeActions();
		contributeToActionBars();
		
//		hookContextMenu();
//		hookDoubleClickAction();
		
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
		
		if(isEnabled) {
			viewer.setContentProvider(new ViewContentProvider());
			viewer.setLabelProvider(new ViewLabelProvider());				
			loadTable();
		}
		else {
			viewer.getTable().clearAll();
			viewer.setLabelProvider(new ErrorLabelProvider());
			viewer.setContentProvider(new ErrorContentProvider("Enable VersionOne Task List"));
			viewer.setSorter(null);
			viewer.setInput(getViewSite());
		}
	}	

	/**
	 * Configure the table
	 */
	private void configureTable() {
		final Table table = viewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setEnabled(isEnabled());
		
		final String[] columnNames = new String[] {"Story", "Task Name", "ID", "Detail Estimate", "To Do", "Status"};		
		// column properties are the Attribute names in the DOM document
		final String[] columnProperties = new String[] {"Parent.Name", "Name", "Number", "DetailEstimate", "ToDo", "Status.Name"};		
		int[] columnWidth     = {200, 150, 70, 100, 50, 100};
		int[] columnAlignment = {SWT.LEFT, SWT.LEFT,SWT.LEFT,SWT.CENTER,SWT.CENTER,SWT.LEFT};
		
		for(int i = 0; i < columnNames.length; ++i) {
			TableColumn tc = new TableColumn(table, columnAlignment[i]);
			tc.setText(columnNames[i]);
			tc.setWidth(columnWidth[i]);
		}
		
		viewer.setColumnProperties(columnProperties);
		viewer.setCellEditors(new CellEditor[] { new TextCellEditor(viewer.getTable()),
				new TextCellEditor(viewer.getTable()),
				new TextCellEditor(viewer.getTable()),
				new TextCellEditor(viewer.getTable()),
				new TextCellEditor(viewer.getTable()),
				new TextCellEditor(viewer.getTable())});
		
//		viewer.setCellModifier(new TextCellModifier(viewer));
		
		if(this.isTrackEffort()) {
			addEffortColumns();
		}
	}

	/**
	 * Adds the columns needed to track effort
	 */
	private void addEffortColumns() {
		// Add Cell Editors
		ArrayList<CellEditor> editorList = new ArrayList<CellEditor>(Arrays.asList(viewer.getCellEditors()));
		editorList.add(4, new TextCellEditor(viewer.getTable()));
		editorList.add(4, new TextCellEditor(viewer.getTable()));
		CellEditor[] editors = new CellEditor[editorList.size()];
		editorList.toArray(editors);
		viewer.setCellEditors(editors);		
		
		// Add Properties
		ArrayList<String> propertyList = new ArrayList<String>(Arrays.asList((String[])viewer.getColumnProperties()));
		propertyList.add(4, "Actuals.Value.@Sum");
		propertyList.add(5, EFFORT_COLUMN_PROPERTY);		
		String[] columnProperties = new String[propertyList.size()];
		propertyList.toArray(columnProperties);
		viewer.setColumnProperties(columnProperties);

		// Columns
		TableColumn doneColumn = new TableColumn(viewer.getTable(), SWT.CENTER, 4);
		doneColumn.setText("Done");
		doneColumn.setWidth(50);
		
		TableColumn todoColumn = new TableColumn(viewer.getTable(), SWT.CENTER, 5);
		todoColumn.setText("Effort");
		todoColumn.setWidth(50);

		viewer.refresh();
	}

	/**
	 * removes the columns needed when tracking effort
	 */
	private void removeEffortColumns() {
		
		ArrayList<CellEditor> editorList = new ArrayList<CellEditor>(Arrays.asList(viewer.getCellEditors()));
		editorList.remove(5);
		editorList.remove(4);
		CellEditor[] editors = new CellEditor[editorList.size()];
		editorList.toArray(editors);
		viewer.setCellEditors(editors);
		
		ArrayList<String> propertyList = new ArrayList<String>(Arrays.asList((String[])viewer.getColumnProperties()));
		propertyList.remove(5);
		propertyList.remove(4);
		String[] columnProperties = new String[propertyList.size()];
		propertyList.toArray(columnProperties);
		viewer.setColumnProperties(columnProperties);
		viewer.getTable().getColumn(5).dispose();
		viewer.getTable().getColumn(4).dispose();
		viewer.refresh();
	}
	
	private void makeActions() {
		selectProjectAction  = new Action() {
			public void run() {
				IProjectTreeNode root = getProjectTreeNode();
				IProjectTreeNode selectNode = null;
				String projectToken = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PROJECT_TOKEN); 
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
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refresh");
		refreshAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.REFRESH_IMAGE_ID));		
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(selectProjectAction);
		manager.add(refreshAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(selectProjectAction);
		manager.add(refreshAction);
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
	 * This method added for testability.  It initializes the underlying V1Server
	 * with the IServices and IMetamodel provided. 
	 * 
	 * NOTE: This method _must_ be called prior to asking Eclipse for this control
	 *       otherwise Eclipse will also initialize the data
	 * 
	 * @param services  - Services to use for testing
	 * @param metaModel - MetaModel to use for testing.
	 */
	public static void initializeForTesting(IServices services, IMetaModel metaModel) {
		V1Server.initialize(services, metaModel);
	}

	/**
	 * Determine if VersionOne Task List is tracking effort
	 * @return
	 */
	private boolean isTrackEffort() {
		return Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_TRACK_EFFORT);
	}
	
	/**
	 * Determine if VersionOne Task List is enabled 
	 * @return
	 */
	private boolean isEnabled() {
		return Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_ENABLED);
	}
	
	/**
	 * This content provider is used when there is an error creating the view (i.e, it's not enabled)
	 * @author Jerry D. Odenwelder Jr.
	 */
	class ErrorContentProvider implements IStructuredContentProvider {
		String message;
		ErrorContentProvider(String text) {
			message = text;
		}
		public Object[] getElements(Object inputElement) {
			return new String[] {message};
		}

		public void dispose() {		
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	/**
	 * This label provider is used when there is an error creating the view (i.e. It's not enabed)
	 * @author Jerry D. Odenwelder Jr.
	 */
	class ErrorLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if(0 == columnIndex) {
				return element.toString();
			}
			else {
				return "";
			}
		}
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
	 * LabelProvider for VersionOne Task
	 * @author jerry
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		public String getColumnText(Object obj, int index) {
			if( (null != obj) && (obj instanceof Task)) { 
				String[] properties = (String[])viewer.getColumnProperties();
				Task task = (Task) obj;
				return task.getValue(properties[index]);
			} else {
				return "";
			}
		}
		
		public Image getColumnImage(Object obj, int index) {
			Image rc = null;
			if(0 == index) {
				rc = getImage(obj);
			}
			return rc;
		}
		
		public Image getImage(Object obj) {
			return Activator.getDefault().getImageRegistry().get(Activator.TASK_IMAGE_ID);
		}
	}
	
	/**
	 * Called when preferences change
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if(property.equals(PreferenceConstants.P_ENABLED)) {
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
	}

	/**
	 * Get the projects from VersionOne 
	 * @return
	 */
	private IProjectTreeNode getProjectTreeNode() {
		try {
			return V1Server.getInstance().getProjects();
		} catch (V1Exception e) {
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
		} catch (V1Exception e) {
			Activator.logError(e);
			MessageDialog.openError(viewer.getControl().getShell(), "Task View Error", "Error Occurred Retrieving Task. Check ErrorLog for more Details");
		}
	}

//	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu");
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				TaskView.this.fillContextMenu(manager);
//			}
//		});
//		Menu menu = menuMgr.createContextMenu(viewer.getControl());
//		viewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuMgr, viewer);
//	}
//
//	private void fillContextMenu(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//		// Other plug-ins can contribute there actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//	}
//	
//	private void hookDoubleClickAction() {
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				doubleClickAction.run();
//			}
//		});
//	}
//
//	private void showMessage(String message) {
//		MessageDialog.openInformation(viewer.getControl().getShell(), "Task View", message);
//	}
	
}
