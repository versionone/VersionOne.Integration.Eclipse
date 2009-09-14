package com.versionone.taskview.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.versionone.common.sdk.Workitem;

/**
 * ContentProvider for VersionOne Task
 * 
 * @author jerry
 */
class ViewContentProvider implements ITreeContentProvider {   
    public Object[] getChildren(Object parentElement) {

        return ((Workitem) parentElement).children.toArray();
    }

    public Object getParent(Object element) {
        return ((Workitem) element).parent;
    }

    public boolean hasChildren(Object element) {
        Workitem workitem = ((Workitem) element);
        return workitem.children != null && workitem.children.size() > 0;
    }

    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Workitem[]) {
            return (Object[]) inputElement;
        } else {
            return new Object[] {};
        }

    }

    public void dispose() {}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}