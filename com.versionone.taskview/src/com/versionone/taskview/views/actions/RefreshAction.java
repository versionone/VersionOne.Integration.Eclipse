package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.taskview.Activator;
import com.versionone.taskview.views.TaskView;

class RefreshAction extends Action {

    private final TaskView workitemView;
    private final TreeViewer treeViewer;

    public RefreshAction(TaskView workItemView, TreeViewer workitemViewer) {
        this.workitemView = workItemView;
        this.treeViewer = workitemViewer;

        setText("Refresh");
        setToolTipText("Refresh");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.REFRESH_IMAGE_ID));
    }

    public void run() {
        workitemView.enableTreeAndActions(false);
        try {// connect not throws -> delete try
            Activator.connect();
        } catch (Exception e) {
            Activator.logError(e);
            workitemView.showMessage("Error Occurred Retrieving Task. Check ErrorLog for more Details");
        }
        workitemView.enableTreeAndActions(true);
        workitemView.setupEffortColumns();
    }
}