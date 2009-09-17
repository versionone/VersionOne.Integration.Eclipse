package com.versionone.taskview.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.taskview.Activator;

public class SaveAction extends Action {

    private static final ApiDataLayer DATA = ApiDataLayer.getInstance();

    private final TaskView workitemView;
    private final TreeViewer treeViewer;

    public SaveAction(TaskView workItemView, TreeViewer workitemViewer) {
        this.workitemView = workItemView;
        this.treeViewer = workitemViewer;

        setText("Save");
        setToolTipText("Save");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.SAVE_IMAGE_ID));
    }

    public void run() {
        workitemView.enableAction(false);
        if (treeViewer.isCellEditorActive()) {
            treeViewer.getTree().getShell().traverse(SWT.TRAVERSE_TAB_NEXT);
        }

        try {
            DATA.commitChanges();
            Activator.connect();
        } catch (Exception e) {
            Activator.logError(e);
            workitemView.showMessage("Error saving task. Check Error log for more information.");
        }

        if (workitemView.loadDataToTable())
            workitemView.enableAction(true);
    }
}