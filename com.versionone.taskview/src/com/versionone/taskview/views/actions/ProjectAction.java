package com.versionone.taskview.views.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.ProjectSelectDialog;
import com.versionone.taskview.views.ProxySelectionProvider;
import com.versionone.taskview.views.TaskView;

class ProjectAction extends Action {

    private final TaskView workitemView;
    private final TreeViewer treeViewer;
    private final IWorkbenchPartSite site;

    public ProjectAction(TaskView workItemView, IWorkbenchPartSite iWorkbenchPartSite) {
        this.workitemView = workItemView;
        this.treeViewer = workItemView.getViewer();
        this.site = iWorkbenchPartSite;

        setText("Select Project");
        setToolTipText("Select Project");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.FILTER_IMAGE_ID));
    }

    @Override
    public void run() {
        if (!isEnabled()) {
            return;
        }
        List<Workitem> projectList;
        try {
            projectList = ApiDataLayer.getInstance().getProjectTree();
        } catch (Exception ex) {
            Activator.logError(ex);
            workitemView.showMessage("Error Occurred Retrieving Projects List. Check ErrorLog for more Details");
            return;
        }

        workitemView.enableTreeAndActions(false);
        ISelectionProvider oldProvider = site.getSelectionProvider();
        List<ISelectionChangedListener> listeners = ((ProxySelectionProvider) oldProvider).getListeners();
        openDialog(projectList, listeners);
        oldProvider.setSelection(new StructuredSelection(new Object[] { null }));
        site.setSelectionProvider(oldProvider);
        workitemView.loadDataToTable();
        workitemView.enableTreeAndActions(true);
    }

    private void openDialog(List<Workitem> projectList, List<ISelectionChangedListener> listeners) {
        try {
            ProjectSelectDialog projectSelectDialog = new ProjectSelectDialog(treeViewer.getControl().getShell(),
                    projectList, ApiDataLayer.getInstance().getCurrentProject());

            projectSelectDialog.create();
            ProxySelectionProvider proxy = new ProxySelectionProvider(projectSelectDialog.getTreeViewer(), listeners);
            projectSelectDialog.setCurrentProject();
            site.setSelectionProvider(proxy);
            projectSelectDialog.open();
        } catch (Exception ex) {
            Activator.logError(ex);
            workitemView.showMessage("Error Occurred Getting Projects. Check ErrorLog for more Details");
        }
    }

}