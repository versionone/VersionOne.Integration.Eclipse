package com.versionone.taskview.views;

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

public class SelectOwnersDialog extends Dialog implements SelectionListener {

    private List ownersList;
    private Workitem workitem;
    private TaskView openingViewer;
    
    private PropertyValues owners;
    private ApiDataLayer dataLayer = ApiDataLayer.getInstance();

    static int WINDOW_HEIGHT = 300;
    static int WINDOW_WIDTH = 400;

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
    public SelectOwnersDialog(Shell parentShell, Workitem workitem, TaskView viewer) {
        super(parentShell);
        this.workitem = workitem;
        this.openingViewer = viewer;
        setShellStyle(this.getShellStyle() | SWT.RESIZE);
        
        owners = dataLayer.getListPropertyValues(workitem.getTypePrefix(), Workitem.OWNERS_PROPERTY);
    }

    /**
     * {@link #createDialogArea(Composite)}
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout());
        
        ownersList = new List(container, SWT.NONE);
        fillOwnersList();
        ownersList.addSelectionListener(new SelectionListener() {
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
    private void fillOwnersList() {
    	// TODO refactor this ugly stuff.
    	PropertyValues currentOwners = (PropertyValues)workitem.getProperty(Workitem.OWNERS_PROPERTY);
    	int[] selectedIndexes = new int[currentOwners.size()];
    	int i = 0;
    	int currentIndex = 0;
    	for(String owner : owners.toStringArray()) {
    		ownersList.add(owner);
    		if(currentOwners.contains(owners.getValueIdByIndex(i))) {
    			selectedIndexes[currentIndex] = i;
    			currentIndex++;
    		}
    		i++;
    	}
    	
    	ownersList.select(selectedIndexes);
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
        	// TODO
        	workitem.setProperty(Workitem.OWNERS_PROPERTY, null);
		} catch (Exception e) {
			Activator.logError("Failed to set owners", e);
		}
    }
}
