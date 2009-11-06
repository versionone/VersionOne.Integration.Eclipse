package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.TaskView;

class AddTaskAction extends Action {

    private final TaskView workitemView;
    private final TreeViewer treeViewer;

    public AddTaskAction(TaskView workItemView) {
        this.workitemView = workItemView;
        this.treeViewer = workItemView.getViewer();

        setText("Add new task");
        setToolTipText("Add new task");
        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ADD_TASK_ID));
    }
    
    @Override
    public void run() {
        Workitem item = workitemView.getCurrentWorkitem();        
        item = item.parent != null ? item.parent : item;        
        Workitem newItem = item.createChild(Workitem.TASK_PREFIX);
        
        treeViewer.refresh();
        treeViewer.setSelection(new StructuredSelection(newItem), true);

    }
}
