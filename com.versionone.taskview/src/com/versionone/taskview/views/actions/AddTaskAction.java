package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;

import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.WorkitemType;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.TaskView;

class AddTaskAction extends Action {

    private final TaskView workitemView;

    AddTaskAction(TaskView workItemView) {
        this.workitemView = workItemView;

        setText("Add new task");
        setToolTipText("Add new task");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ADD_TASK_ID));
    }
    
    @Override
    public void run() {
        Workitem item = workitemView.getCurrentWorkitem();        
        item = item.parent != null ? item.parent : item;        
        try {
            Workitem newItem = item.createChild(WorkitemType.Task);
            workitemView.refreshViewer(new StructuredSelection(newItem));
            //treeViewer.setSelection(new StructuredSelection(newItem), true);
        } catch (DataLayerException e) {
            Activator.logError(e);
            MessageDialog.openError(workitemView.getViewer().getControl().getShell(), "Task View Error",
                    "Error during addition new task. Check Error Log for more details.");
        }
    }
}