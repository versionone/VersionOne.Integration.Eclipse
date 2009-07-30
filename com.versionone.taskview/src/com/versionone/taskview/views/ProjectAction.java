package com.versionone.taskview.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.Viewer;

import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.IProjectTreeNode;
import com.versionone.common.sdk.ProjectTreeNode;
import com.versionone.taskview.Activator;

class ProjectAction extends Action {
	private TaskView workItemView;
	private Viewer workItemViewer;
	
	public ProjectAction(TaskView workItemView, Viewer workitemViewer) {
		this.workItemView = workItemView;
		this.workItemViewer = workitemViewer;
		
		setText("Select Project");
		setToolTipText("Select Project");
		setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.FILTER_IMAGE_ID));
	}
	
	
	public void run() {
		IProjectTreeNode root = workItemView.getProjectTreeNode();
		IProjectTreeNode selectNode = null;
		String projectToken = PreferencePage.getPreferences().getString(PreferenceConstants.P_PROJECT_TOKEN); 
		if(!projectToken.equals("")) {
			selectNode = new ProjectTreeNode("", projectToken);
		}
		ProjectSelectDialog projectSelectDialog = new ProjectSelectDialog(workItemViewer.getControl().getShell(), root, selectNode);
		projectSelectDialog.create();
		projectSelectDialog.open();
		workItemView.loadTable();
	}
}
