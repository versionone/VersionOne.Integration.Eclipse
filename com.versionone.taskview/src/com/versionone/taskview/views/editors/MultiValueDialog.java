package com.versionone.taskview.views.editors;

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
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.PlatformUI;

import com.versionone.taskview.Activator;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.Workitem;

public class MultiValueDialog extends Dialog implements SelectionListener {

    private static int WINDOW_HEIGHT = 300;
    private static int WINDOW_WIDTH = 400;

    private List list;
    private PropertyValues values;

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
    public MultiValueDialog(Shell parentShell, PropertyValues values) {
        super(parentShell);
        this.values = values;
        // this.openingViewer = viewer;
        setShellStyle(this.getShellStyle() | SWT.RESIZE);

    }

    /**
     * {@link #createDialogArea(Composite)}
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout());

        list = new List(container, SWT.MULTI | SWT.V_SCROLL);
        fillList();
        list.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO
            }

            public void widgetSelected(SelectionEvent e) {
                // TODO
            }
        });

        return container;
    }

    /*
     * Fill Owners list.
     */
    private void fillList() {
        int[] selectedIndexes = new int[values.size()];
        int i = 0;
        int currentIndex = 0;
        for (String owner : values.toStringArray()) {
            list.add(owner);
            if (values.contains(values.getValueIdByIndex(i))) {
                selectedIndexes[currentIndex] = i;
                currentIndex++;
            }
            i++;
        }

        list.select(selectedIndexes);
    }

    /**
     * {@link #configureShell(Shell)}
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Select owners");
        Display display = PlatformUI.getWorkbench().getDisplay();
        Point size = newShell.computeSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        Rectangle screen = display.getMonitors()[0].getBounds();
        newShell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
        newShell.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
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
        // TODO
    }

    public void setValue(Object value) {
        // TODO Auto-generated method stub

    }

    public Object getValue() {
        // TODO Auto-generated method stub
        return null;
    }
}
