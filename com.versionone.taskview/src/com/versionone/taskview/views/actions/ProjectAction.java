package com.versionone.taskview.views.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.Project;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.ProjectSelectDialog;
import com.versionone.taskview.views.ProxySelectionProvider;
import com.versionone.taskview.views.TaskView;

class ProjectAction extends Action {

    private final TaskView workitemView;
    private final TreeViewer treeViewer;
    private final IWorkbenchPartSite site;

    ProjectAction(TaskView workItemView, IWorkbenchPartSite iWorkbenchPartSite) {
        this.workitemView = workItemView;
        this.treeViewer = workItemView.getViewer();
        this.site = iWorkbenchPartSite;

        setText("Select Project");
        setToolTipText("Select Project");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.FILTER_IMAGE_ID));
    }

    @Override
    public void run() {
        try {
            final List<Project> projectList = ApiDataLayer.getInstance().getProjectTree();
            workitemView.enableTreeAndActions(false);
            ISelectionProvider oldProvider = site.getSelectionProvider();
            List<ISelectionChangedListener> listeners = ((ProxySelectionProvider) oldProvider).getListeners();
            openDialog(projectList, listeners);
            
            oldProvider.setSelection(new StructuredSelection(new Object[] { null }));
            site.setSelectionProvider(oldProvider);
            workitemView.loadDataToTable();
            workitemView.enableTreeAndActions(true);
        } catch (Exception ex) {
            Activator.logError(ex);
            workitemView.showMessage("Error Occurred Retrieving Projects List. Check ErrorLog for more Details");
            return;
        }
    }

    private void openDialog(List<Project> projects, List<ISelectionChangedListener> listeners)
            throws DataLayerException {
        final Shell shell = treeViewer.getControl().getShell();
        final Project project = ApiDataLayer.getInstance().getCurrentProject();
        final ProjectSelectDialog dialog = new ProjectSelectDialog(shell, projects, project);

        dialog.create();
        final ProxySelectionProvider proxy = new ProxySelectionProvider(dialog.getTreeViewer(), listeners);
        dialog.setCurrentProject();
        site.setSelectionProvider(proxy);
        dialog.open();
    }

}