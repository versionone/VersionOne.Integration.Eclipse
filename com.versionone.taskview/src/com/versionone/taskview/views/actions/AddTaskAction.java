package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;

import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.EntityType;
import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.SecondaryWorkitem;
import com.versionone.common.sdk.Workitem;
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
        try {
            final Workitem currentItem = workitemView.getCurrentWorkitem();
            final SecondaryWorkitem newItem;
            if (currentItem instanceof SecondaryWorkitem) {
                newItem = ((SecondaryWorkitem) currentItem).parent.createChild(EntityType.Task);
            } else if (currentItem instanceof PrimaryWorkitem) {
                newItem = ((PrimaryWorkitem) currentItem).createChild(EntityType.Task);
            } else {
                throw new IllegalStateException("Wrong current Workitem:" + currentItem);
            }
            workitemView.refreshViewer(new StructuredSelection(newItem));
        } catch (DataLayerException e) {
            Activator.logError(e);
            MessageDialog.openError(workitemView.getViewer().getControl().getShell(), "Task View Error",
                    "Error during addition new task. Check Error Log for more details.");
        }
    }
}