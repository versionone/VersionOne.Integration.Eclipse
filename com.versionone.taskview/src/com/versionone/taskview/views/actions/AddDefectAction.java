package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.WorkitemType;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.TaskView;

public class AddDefectAction extends Action {
    
    private final TaskView workitemView;

    AddDefectAction(TaskView workItemView) {
        this.workitemView = workItemView;

        setText("Add new defect");
        setToolTipText("Add new defect");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ADD_DEFECT_ID));
    }

    @Override
    public void run() {

        try {
            Workitem newItem = ApiDataLayer.getInstance().createNewWorkitem(WorkitemType.Defect, null);
            workitemView.refreshViewer(new StructuredSelection(newItem));
        } catch (DataLayerException e) {
            Activator.logError(e);
            MessageDialog.openError(workitemView.getViewer().getControl().getShell(), "Task View Error",
                    "Error during addition new defect. Check Error Log for more details.");
        }

    }    
}
