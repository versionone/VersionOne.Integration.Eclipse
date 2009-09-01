package com.versionone.taskview.views;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

class ProjectAction extends Action {
    private TaskView workItemView;
    private TreeViewer workItemViewer;

    public ProjectAction(TaskView workItemView, TreeViewer workitemViewer) {
        this.workItemView = workItemView;
        this.workItemViewer = workitemViewer;

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

        
        try {
            ProjectSelectDialog projectSelectDialog = new ProjectSelectDialog(workItemViewer.getControl().getShell(), projectList,
                    ApiDataLayer.getInstance().getCurrentProject());
            projectSelectDialog.create();
            projectSelectDialog.open();
        } catch (Exception ex) {
            Activator.logError(ex);
            MessageDialog.openError(workItemViewer.getTree().getShell(), "Project list error",
            "Error Occurred Retrieving Task. Check ErrorLog for more Details");
        }
        
        workItemView.loadTable();
        workItemView.enableViewerAndActions(true);
    }
}
