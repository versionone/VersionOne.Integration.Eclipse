package com.versionone.taskview.views.htmleditor;

import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.versionone.common.sdk.Workitem;

import de.spiritlink.richhtml4eclipse.widgets.AllActionConstants;
import de.spiritlink.richhtml4eclipse.widgets.ComposerStatus;
import de.spiritlink.richhtml4eclipse.widgets.EventConstants;
import de.spiritlink.richhtml4eclipse.widgets.HtmlComposer;
import de.spiritlink.richhtml4eclipse.widgets.JavaScriptCommands;
import de.spiritlink.richhtml4eclipse.widgets.PropertyConstants;

public class HTMLEditor extends Dialog {

    static int WINDOW_HEIGHT = 800;
    static int WINDOW_WIDTH = 500;
    private static Workitem workitem;

    public HTMLEditor(Shell parentShell, Workitem workitem) {
        super(parentShell);

        this.workitem = workitem;
        setShellStyle(this.getShellStyle() | SWT.RESIZE);
        //getButton(IDialogConstants.OK_ID).
        // TODO Auto-generated constructor stub
    }

    /**
     * {@link #createDialogArea(Composite)}
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite container = (Composite) super.createDialogArea(parent);        
        container.setLayout(new GridLayout(1, true));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));//SWT.FILL, SWT.FILL

        //
        // composer.setVisible(true);
        CoolBar coolbar = new CoolBar(container, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);//SWT.FILL, SWT.BEGINNING
        //GridLayout gd = new GridLayout(1, true);//SWT.FILL, SWT.BEGINNING
        gd.widthHint = 100;
        coolbar.setLayoutData(gd);

        coolbar.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                //container.getShell().layout();
            }
        });
        
        ToolBar menu = new ToolBar(coolbar, SWT.HORIZONTAL | SWT.FLAT);//
        ToolBarManager manager = new ToolBarManager(menu);

        CoolItem item = new CoolItem(coolbar, SWT.VERTICAL);//SWT.VERTICAL
        item.setControl(menu);

        final HtmlComposer composer = new HtmlComposer(container, SWT.BORDER | SWT.SCROLL_LINE);
        composer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        composer.execute(JavaScriptCommands.SET_HTML(workitem.getPropertyAsString(Workitem.DescriptionProperty)));


        manager.add(new BoldAction(composer));
        // manager.add(new ItalicAction(composer));
        // manager.add(new UnderLineAction(composer));
        manager.update(true);

        return container;
    }
    

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * {@link #configureShell(Shell)}
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Description editor");
        Display display = PlatformUI.getWorkbench().getDisplay();
        Point size = newShell.computeSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        Rectangle screen = display.getMonitors()[0].getBounds();
        newShell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
        newShell.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    public class BoldAction extends Action implements Listener {

        private HtmlComposer composer = null;

        public BoldAction(HtmlComposer composer) {
            super("", IAction.AS_CHECK_BOX); //$NON-NLS-1$
            setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("de.spiritlink.richhtml4eclipse", //$NON-NLS-1$
                    "tiny_mce/jscripts/tiny_mce/themes/advanced/images/bold.gif")); //$NON-NLS-1$
            this.composer = composer;
            // adds a listener to the widget for "bold-events"
            this.composer.addListener(EventConstants.BOLD, this);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.action.Action#run()
         */
        @Override
        public void run() {
            // Executes the command for bold to the composer
            this.composer.execute(JavaScriptCommands.BOLD);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets
         * .Event)
         */
        public void handleEvent(Event event) {
            Properties props = (Properties) event.data;
            if (ComposerStatus.SELECTED.equals(props.getProperty(PropertyConstants.STATUS))) {
                // current selection/cursor is bold --> set the action checked
                setChecked(true);
            } else if (ComposerStatus.NORMAL.equals(props.getProperty(PropertyConstants.STATUS))) {
                setChecked(false);
            } else if (event.type == EventConstants.ALL
                    && AllActionConstants.RESET_ALL.equals(props.getProperty(PropertyConstants.COMMAND))) {
                // callback if the cursor changed, reset the state.
                setChecked(false);
            }
        }

    }
}
