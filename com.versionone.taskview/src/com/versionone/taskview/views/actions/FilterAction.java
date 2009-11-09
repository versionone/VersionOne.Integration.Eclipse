package com.versionone.taskview.views.actions;

import org.eclipse.jface.action.Action;

import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.taskview.Activator;
import com.versionone.taskview.views.TaskView;

class FilterAction extends Action {

    private final static String MESSAGE = "Show only my workitems";

    private final TaskView workitemView;

    public FilterAction(TaskView workItemView) {
        super(MESSAGE, AS_CHECK_BOX);
        this.workitemView = workItemView;

        setToolTipText(MESSAGE);
        boolean showAllTask = PreferencePage.getPreferences().getBoolean(PreferenceConstants.P_ONLY_USER_WORKITEMS);
        setChecked(!showAllTask);

        setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.FILTER_WORKITEM_IMAGE_ID));
    }

    @Override
    public void run() {
        workitemView.enableTreeAndActions(false);

        ApiDataLayer.getInstance().setShowAllTasks(!isChecked());
        PreferencePage.getPreferences().setValue(PreferenceConstants.P_ONLY_USER_WORKITEMS, !isChecked());

        workitemView.enableTreeAndActions(true);
    }
}