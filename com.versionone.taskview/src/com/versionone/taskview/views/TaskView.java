package com.versionone.taskview.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

import com.versionone.taskview.views.editors.ListEditor;
import com.versionone.taskview.views.editors.ReadOnlyEditor;
import com.versionone.taskview.views.editors.TextEditor;
import com.versionone.taskview.views.providers.SimpleProvider;

/**
 * VersionOne Task View
 * 
 * @author Jerry D. Odenwelder Jr.
 * 
 */
public class TaskView extends ViewPart implements IPropertyChangeListener {

    /*
     * These constants are the VersionOne names for the column titles. We
     * localize these values and use that name as column titles
     */
    private static final String V1_COLUMN_TITLE_ID = "ColumnTitle'ID";
    private static final String V1_COLUMN_TITLE_PARENT = "Story";
    private static final String V1_COLUMN_TITLE_TITLE = "Task";
    private static final String V1_COLUMN_TITLE_DETAIL_ESTIMATE = "ColumnTitle'DetailEstimate";
    private static final String V1_COLUMN_TITLE_TO_DO = "ColumnTitle'ToDo";
    private static final String V1_COLUMN_TITLE_STATUS = "ColumnTitle'Status";
    private static final String V1_COLUMN_TITLE_DONE = "ColumnTitle'Done";
    private static final String V1_COLUMN_TITLE_EFFORT = "ColumnTitle'Effort";

    private boolean isEffortColumsShow;
    private TreeViewer viewer;
    private Action selectProjectAction = null;
    private Action refreshAction = null;
    private Action saveAction = null;
    

    public TaskView() {}

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
        PreferencePage.getPreferences().addPropertyChangeListener(this);

        viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        if (isEnabled()) {
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
        viewer.getTree().setEnabled(isEnabled);
        viewer.getTree().setLinesVisible(isEnabled);
        viewer.getTree().setHeaderVisible(isEnabled);
        viewer.getTree().setEnabled(isEnabled);

        viewer.setContentProvider(new ViewContentProvider());

        if (isEnabled) {
            loadTable();
        } else {
            viewer.getTree().clearAll(true);
            viewer.setSorter(null);
            viewer.setInput(getViewSite());
        }
    }

    /**
     * Configure the table
     */
    private void configureTable() {
        TreeViewerColumn column = createTableViewerColumn(V1_COLUMN_TITLE_ID, 120, SWT.LEFT);
        column.setLabelProvider(new SimpleProvider(Workitem.IdProperty, true));
        column.setEditingSupport(new ReadOnlyEditor(Workitem.IdProperty, viewer));

        column = createTableViewerColumn(V1_COLUMN_TITLE_TITLE, 150, SWT.LEFT);
        column.setLabelProvider(new SimpleProvider(Workitem.NameProperty, false));
        column.setEditingSupport(new TextEditor(Workitem.NameProperty, viewer));

        column = createTableViewerColumn(V1_COLUMN_TITLE_STATUS, 100, SWT.LEFT);
        column.setLabelProvider(new SimpleProvider(Workitem.StatusProperty, false));
        column.setEditingSupport(new ListEditor(viewer, Workitem.StatusProperty));

        column = createTableViewerColumn(V1_COLUMN_TITLE_DETAIL_ESTIMATE, 100, SWT.CENTER);
        column.setLabelProvider(new SimpleProvider(Workitem.DetailEstimateProperty, false));
        column.setEditingSupport(new TextEditor(Workitem.DetailEstimateProperty, viewer));

        column = createTableViewerColumn(V1_COLUMN_TITLE_TO_DO, 50, SWT.CENTER);
        column.setLabelProvider(new SimpleProvider(Workitem.TodoProperty, false));
        column.setEditingSupport(new TextEditor(Workitem.TodoProperty, viewer));

        if (ApiDataLayer.getInstance().isTrackEffortEnabled()) {
            addEffortColumns();
        }
    }

    /**
     * Adds the columns needed to track effort
     */
    private void addEffortColumns() {
        TreeViewerColumn column = createTableViewerColumn(V1_COLUMN_TITLE_DONE, 50, SWT.CENTER, 4);
        column.setLabelProvider(new SimpleProvider(Workitem.DoneProperty, false));

        column = createTableViewerColumn(V1_COLUMN_TITLE_EFFORT, 50, SWT.CENTER, 5);
        column.setLabelProvider(new SimpleProvider(Workitem.EffortProperty, false));
        column.setEditingSupport(new TextEditor(Workitem.EffortProperty, viewer));

        viewer.refresh();
        isEffortColumsShow = true;
    }

    /**
     * removes the columns needed when tracking effort
     */
    private void removeEffortColumns() {
        viewer.getTree().getColumn(5).dispose();
        viewer.getTree().getColumn(4).dispose();
        viewer.refresh();
        isEffortColumsShow = false;
    }

