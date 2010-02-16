package com.versionone.taskview.views;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.versionone.common.sdk.PrimaryWorkitem;
import com.versionone.common.sdk.Project;
import com.versionone.common.sdk.SecondaryWorkitem;

/**
 * ContentProvider for VersionOne Entities.
 * Accepts List of Projects and Workitems as Input.
 * 
 * @author jerry
 */
class EntityContentProvider implements ITreeContentProvider {

    public Object[] getChildren(Object parent) {
        if (parent instanceof PrimaryWorkitem) {
            return ((PrimaryWorkitem) parent).children.toArray();
        } else if (parent instanceof Project) {
            return ((Project) parent).children.toArray();
        } 
        return new Object[0];
    }

    public Object getParent(Object element) {
        if (element instanceof SecondaryWorkitem) {
            return ((SecondaryWorkitem) element).parent;
        } else if (element instanceof Project) {
            return ((Project) element).parent;
        } 
        return null;
    }

    public boolean hasChildren(Object element) {
        if (element instanceof PrimaryWorkitem) {
            return ((PrimaryWorkitem) element).children.size() > 0;
        } else if (element instanceof Project) {
            return ((Project)element).children.size() > 0;
        }
        return false;
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