package com.versionone.taskview.views;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;

import com.versionone.common.sdk.Task;
import com.versionone.common.sdk.V1Server;
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
        TreeItem[] rows = workItemViewer.getTree().getItems();
        ArrayList<Task> saveUs = new ArrayList<Task>();
        for (int i = 0; i < rows.length; ++i) {
            if (((Task) rows[i].getData()).isDirty()) {
                saveUs.add((Task) rows[i].getData());
            }
        }
        if (0 != saveUs.size()) {
            try {
                V1Server.getInstance().save(saveUs);
            } catch (Exception e) {
                Activator.logError(e);
                workItemView.showMessage("Error saving task. Check Error log for more information.");
            }
            workItemView.loadTable();
            workItemView.updateStatusCodes();
        }
    }
}
