package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.taskview.Activator;
import com.versionone.taskview.views.TaskView;

class AddTaskAction extends Action {

    private final TaskView workitemView;
    private final TreeViewer treeViewer;

    public AddTaskAction(TaskView workItemView, TreeViewer workitemViewer) {
        this.workitemView = workItemView;
        this.treeViewer = workitemViewer;

        setText("Add new task");
        setToolTipText("Add new task");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ADD_TASK_ID));
    }
}
