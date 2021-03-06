package com.versionone.taskview.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.Entity;
import com.versionone.taskview.views.properties.WorkitemPropertySource;

public class ProxySelectionProvider implements ISelectionProvider {

    private final ISelectionProvider proxy;
    private final List<ISelectionChangedListener> listeners;

    public ProxySelectionProvider(ISelectionProvider proxy) {
        this.proxy = proxy;
        listeners = new ArrayList<ISelectionChangedListener>();
    }

    public ProxySelectionProvider(TreeViewer treeViewer, List<ISelectionChangedListener> listeners) {
        this(treeViewer);
        for (ISelectionChangedListener listener : listeners) {
            addSelectionChangedListener(listener);
        }
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        proxy.addSelectionChangedListener(new ProxyListener(listener));
        listeners.add(listener);
    }

    public List<ISelectionChangedListener> getListeners() {
        return listeners;
    }

    public ISelection getSelection() {
        return wrap(proxy.getSelection());
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        // TODO proxy.removeListener(listenersMap.remove(listener));
    }

    public void setSelection(ISelection selection) {
        proxy.setSelection(wrap(selection));
    }

    /**
     * Wraps and unwraps Workitem to WorkitemPropertySource
     */
    private ISelection wrap(ISelection sel) {
        if (sel instanceof IStructuredSelection) {
            IStructuredSelection sSel = (IStructuredSelection) sel;
            final Object element = sSel.getFirstElement();
            Object res = null;
            if (element instanceof Entity) {
                res = new WorkitemPropertySource((Entity) element, proxy);
            } else if (element instanceof WorkitemPropertySource) {
                res = ((WorkitemPropertySource) element).getItem();
            }
            if (res != null) {
                return new StructuredSelection(res);
            }
        }
        return sel;
    }

    private class ProxyListener implements ISelectionChangedListener {

        private final ISelectionChangedListener listener;

        public ProxyListener(ISelectionChangedListener listener) {
            this.listener = listener;
        }

        public void selectionChanged(SelectionChangedEvent event) {
            listener.selectionChanged(new SelectionChangedEvent(ProxySelectionProvider.this, wrap(event.getSelection())));
        }
    }
}
