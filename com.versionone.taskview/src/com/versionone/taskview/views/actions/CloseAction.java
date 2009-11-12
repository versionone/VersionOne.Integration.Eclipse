package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;

import com.versionone.taskview.views.CloseWorkitemDialog;
import com.versionone.taskview.views.TaskView;

class CloseAction extends Action {
    
    private final TaskView view;
    
    CloseAction(TaskView workItemView) {
        this.view = workItemView;

        setText("Close");
        setToolTipText("Close");
    }

    @Override
    public void run() {
        CloseWorkitemDialog closeDialog = new CloseWorkitemDialog(view.getViewer().getControl().getShell(), view.getCurrentWorkitem(), view);
        closeDialog.setBlockOnOpen(true);
        closeDialog.open();
    }    

}
