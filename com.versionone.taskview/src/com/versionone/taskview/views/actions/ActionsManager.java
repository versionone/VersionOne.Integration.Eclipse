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
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.views.TaskView;

public class ActionsManager implements ISelectionChangedListener, IMenuListener {

    // Actions list
    private final Action selectProjectAction;
    private final Action refreshAction;
    private final Action saveAction;
    private final Action filterAction;
    private final Action addTask;
    private final Action addDefect;
    // Actions only for context menu
    private final Action close;
    private final Action quickClose;
    private final Action signUp;

    private boolean isTaskCanBeAdded = false;
    private final TaskView taskView;

    public ActionsManager(TaskView taskView, IWorkbenchPartSite site) {
        this.taskView = taskView;
        selectProjectAction = new ProjectAction(taskView, site);
        refreshAction = new RefreshAction(taskView);
        saveAction = new SaveAction(taskView);
        filterAction = new FilterAction(taskView);
        addTask = new AddTaskAction(taskView);
        addDefect = new AddDefectAction(taskView);
        close = new CloseAction(taskView);
        quickClose = new QuickCloseAction(taskView);
        signUp = new SignUpAction(taskView);
    }

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

    /**
     * 
     * @param enabled
     *            status for actions except refresh
     * @param refreshEnable
     *            status for refresh
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
        Workitem item = taskView.getCurrentWorkitem();
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