    /**
     * Create the action menus
     */
    private void makeActions() {
        selectProjectAction = new ProjectAction(this, viewer);
        refreshAction = new RefreshAction(this, viewer);
        saveAction = new SaveAction(this, viewer);
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
     * This method added for testing. It provides access to the underlying
     * TableViewer
     * 
     * @return TableViewer used in this control
     */
    public TreeViewer getViewer() {
        return this.viewer;
    }

    /**
     * Determine if VersionOne Task List is enabled
     * 
     * @return
     */
    private boolean isEnabled() {
        return PreferencePage.getPreferences().getBoolean(PreferenceConstants.P_ENABLED);
    }

    /**
     * Called when preferences change
     */
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(PreferenceConstants.P_ENABLED)) {
            if (0 == viewer.getTree().getColumnCount()) {
                configureTable();
            }
            selectProvider();
        } else if (property.equals(PreferenceConstants.P_URL) || property.equals(PreferenceConstants.P_USER) ||
                property.equals(PreferenceConstants.P_PASSWORD) || property.equals(PreferenceConstants.P_MEMBER_TOKEN)) {
            try {
                Activator.connect();            
            } catch (Exception e) {
                Activator.logError(e);
                MessageDialog.openError(viewer.getTree().getShell(), "Task View Error",
                        "Error Occurred Retrieving Task. Check ErrorLog for more Details");
            }
            reCreateTable();
        }
        /*
        else if (property.equals(PreferenceConstants.P_TRACK_EFFORT)) {
            if (isTrackEffort()) {
                this.addEffortColumns();
            } else {
                this.removeEffortColumns();
            }
        }
         */
    }

    /**
     * Get the projects from VersionOne
     * 
     * @return
     */
    /*
    protected IProjectTreeNode getProjectTreeNode() {
        try {
            return V1Server.getInstance().getProjects();
        } catch (Exception e) {
            Activator.logError(e);
            MessageDialog.openError(viewer.getControl().getShell(), "Project View Error",
                    "Error Occurred Retrieving Projects. Check ErrorLog for more Details");
            return new ProjectTreeNode("root", "0");
        }
    }
    */

    /**
     * Load the Viewer with Task data
     */
    protected void loadTable() {
        try {
            // viewer.setInput(V1Server.getInstance().getTasks());
            // ApiDataLayer.getInstance().connect("http://jsdksrv01:8080/VersionOne/",
            // "admin", "admin", false);
            viewer.setInput(ApiDataLayer.getInstance().getWorkitemTree());
        } catch (Exception e) {
            /*
            Activator.logError(e);
            MessageDialog.openError(viewer.getControl().getShell(), "Task View Error",
                    "Error Occurred Retrieving Task. Check ErrorLog for more Details");
            */
        }
    }

    
    /**
     * Retrieve the StatusCodes from the server
     * 
     * @return StatusCodes from the server or an empty collection
     */
    /*
    private IStatusCodes getStatusValues() {
        try {
            return V1Server.getInstance().getTaskStatusValues();
        } catch (Exception e) {
            Activator.logError(e);
            showMessage("Error retrieving Task Status from server. Additional informaiton available in Error log.");
            return new IStatusCodes() {
                String[] _data = new String[] {};

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
    */

    /**
     * Create a TableViewerColumn with specified properties and append it to the
     * end of the table
     * 
     * Calls createTableViewerColumn(String label, int width, int alignment, int
     * index) with a -1 as the index
     * 
     * @param label
     *            - Column label
     * @param width
     *            - Column Width
     * @param alignment
     *            - Column alignment
     * @return new TableViewerColumn
     */
    TreeViewerColumn createTableViewerColumn(String label, int width, int alignment) {
        return createTableViewerColumn(label, width, alignment, -1);
    }

    /**
     * Create a TableViewerColumn at a specific column location
     * 
     * @param label
     *            - Column label
     * @param width
     *            - Column Width
     * @param alignment
     *            - Column alignment
     * @param index
     *            - location for column. -1 indicates the column goes at the end
     * @return new TableViewerColumn
     */
    TreeViewerColumn createTableViewerColumn(String label, int width, int alignment, int index) {
        TreeViewerColumn rc = null;
        if (-1 == index) {
            rc = new TreeViewerColumn(viewer, SWT.NONE);
        } else {
            rc = new TreeViewerColumn(viewer, SWT.NONE, index);
        }
        rc.getColumn().setWidth(width);
        rc.getColumn().setAlignment(alignment);
        try {
            rc.getColumn().setText(ApiDataLayer.getInstance().localizerResolve(label));
        } catch (DataLayerException e) {
            Activator.logError(e);
            rc.getColumn().setText("**Error**");
        }
        return rc;
    }

    private void hookDoubleClickAction() {
        //
        // Code to launch a browser when the user double clicks on a task. The
        // browser is instructed to navigate
        // to the asset detail page for that task.
        // This code is currently commented out because
        // a) with integrated authentication the server responds with a
        // "forbidden" message.
        // b) with v1 authentication, the user is always prompted for
        // credentials.
        // 
        // viewer.addDoubleClickListener(new IDoubleClickListener() {
        // public void doubleClick(DoubleClickEvent event) {
        // IStructuredSelection selection = (IStructuredSelection)
        // event.getSelection();
        // String oid = null;
        // try {
        // oid = ((Task)selection.getFirstElement()).getToken();
        // StringBuffer v1Url = new
        // StringBuffer(PreferencePage.getPreferences().getString(PreferenceConstants.P_URL));
        // v1Url.append("assetdetail.v1?Oid=");
        // v1Url.append(oid);
        // URL url = new URL(v1Url.toString());
        // PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
        // } catch (Exception e) {
        // Activator.logError(e);
        // }
        // }
        // });
    }

    protected void showMessage(String message) {
        MessageDialog.openInformation(viewer.getControl().getShell(), "Task View", message);
    }

    @Override
    public void dispose() {
        PreferencePage.getPreferences().removePropertyChangeListener(this);
        super.dispose();
    }

    /*
    protected void updateStatusCodes() {
        statusEditor.setStatusCodes(getStatusValues());
    }
    */
    protected void reCreateTable() {       
        
        if (isEffortColumsShow && !ApiDataLayer.getInstance().isTrackEffortEnabled()) {
            removeEffortColumns();
        } else if (!isEffortColumsShow && ApiDataLayer.getInstance().isTrackEffortEnabled()) {
            addEffortColumns();
        }
        loadTable();
    }

}
