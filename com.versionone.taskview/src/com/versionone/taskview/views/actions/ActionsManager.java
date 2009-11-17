package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPartSite;

import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.views.TaskView;

public class ActionsManager implements ISelectionChangedListener, IMenuListener {
    //Actions list
    private Action selectProjectAction = null;
    private Action refreshAction = null;
    private Action saveAction = null;
    private Action filterAction = null;
    private Action addTask = null;
    // actins only for context menu
    private Action close = null;
    private Action quickClose = null;
    private Action signUp = null;
    
    private boolean isTaskCanBeAdded = false;
    private TaskView taskView;
    
    public void addActions(IContributionManager manager) {
        manager.add(addTask);
        manager.add(new Separator());
        manager.add(filterAction);
        manager.add(selectProjectAction);
        manager.add(new Separator());
        manager.add(refreshAction);
        manager.add(saveAction);
    }
    

    public void init(TaskView taskView, IWorkbenchPartSite site) {
        this.selectProjectAction = new ProjectAction(taskView, site);
        this.refreshAction = new RefreshAction(taskView);
        this.saveAction = new SaveAction(taskView);
        this.filterAction = new FilterAction(taskView);
        this.addTask = new AddTaskAction(taskView);
        this.close = new CloseAction(taskView);
        this.quickClose = new QuickCloseAction(taskView);
        this.signUp = new SignUpAction(taskView);
        this.taskView = taskView;
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
        
        updateAdditionButtons();
    }
    
    
    public void menuAboutToShow(IMenuManager manager) {
        Workitem item = taskView.getCurrentWorkitem();
        if (item != null && taskView.validRowSelected()) {
            createContextMenu(manager);
            
            quickClose.setEnabled(item.canQuickClose());
            signUp.setEnabled(item.canSignup() && !item.isMine());
            close.setEnabled(item.isPersistent());
        }
    }
     
    
    private void updateAdditionButtons() {
        enableAddTask(canAddTask());        
    }
    
    private boolean canAddTask() {
        return isTaskCanBeAdded;
    }


    private void createContextMenu(IMenuManager menuManager) {        
        menuManager.add(close);
        menuManager.add(quickClose);
        menuManager.add(new Separator());
        menuManager.add(signUp);
        menuManager.add(new Separator());
        menuManager.add(addTask);
    }
}
