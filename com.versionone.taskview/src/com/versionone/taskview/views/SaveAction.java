package com.versionone.taskview.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.taskview.Activator;

public class SaveAction extends Action {

    private TaskView workItemView;
    private TreeViewer workItemViewer;

    public SaveAction(TaskView workItemView, TreeViewer workitemViewer) {
        this.workItemView = workItemView;
        this.workItemViewer = workitemViewer;

        setText("Save");
        setToolTipText("Save");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.SAVE_IMAGE_ID));
    }

    public void run() {
        if (workItemViewer.isCellEditorActive()) {
            workItemViewer.getTree().getShell().traverse(SWT.TRAVERSE_TAB_NEXT);
        }
        try {
            ApiDataLayer.getInstance().commitChanges();
        } catch (DataLayerException e) {
            Activator.logError(e);
            workItemView.showMessage("Error saving task. Check Error log for more information.");
        }
        workItemView.loadTable();
    }
}
