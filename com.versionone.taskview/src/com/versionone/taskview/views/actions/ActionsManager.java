package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;

import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.views.TaskView;

public class ActionsManager implements ISelectionChangedListener{
    private Action selectProjectAction = null;
    private Action refreshAction = null;
    private Action saveAction = null;
    private Action filterAction = null;
    private Action addTask = null;
    private boolean isTaskCanBeAdded = false;
    
    public void addActions(IContributionManager manager) {
        manager.add(filterAction);
        manager.add(selectProjectAction);
        manager.add(refreshAction);
        manager.add(saveAction);
        manager.add(addTask);
    }
    

    public void init(TaskView taskView, TreeViewer viewer, IWorkbenchPartSite site) {
        selectProjectAction = new ProjectAction(taskView, site);
        refreshAction = new RefreshAction(taskView);
        saveAction = new SaveAction(taskView);
        filterAction = new FilterAction(taskView);
        addTask = new AddTaskAction(taskView);
    }
    
    /**
     * 
     * @param enabled status for actions except refresh
     * @param refreshEnable status for refresh
     */
    public void enableAction(boolean enabled, boolean refreshEnable) {
        selectProjectAction.setEnabled(enabled);
        refreshAction.setEnabled(refreshEnable);
        saveAction.setEnabled(enabled);
        filterAction.setEnabled(enabled);       
        enableAddTask(enabled && canAddTask());
    }
    
    public void enableAddTask(boolean enabled) {
        addTask.setEnabled(enabled);
    }


    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        Workitem element = null;
        
        if (selection != null && selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            element = (Workitem) structuredSelection.getFirstElement();
        }
        
        isTaskCanBeAdded = element != null;
        
        updateAdditionButton();
    }
    
    private void updateAdditionButton() {
        enableAddTask(canAddTask());        
    }
    
    private boolean canAddTask() {
        return isTaskCanBeAdded;
    }
}
