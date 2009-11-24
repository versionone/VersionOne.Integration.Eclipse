package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;

import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.ValidatorException;
import com.versionone.common.sdk.Entity;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.TaskView;

class QuickCloseAction extends Action {

    private final TaskView view;
    
    QuickCloseAction(TaskView workItemView) {
        this.view = workItemView; 
        
        setText("Quick close");
        setToolTipText("Quick close");
    }
    
    @Override
    public void run() {
        try {
            Entity item = view.getCurrentWorkitem();
            if (item != null) {
                item.quickClose();
                view.refreshViewer(null);
            }
        } catch (ValidatorException ex) {
            Activator.logWarning("Workitem cannot be closed because some required fields are empty:" + ex.getMessage());
            view.showMessage("Workitem cannot be closed because some required fields are empty:" + ex.getMessage());
        } catch (DataLayerException ex) {
            Activator.logError(ex);
            MessageDialog.openError(view.getViewer().getControl().getShell(), "Task View Error",
                    "Error during closing Workitem. Check Error Log for more details.");
        }
    }
}
