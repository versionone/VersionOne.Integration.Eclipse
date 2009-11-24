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

import com.versionone.common.sdk.Entity;
import com.versionone.taskview.views.TaskView;

public class ActionsManager implements ISelectionChangedListener, IMenuListener {
    //Actions list
    private Action selectProjectAction;
    private Action refreshAction;
    private Action saveAction;
    private Action filterAction;
    private Action addTask;
    private Action addDefect;
    // actins only for context menu
    private Action close;
    private Action quickClose;
    private Action signUp;
    
    private boolean isTaskCanBeAdded = false;
    private TaskView taskView;
    
    public void addActions(IContributionManager manager) {
        manager.add(addTask);
        manager.add(addDefect);
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
        this.addDefect = new AddDefectAction(taskView);
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
    public void enableActions(boolean enabled, boolean refreshEnable) {
        selectProjectAction.setEnabled(enabled);
        refreshAction.setEnabled(refreshEnable);
        saveAction.setEnabled(enabled);
        filterAction.setEnabled(enabled);        
        addTask.setEnabled(enabled && canAddTask());
        addDefect.setEnabled(enabled);
    }


    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        Entity element = null;
        
        if (selection != null && selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            element = (Entity) structuredSelection.getFirstElement();
        }
        
        isTaskCanBeAdded = element != null;        
        updateAdditionButtons();
    }
    
    
    public void menuAboutToShow(IMenuManager manager) {
        Entity item = taskView.getCurrentWorkitem();
        createContextMenu(manager);
        if (item != null && taskView.validRowSelected()) {
            quickClose.setEnabled(item.canQuickClose());
            signUp.setEnabled(item.canSignup() && !item.isMine());
            close.setEnabled(item.isPersistent());
        } else {
            quickClose.setEnabled(false);
            signUp.setEnabled(false);
            close.setEnabled(false);
        }
        updateAdditionButtons();
    }

    private void updateAdditionButtons() {
        addTask.setEnabled(canAddTask());
        addDefect.setEnabled(true);
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
        menuManager.add(addDefect);
    }
}
