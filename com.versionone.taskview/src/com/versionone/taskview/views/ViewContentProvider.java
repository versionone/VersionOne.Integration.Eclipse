package com.versionone.taskview.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.versionone.common.sdk.Entity;

/**
 * ContentProvider for VersionOne Task
 * 
 * @author jerry
 */
class ViewContentProvider implements ITreeContentProvider {   
    public Object[] getChildren(Object parentElement) {

        return ((Entity) parentElement).children.toArray();
    }

    public Object getParent(Object element) {
        return ((Entity) element).parent;
    }

    public boolean hasChildren(Object element) {
        Entity workitem = ((Entity) element);
        return workitem.children != null && workitem.children.size() > 0;
    }

    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Entity[]) {
            return (Object[]) inputElement;
        } else {
            return new Object[] {};
        }

    }

    public void dispose() {}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}