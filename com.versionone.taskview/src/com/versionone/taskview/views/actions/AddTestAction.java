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

public class AddTestAction extends Action {

    private final TaskView workitemView;

    AddTestAction(TaskView workitemView) {
        this.workitemView = workitemView;

        setText("Add new test");
        setToolTipText("Add new test");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ADD_TEST_ID));
    }

    @Override
    public void run() {
        try {
            final Workitem currentItem = workitemView.getCurrentWorkitem();
            final PrimaryWorkitem parent;
            if (currentItem instanceof SecondaryWorkitem) {
                parent = ((SecondaryWorkitem) currentItem).parent;
            } else if (currentItem instanceof PrimaryWorkitem) {
                parent = ((PrimaryWorkitem) currentItem);
            } else {
                throw new IllegalStateException("Wrong current Workitem:" + currentItem);
            }
            workitemView.refreshViewer(new StructuredSelection(parent.createChild(EntityType.Test)));
        } catch (DataLayerException e) {
            Activator.logError(e);
            MessageDialog.openError(workitemView.getViewer().getControl().getShell(), "Workitem View Error",
                    "Error during addition new test. Check Error Log for more details.");
        }
    }
}
