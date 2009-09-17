package com.versionone.taskview.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.taskview.Activator;

public class FilterAction extends Action {

    private final static String MESSAGE = "Show only my workitems";

    private final TaskView workitemView;
    private final TreeViewer treeViewer;

    public FilterAction(TaskView workItemView, TreeViewer workitemViewer) {
        super(MESSAGE, AS_CHECK_BOX);
        this.workitemView = workItemView;
        this.treeViewer = workitemViewer;

        setToolTipText(MESSAGE);
        boolean showAllTask = PreferencePage.getPreferences().getInt(PreferenceConstants.P_WORKITEM_FILTER_SELECTION) == 1 ? false
                : true;
        setChecked(!showAllTask);

        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.FILTER_WORKITEM_IMAGE_ID));
    }

    @Override
    public void run() {
        workitemView.enableTreeAndActions(false);

        int value = isChecked() ? 1 : 0;
        ApiDataLayer.getInstance().showAllTasks = !isChecked();
        PreferencePage.getPreferences().setValue(PreferenceConstants.P_WORKITEM_FILTER_SELECTION, value);

        workitemView.enableTreeAndActions(true);
    }
}