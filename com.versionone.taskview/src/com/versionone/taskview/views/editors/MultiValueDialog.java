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

public class MultiValueDialog extends Dialog {

    private static int WINDOW_HEIGHT = 170;
    private static int WINDOW_WIDTH = 183;

    private List list;
    private String[] allValues;
    private int[] selectedIndices;

    /**
     * Create
     * 
     * @param parentShell
     *            - @see Dialog
     * @param allValues
     *            - all available values.
     */
    public MultiValueDialog(Shell parentShell, String[] allValues) {
        super(parentShell);
        this.allValues = allValues;
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
        for (String owner : allValues) {
            list.add(owner);
        }
        list.select(selectedIndices);
    }

    /**
     * Sets name, size and places at a center of a screen.
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Select owners");
        newShell.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        try {
            Display display = PlatformUI.getWorkbench().getDisplay();
            Point size = newShell.computeSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            Rectangle screen = display.getMonitors()[0].getBounds();
            newShell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
        } catch (Exception e) {
            // Do nothing
        }
    }

    public void setSelectedIndices(int[] value) {
        selectedIndices = value;
    }

    public int[] getSelectedIndices() {
        return selectedIndices;
    }
}
