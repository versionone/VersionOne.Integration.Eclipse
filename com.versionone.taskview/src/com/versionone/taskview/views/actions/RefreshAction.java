package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;

import com.versionone.taskview.Activator;
import com.versionone.taskview.views.TaskView;

class RefreshAction extends Action {

    private final TaskView workitemView;

    public RefreshAction(TaskView workItemView) {
        this.workitemView = workItemView;

        setText("Refresh");
        setToolTipText("Refresh");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.REFRESH_IMAGE_ID));
    }

    @Override
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