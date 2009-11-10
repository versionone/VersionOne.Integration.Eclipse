package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;

import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.TaskView;

class SignUpAction extends Action {
    
    private final TaskView view;
    
    SignUpAction(TaskView workItemView) {
        this.view = workItemView; 
        
        setText("Signup");
        setToolTipText("Signup");                
    }
    
    public void run(Event e) {
        try {
            Workitem item = view.getCurrentWorkitem();
            if (item != null) {
                item.signup();
                view.refreshViewer(null);
            }
        } catch (DataLayerException ex) {
            Activator.logError(ex);
            MessageDialog.openError(view.getViewer().getControl().getShell(), "Task View Error",
                    "Error during signing up. Check Error Log for more details.");
        }
    }
}
