package com.versionone.taskview.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.taskview.Activator;

class RefreshAction extends Action {

    private TaskView workItemView;
    private TreeViewer workitemViewer;

    public RefreshAction(TaskView workItemView, TreeViewer workitemViewer) {
        this.workItemView = workItemView;
        this.workitemViewer = workitemViewer;

        setText("Refresh");
        setToolTipText("Refresh");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.REFRESH_IMAGE_ID));
    }

    public void run() {
        workItemView.enableViewerAndActions(false);
        try {
            Activator.connect();            
        } catch (Exception e) {
            Activator.logError(e);
            MessageDialog.openError(workitemViewer.getTree().getShell(), "Task View Error",
                    "Error Occurred Retrieving Task. Check ErrorLog for more Details");
        }
        workItemView.enableViewerAndActions(true);
        workItemView.reCreateTable();
        //workItemView.updateStatusCodes();
    }
}
