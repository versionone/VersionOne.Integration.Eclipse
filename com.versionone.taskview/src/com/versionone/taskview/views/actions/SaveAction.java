package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.ValidatorException;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.ErrorMessageDialog;
import com.versionone.taskview.views.TaskView;

class SaveAction extends Action {

    private static final ApiDataLayer DATA = ApiDataLayer.getInstance();

    private final TaskView workitemView;

    public SaveAction(TaskView workitemView) {
        this.workitemView = workitemView;

        setText("Save");
        setToolTipText("Save");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.SAVE_IMAGE_ID));
    }

    @Override
    public void run() {
        final TreeViewer treeViewer = workitemView.getViewer();
        workitemView.getActionsManager().enableActions(false, false);
        if (treeViewer.isCellEditorActive()) {
            treeViewer.getTree().getShell().traverse(SWT.TRAVERSE_TAB_NEXT);
        }

        try {
            DATA.commitChanges();
            Activator.connect();
        } catch (ValidatorException e) {
            Activator.logWarning(e.getMessage());
            ErrorMessageDialog errorMessage = new ErrorMessageDialog(treeViewer.getTree().getShell(), e.getMessage());
            errorMessage.setBlockOnOpen(true);
            errorMessage.open();
        } catch (DataLayerException e) {
            Activator.logError(e);
            workitemView.showMessage("Error saving task. Check Error log for more information.");
        }

        final boolean loaded = workitemView.loadDataToTable();
        workitemView.getActionsManager().enableActions(loaded, true);
    }
}