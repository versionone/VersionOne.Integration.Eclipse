package com.versionone.taskview.views.providers;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

public class SimplePlovider extends ColumnLabelProvider {

	private String propertyName;
	private boolean isShowTypeIcon;
	
	public SimplePlovider(String propertyName, boolean isShowTypeIcon) {
		this.propertyName = propertyName;
		this.isShowTypeIcon = isShowTypeIcon;
		
	}
	
	@Override
	public String getText(Object element) {
		try {
			if (propertyName.equals(Workitem.IdProperty)) {
				return ((Workitem)element).getId();
			} else {
				return ((Workitem)element).getProperty(propertyName).toString();
			}
		} catch (Exception e) {
			Activator.logError(e);
		}
		return "*** Error ***";
	}
	
	@Override
	public Image getImage(Object element) {
		Image icon = null;
		String workItemType = ((Workitem)element).getTypePrefix();
		ImageRegistry imageStore = Activator.getDefault().getImageRegistry();
		if (!isShowTypeIcon) {
			icon = super.getImage(element);
		} else if (workItemType.equals(Workitem.TaskPrefix)) {
			icon = imageStore.get(Activator.TASK_IMAGE_ID);
		} else if (workItemType.equals(Workitem.DefectPrefix)) {
			icon = imageStore.get(Activator.DEFECT_IMAGE_ID);
		} else if (workItemType.equals(Workitem.TestPrefix)) {
			icon = imageStore.get(Activator.TEST_IMAGE_ID);
		} else if (workItemType.equals(Workitem.StoryPrefix)) {
			icon = imageStore.get(Activator.STORY_IMAGE_ID);
		}
		
		return icon;
	}
}
