package com.versionone.taskview.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.views.properties.WorkitemPropertySource;

public class ProxySelectionProvider implements ISelectionProvider {

    private final ISelectionProvider proxy;

    public ProxySelectionProvider(ISelectionProvider proxy) {
        this.proxy = proxy;
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        proxy.addSelectionChangedListener(new ProxyListener(listener));
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
            if (element instanceof Workitem)
                res = new WorkitemPropertySource((Workitem) element);
            else if (element instanceof WorkitemPropertySource)
                res = ((WorkitemPropertySource) element).getItem();
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
            listener
                    .selectionChanged(new SelectionChangedEvent(ProxySelectionProvider.this, wrap(event.getSelection())));
        }
    }
}
