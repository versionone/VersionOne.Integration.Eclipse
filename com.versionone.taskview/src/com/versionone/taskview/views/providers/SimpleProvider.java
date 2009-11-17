package com.versionone.taskview.views.providers;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.versionone.common.sdk.Workitem;
import com.versionone.common.sdk.WorkitemType;
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
            WorkitemType workitemType = ((Workitem) element).getType();
            ImageRegistry imageStore = Activator.getDefault().getImageRegistry();
            if (workitemType.isWorkitem()) {
                icon = imageStore.get(Activator.WORKITEM_IMAGE_PREFIX + workitemType);
            }
        }
        return icon;
    }
}
