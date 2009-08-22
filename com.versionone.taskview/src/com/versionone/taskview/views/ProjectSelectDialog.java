package com.versionone.taskview.views;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.Workitem;
import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;

/**
 * Dialog box used to select projects. Pressing Okay updates the PreferenceStore
 * with the selected item.
 * 
 * @author Jerry D. Odenwelder Jr.
 * 
 */
public class ProjectSelectDialog extends Dialog implements SelectionListener {

    private Tree projectTree = null;
    private List<Workitem> v1Roots;
    private Workitem selectedProjectTreeNode;
    private TreeItem _selectedTreeItem;

    static int WINDOW_HEIGHT = 200;
    static int WINDOW_WIDTH = 200;

    /**
     * Create
     * 
     * @param parentShell
     *            - @see Dialog
     * @param projectTree
     *            - root node of tree to display
     * @param defaultSelected
     *            - node of project to select by default, if null, the root is
     *            selected
     */
    public ProjectSelectDialog(Shell parentShell, List<Workitem> projectTree, Workitem defaultSelected) {
        super(parentShell);
        setShellStyle(this.getShellStyle() | SWT.RESIZE);
        v1Roots = projectTree;
        selectedProjectTreeNode = defaultSelected;
        /*if (null == _selectedProjectTreeNode)
            _selectedProjectTreeNode = _rootNode;*/
    }

    /**
     * Get the project selected by the user
     * 
     * @return
     */
    public Workitem getSelectedProject() {
        return selectedProjectTreeNode;
    }

    /**
     * {@link #createDialogArea(Composite)}
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout(SWT.VERTICAL));
        projectTree = new Tree(container, SWT.SINGLE);
        projectTree.addSelectionListener(this);
        populateTree();
        return container;
    }

    /**
     * {@link #configureShell(Shell)}
     */
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

    /**
     * Populate the Tree control with IProjectTreeNode data
     */
    // TODO this and next methods can be optimazed        
    private void populateTree() {
        int i = 0;
        for (Workitem v1Root : v1Roots) {
            final TreeItem treeItem = new TreeItem(this.projectTree, SWT.NONE, i);
            treeItem.setText(v1Root.getPropertyAsString(Workitem.NAME_PROPERTY));
            treeItem.setData(v1Root);
            treeItem.setExpanded(true);
            int j = 0;
            for (Workitem child : v1Root.children) {
                populateTree(treeItem, child, j);
                j++;
            }
            i++;
            //TreeNode node = rootNodes.Add(v1Root.GetProperty(Workitem.NameProperty) as string);
            //node.Tag = v1Root;
//            TreeNode res2 = AddNodesRecursively(node.Nodes, v1Root.Children);
//            if (res2 != null) {
//                res = res2;
//            } else if (v1Root.Id == Settings.Instance.SelectedProjectId) {
//                res = node;
//            }
        }

        if (null == _selectedTreeItem) {
            _selectedTreeItem = projectTree.getItem(0);
            selectedProjectTreeNode = (Workitem) _selectedTreeItem.getData();
        }
        projectTree.setSelection(_selectedTreeItem);
        
        /*
        if (projectTree1.hasChildren()) {
            IProjectTreeNode[] rootNodes = _rootNode.getChildren();
            for (int i = 0; i < rootNodes.length; ++i) {
                final TreeItem treeItem = new TreeItem(this.projectTree, SWT.NONE, i);
                treeItem.setText(rootNodes[i].getName());
                treeItem.setData(rootNodes[i]);
                treeItem.setExpanded(true);
                IProjectTreeNode[] children = rootNodes[i].getChildren();
                for (int childIndex = 0; childIndex < children.length; ++childIndex) {
                    populateTree(treeItem, children[childIndex], childIndex);
                }
            }
            if (null == _selectedTreeItem) {
                _selectedTreeItem = projectTree.getItem(0);
                _selectedProjectTreeNode = (IProjectTreeNode) _selectedTreeItem.getData();
            }
            projectTree.setSelection(_selectedTreeItem);
        }
        */
    }

    /**
     * Populate child tree nodes with IProjectTreeNode data
     * 
     * @param tree
     *            - parent node for children
     * @param node
     *            - IProjectTreeNode data for this node
     * @param index
     *            - index for this TreeItem in the parent node
     */
    private void populateTree(TreeItem tree, Workitem node, int index) {
        final TreeItem treeItem = new TreeItem(tree, SWT.NONE, index);
        if (selectedProjectTreeNode.equals(node)) {
            _selectedTreeItem = treeItem;
        }
        //treeItem.setText(node.getName());
        //treeItem.setData(node);
        treeItem.setText(node.getPropertyAsString(Workitem.NAME_PROPERTY));
        treeItem.setData(node);
        if (node.children.size() > 0) {
            //IProjectTreeNode[] children = node.children();
            int i = 0;
            for (Workitem child : node.children) {
                populateTree(treeItem, child, i);
                i++;
            }
        }
        treeItem.setExpanded(true);
    }

    /**
     * {@link #widgetDefaultSelected(SelectionEvent)}
     */
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    /**
     * {@link #widgetSelected(SelectionEvent)}
     */
    public void widgetSelected(SelectionEvent e) {
        this.selectedProjectTreeNode = (Workitem) ((TreeItem) e.item).getData();
    }

    /**
     * {@link #okPressed()}
     */
    @Override
    protected void okPressed() {
        super.okPressed();
        PreferencePage.getPreferences().setValue(PreferenceConstants.P_PROJECT_TOKEN,
                selectedProjectTreeNode.getId());
        ApiDataLayer.getInstance().setCurrentProject(selectedProjectTreeNode);
    }
}
