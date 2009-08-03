package com.versionone.taskview.views;

import org.eclipse.jface.action.Action;

import com.versionone.taskview.Activator;

class RefreshAction extends Action {

    private TaskView workItemView;

    public RefreshAction(TaskView workItemView) {
        this.workItemView = workItemView;

        setText("Refresh");
        setToolTipText("Refresh");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.REFRESH_IMAGE_ID));
    }

    public void run() {
        workItemView.loadTable();
        workItemView.updateStatusCodes();
    }
}
