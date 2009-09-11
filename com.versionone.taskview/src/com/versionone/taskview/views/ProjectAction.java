package com.versionone.taskview.views;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

class ProjectAction extends Action {
    private TaskView workItemView;
    private TreeViewer workItemViewer;
    private IWorkbenchPartSite iWorkbenchPartSite;

    public ProjectAction(TaskView workItemView, TreeViewer workitemViewer, IWorkbenchPartSite iWorkbenchPartSite) {
        this.workItemView = workItemView;
        this.workItemViewer = workitemViewer;
        this.iWorkbenchPartSite = iWorkbenchPartSite;

        setText("Select Project");
        setToolTipText("Select Project");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.FILTER_IMAGE_ID));
    }

    @Override
    public void run() {
        if (!isEnabled()) {
            return;
        }
        workItemView.enableViewerAndActions(false);
        List<Workitem> projectList = null;
        try {
            projectList = ApiDataLayer.getInstance().getProjectTree();
        } catch (Exception ex) {
            Activator.logError(ex);
            MessageDialog.openError(workItemViewer.getTree().getShell(), "Project list error",
            "Error Occurred Retrieving Task. Check ErrorLog for more Details");
        }

        ISelectionProvider oldProvider = iWorkbenchPartSite.getSelectionProvider();
        List<ISelectionChangedListener> listeners = ((ProxySelectionProvider)oldProvider).getListeners();
        
        try {
            ProjectSelectDialog projectSelectDialog = new ProjectSelectDialog(workItemViewer.getControl().getShell(), projectList,
                    ApiDataLayer.getInstance().getCurrentProject());                        
            
            projectSelectDialog.create();
            ProxySelectionProvider proxy = new ProxySelectionProvider(projectSelectDialog.getTreeViewer());
            for (ISelectionChangedListener listener : listeners) {
                proxy.addSelectionChangedListener(listener);
            }
            projectSelectDialog.setCurrentProject();
            iWorkbenchPartSite.setSelectionProvider(proxy);
            projectSelectDialog.open();
        } catch (Exception ex) {
            Activator.logError(ex);
            MessageDialog.openError(workItemViewer.getTree().getShell(), "Project list error",
            "Error Occurred Retrieving Task. Check ErrorLog for more Details");
        }
        iWorkbenchPartSite.setSelectionProvider(oldProvider);        
        workItemView.loadTable();
        workItemView.enableViewerAndActions(true);
    }
}
