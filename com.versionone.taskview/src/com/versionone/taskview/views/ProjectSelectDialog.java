package com.versionone.taskview.views;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Entity;
import com.versionone.common.sdk.Project;
import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.taskview.views.providers.SimpleProvider;

/**
 * Dialog box used to select projects. Pressing Okay updates the PreferenceStore
 * with the selected item.
 * 
 * @author Jerry D. Odenwelder Jr.
 * 
 */
public class ProjectSelectDialog extends Dialog {

    private final List<Project> v1Roots;
    private final Project selectedProjectTreeNode;
    private TreeViewer viewer = null;

    static int WINDOW_HEIGHT = 200;
    static int WINDOW_WIDTH = 200;

    /**
     * Create
     * 
     * @param parentShell
     * @param projectTree
     *            root node of tree to display
     * @param defaultSelected
     *            node of project to select by default, if null, the root is
     *            selected
     * @see org.eclipse.jface.dialogs.Dialog
     */
    public ProjectSelectDialog(Shell parentShell, List<Project> projectTree, Project defaultSelected) {
        super(parentShell);
        setShellStyle(this.getShellStyle() | SWT.RESIZE);
        v1Roots = projectTree;
        selectedProjectTreeNode = defaultSelected == null ? v1Roots.get(0) : defaultSelected;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout(SWT.VERTICAL));
        viewer = new TreeViewer(container, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        viewer.setContentProvider(new EntityContentProvider());
        viewer.setInput(v1Roots);
        viewer.setLabelProvider(new SimpleProvider(Entity.NAME_PROPERTY, false));

        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Project Selection");
        Display display = PlatformUI.getWorkbench().getDisplay();
        Point size = newShell.computeSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        Rectangle screen = display.getMonitors()[0].getBounds();
        newShell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
        newShell.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    protected void okPressed() {
        final ISelection selection = viewer.getSelection();
        if (selection instanceof StructuredSelection) {
            final Project selectedItem = (Project) ((IStructuredSelection) selection).getFirstElement();
            super.okPressed();
            PreferencePage.getPreferences().setValue(PreferenceConstants.P_PROJECT_TOKEN, selectedItem.getId());
            ApiDataLayer.getInstance().setCurrentProject(selectedItem);
        }
    }

    public TreeViewer getTreeViewer() {
        return viewer;
    }

    public void setCurrentProject() {
        viewer.expandAll();
        viewer.setSelection(new StructuredSelection(selectedProjectTreeNode), true);
    }
}
