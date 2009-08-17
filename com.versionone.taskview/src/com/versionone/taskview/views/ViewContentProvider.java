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

    Object justForTest;

    public Object[] getChildren(Object parentElement) {
        // TODO Auto-generated method stub
        return ((Workitem) parentElement).children.toArray();
    }

    public Object getParent(Object element) {
        // TODO Auto-generated method stub
        return ((Workitem) element).parent;
    }

    public boolean hasChildren(Object element) {
        // TODO Auto-generated method stub
        Workitem workitem = ((Workitem) element);
        return workitem.children != null && workitem.children.size() > 0;
    }

    public Object[] getElements(Object inputElement) {
        // justForTest = inputElement;
        // return inputElement;
        if (inputElement instanceof Workitem[]) {
            return (Object[]) inputElement;
        } else {
            return new Object[] {};
        }

    }

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO Auto-generated method stub
    	if(newInput != null) {
    		// add this as a listener
    	}
    	
    	if(oldInput != null) {
    		// unsubscribe this
    	}
    }

    /*
     * public void inputChanged(Viewer v, Object oldInput, Object newInput)
     * {} public void dispose() {} public Object[] getElements(Object
     * parent) { if(parent instanceof Task[]) { return (Object[]) parent; }
     * else { return new Object[]{}; } }
     */
}