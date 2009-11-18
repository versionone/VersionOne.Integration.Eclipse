package com.versionone.taskview.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import com.versionone.taskview.Activator;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.PropertyValues;
import com.versionone.common.sdk.ValidatorException;
import com.versionone.common.sdk.ValueId;
import com.versionone.common.sdk.Workitem;

public class CloseWorkitemDialog extends Dialog implements SelectionListener {

    private Combo statusCombobox;
    private Label toDoLabel;
    private Label statusLabel;
    private Text toDoText;
    private Workitem workitem;
    private TaskView openingViewer;
    
    private PropertyValues statuses;
    private ApiDataLayer dataLayer = ApiDataLayer.getInstance();
    
    private int selectedStatusIndex = -1;

    static int WINDOW_HEIGHT = 110;
    static int WINDOW_WIDTH = 350;

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
    public CloseWorkitemDialog(Shell parentShell, Workitem workitem, TaskView viewer) {
        super(parentShell);
        this.workitem = workitem;
        this.openingViewer = viewer;
        setShellStyle(this.getShellStyle() | SWT.RESIZE);
        
        statuses = dataLayer.getListPropertyValues(workitem.getType(), Workitem.STATUS_PROPERTY);
    }

    /**
     * {@link #createDialogArea(Composite)}
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 15;
        container.setLayout(layout);
        
        toDoLabel = new Label(container, SWT.NONE);
        toDoLabel.setText("To Do");
        toDoLabel.setSize(40, 30);
        
        toDoText = new Text(container, SWT.BORDER);
        toDoText.setSize(40, 30);
        toDoText.setEditable(false);
        toDoText.setText(workitem.getPropertyAsString(Workitem.TODO_PROPERTY));
        
        statusLabel = new Label(container, SWT.NONE);
        statusLabel.setText("Status");
        statusLabel.setSize(40, 30);
        
        statusCombobox = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        statusCombobox.setSize(200, 40);
        fillStatusCombobox();
        statusCombobox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				selectedStatusIndex = ((Combo)e.widget).getSelectionIndex();
			}

			public void widgetSelected(SelectionEvent e) {
				selectedStatusIndex = ((Combo)e.widget).getSelectionIndex();
			}
        });
        
        return container;
    }
    
    /*
     * Fill Status combobox.
     */
    private void fillStatusCombobox() {
    	String[] values = statuses.toStringArray();
    	for(String value : values) {
    		statusCombobox.add(value);
    	}
    	ValueId selectedValue = (ValueId)workitem.getProperty(Workitem.STATUS_PROPERTY);
    	statusCombobox.select(statuses.getStringArrayIndex(selectedValue));
    }

    /**
     * {@link #configureShell(Shell)}
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Close " + workitem.getType());
        Display display = PlatformUI.getWorkbench().getDisplay();
        Point size = newShell.computeSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        Rectangle screen = display.getMonitors()[0].getBounds();
        newShell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
        newShell.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    /**
     * {@link #widgetDefaultSelected(SelectionEvent)}
     */
    public void widgetDefaultSelected(SelectionEvent e) { }

    /**
     * {@link #widgetSelected(SelectionEvent)}
     */
    public void widgetSelected(SelectionEvent e) {
        // TODO
    }

    /**
     * {@link #okPressed()}
     */
    @Override
    protected void okPressed() {
        super.okPressed();
        try {
            if (selectedStatusIndex >= 0) {
                ValueId selectedStatus = statuses.getValueIdByIndex(selectedStatusIndex);
                workitem.setProperty(Workitem.STATUS_PROPERTY, selectedStatus);
                workitem.commitChanges();
            } else {
                workitem.validateRequiredFields();
            }
            workitem.close();
            if (openingViewer != null) {
                openingViewer.refreshViewer(null);
            }
        } catch (ValidatorException ex) {
            Activator.logWarning("Workitem cannot be closed because some required fields are empty:" + ex.getMessage());
            openingViewer.showMessage("Workitem cannot be closed because some required fields are empty:" + ex.getMessage());
            if (openingViewer != null) {
                openingViewer.refreshViewer(null);
            }
        } catch (DataLayerException e) {
            Activator.logError("Failed to close workitem", e);
        }
        
    }
}
