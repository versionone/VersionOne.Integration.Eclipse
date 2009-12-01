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

    private final TaskView taskView;
    // Actions list
    private final Action selectProjectAction;
    private final Action refreshAction;
    private final Action saveAction;
    private final Action filterAction;
    private final Action addDefect;
    private final Action addTask;
    private final Action addTest;
    // Actions only for context menu
    private final Action close;
    private final Action quickClose;
    private final Action signUp;

    private Workitem selectedWorkitem;

    public ActionsManager(TaskView taskView, IWorkbenchPartSite site) {
        this.taskView = taskView;
        selectProjectAction = new ProjectAction(taskView, site);
        refreshAction = new RefreshAction(taskView);
        saveAction = new SaveAction(taskView);
        filterAction = new FilterAction(taskView);
        addTask = new AddTaskAction(taskView);
        addTest = new AddTestAction(taskView);
        addDefect = new AddDefectAction(taskView);
        close = new CloseAction(taskView);
        quickClose = new QuickCloseAction(taskView);
        signUp = new SignUpAction(taskView);
    }

    public void createToolbar(IContributionManager manager) {
        manager.add(addDefect);
        manager.add(addTask);
        manager.add(addTest);
        manager.add(new Separator());
        manager.add(filterAction);
        manager.add(selectProjectAction);
        manager.add(new Separator());
        manager.add(refreshAction);
        manager.add(saveAction);
    }

    private void createContextMenu(IMenuManager manager) {
        manager.add(close);
        manager.add(quickClose);
        manager.add(new Separator());
        manager.add(signUp);
        manager.add(new Separator());
        manager.add(addDefect);
        manager.add(addTask);
        manager.add(addTest);
    }

    /**
     * 
     * @param enabled
     *            status for actions except refresh
     * @param refreshEnable
     *            status for refresh
     */
    public void enableActions(boolean enabled, boolean refreshEnable) {
        refreshAction.setEnabled(refreshEnable);

        selectProjectAction.setEnabled(enabled);
        saveAction.setEnabled(enabled);
        filterAction.setEnabled(enabled);

        addDefect.setEnabled(enabled);
        addTask.setEnabled(enabled && selectedWorkitem != null);
        addTest.setEnabled(enabled && selectedWorkitem != null);
    }

    public void selectionChanged(SelectionChangedEvent event) {
        final ISelection selection = event.getSelection();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            selectedWorkitem = (Workitem) structuredSelection.getFirstElement();
        } else {
            selectedWorkitem = null;
        }
        onSelectedWorkitemChange();
    }

    public void menuAboutToShow(IMenuManager manager) {
        final Workitem item = taskView.getCurrentWorkitem();
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
        onSelectedWorkitemChange();
    }

    private void onSelectedWorkitemChange() {
        addDefect.setEnabled(true);
        addTask.setEnabled(selectedWorkitem != null);
        addTest.setEnabled(selectedWorkitem != null);
    }
}
