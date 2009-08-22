package com.versionone.taskview.views.providers;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

public class SimpleProvider extends ColumnLabelProvider {

    private final String propertyName;
    private final boolean isShowTypeIcon;

    public SimpleProvider(String propertyName, boolean isShowTypeIcon) {
        this.propertyName = propertyName;
        this.isShowTypeIcon = isShowTypeIcon;
    }

    @Override
    public String getText(Object element) {
        try {
            return ((Workitem) element).getPropertyAsString(propertyName);
        } catch (IllegalArgumentException e) {
            Activator.logError("Cannot get property '" + propertyName + "' of " + element, e);
            return "*** Error ***";
        }
    }

    @Override
    public Image getImage(Object element) {
        Image icon = null;
        if (isShowTypeIcon) {
            String workItemType = ((Workitem) element).getTypePrefix();
            ImageRegistry imageStore = Activator.getDefault().getImageRegistry();
            if (workItemType.equals(Workitem.TASK_PREFIX)) {
                icon = imageStore.get(Activator.TASK_IMAGE_ID);
            } else if (workItemType.equals(Workitem.DEFECT_PREFIX)) {
                icon = imageStore.get(Activator.DEFECT_IMAGE_ID);
            } else if (workItemType.equals(Workitem.TEST_PREFIX)) {
                icon = imageStore.get(Activator.TEST_IMAGE_ID);
            } else if (workItemType.equals(Workitem.STORY_PREFIX)) {
                icon = imageStore.get(Activator.STORY_IMAGE_ID);
            }
        }
        return icon;
    }
}
