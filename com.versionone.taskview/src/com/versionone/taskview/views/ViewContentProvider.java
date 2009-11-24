package com.versionone.taskview.views;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.SecondaryWorkitem;

/**
 * ContentProvider for VersionOne Workitems
 * 
 * @author jerry
 */
class ViewContentProvider implements ITreeContentProvider {

    public Object[] getChildren(Object parentElement) {
        return ((PrimaryWorkitem) parentElement).children.toArray();
    }

    public Object getParent(Object element) {
        if (element instanceof SecondaryWorkitem) {
            return ((SecondaryWorkitem) element).parent;
        }
        return null;
    }

    public boolean hasChildren(Object element) {
        if (element instanceof PrimaryWorkitem) {
            PrimaryWorkitem workitem = (PrimaryWorkitem) element;
            return workitem.children.size() > 0;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            return ((List) inputElement).toArray();
        } else {
            return new Object[] {};
        }

    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}