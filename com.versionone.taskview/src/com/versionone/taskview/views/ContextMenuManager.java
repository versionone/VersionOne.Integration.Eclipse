package com.versionone.taskview.views;

import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.versionone.common.sdk.DataLayerException;
import com.versionone.common.sdk.Workitem;
import com.versionone.taskview.Activator;

public class ContextMenuManager {

    private final Shell shell;
    private final Menu menu;
    private final TaskView view;
    
    private static final String MENU_ITEM_CLOSE_KEY = "Close";
    private static final String MENU_ITEM_QUICK_CLOSE_KEY = "Quick Close";
    private static final String MENU_ITEM_SIGNUP_KEY = "Signup";
    private static final String MENU_ITEM_ADD_TASK_KEY = "Add new task";
    
    public ContextMenuManager(Shell shell, Menu menu, TaskView taskView) {
        this.shell = shell;
        this.menu = menu;
        this.view = taskView;
    }

    private MenuItem getClose() {
        final MenuItem closeItem = new MenuItem(menu, SWT.PUSH);
        closeItem.setText(MENU_ITEM_CLOSE_KEY);
        closeItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                CloseWorkitemDialog closeDialog = new CloseWorkitemDialog(shell, view.getCurrentWorkitem(), view);
                closeDialog.setBlockOnOpen(true);
                closeDialog.open();
            }
        });
        
        return closeItem;
    }
    
    private MenuItem getQuickClose() {
        final MenuItem quickCloseItem = new MenuItem(menu, SWT.PUSH);
        quickCloseItem.setText(MENU_ITEM_QUICK_CLOSE_KEY);
        quickCloseItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                try {
                    Workitem item = view.getCurrentWorkitem();
                    if (item != null) {
                        item.quickClose();                        
                        view.refreshViewer();
                    }
                } catch (DataLayerException ex) {
                    Activator.logError(ex);
                    MessageDialog.openError(shell, "Task View Error",
                            "Error during closing Workitem. Check Error Log for more details.");
                }
            }
        });
        
        return quickCloseItem;
    }

    
    private MenuItem getSignUp() {
        final MenuItem signupItem = new MenuItem(menu, SWT.PUSH);
        signupItem.setText(MENU_ITEM_SIGNUP_KEY);
        signupItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                try {
                    Workitem item = view.getCurrentWorkitem();
                    if (item != null) {
                        item.signup();
                        view.refreshViewer();
                    }
                } catch (DataLayerException ex) {
                    Activator.logError(ex);
                    MessageDialog.openError(shell, "Task View Error",
                            "Error during signing up. Check Error Log for more details.");
                }
            }
        });
        
        return signupItem;
    }   

    private MenuItem getAddNewTask() {
        final MenuItem addTaskItem = new MenuItem(menu, SWT.PUSH);
        addTaskItem.setText(MENU_ITEM_ADD_TASK_KEY);
        addTaskItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                Workitem item = view.getCurrentWorkitem();        
                item = item.parent != null ? item.parent : item;        
                Workitem newItem = item.createChild(Workitem.TASK_PREFIX);                
                view.getViewer().refresh();
                view.getViewer().setSelection(new StructuredSelection(newItem), true);
            }
        });
        
        return addTaskItem;
    }

    
    private HashMap<String, MenuItem> menuItemsMap = new HashMap<String, MenuItem>();
    public void init (Control control) {
        menuItemsMap.put(MENU_ITEM_CLOSE_KEY, getClose());        
        final MenuItem quickCloseItem = getQuickClose();
        menuItemsMap.put(MENU_ITEM_QUICK_CLOSE_KEY, quickCloseItem);
        new MenuItem(menu, SWT.SEPARATOR);
        final MenuItem signUpItem = getSignUp();
        menuItemsMap.put(MENU_ITEM_SIGNUP_KEY, signUpItem);        
        new MenuItem(menu, SWT.SEPARATOR);
        menuItemsMap.put(MENU_ITEM_ADD_TASK_KEY, getAddNewTask());
        
        menu.addMenuListener(new MenuListener() {

            public void menuHidden(MenuEvent e) {
            }

            public void menuShown(MenuEvent e) {
                Workitem item = view.getCurrentWorkitem();
                if (menu.getVisible() && (item == null || !view.validRowSelected())) {
                    menu.setVisible(false);
                } else {
                    quickCloseItem.setEnabled(item.canQuickClose());
                    signUpItem.setEnabled(item.canSignup() && !item.isMine());
                }
                
            }
        });
        
        control.setMenu(menu);
    }
}
