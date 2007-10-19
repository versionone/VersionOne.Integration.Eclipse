package com.versionone.taskview.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


/**
 * FieldEditor used for the "validate" button 
 * @author Jerry D. Odenwelder Jr.
 *
 */
public class ButtonFieldEditor extends FieldEditor {

	/**
	 * Clients that want to use this control should pass an object that implements 
	 * this interface.
	 * @author Jerry D. Odenwelder Jr.
	 */
	public interface Validator {
		boolean isValid();
	}
	
	// the actual control
	private Button button = null;
	
	// the text on the control
	private String buttonText = null;
	
	/**
	 * I could not the "Button" to behave and appear
	 * as I wanted, so I added this boolean to 
	 * represent the value
	 * */ 
	boolean value = false;
	
	/**
	 * This is the validation logic needed by the client
	 */
	Validator validateMe = null;
	
	protected ButtonFieldEditor() {
	}
	
	public ButtonFieldEditor(String name, String textLabel, Validator validator, Composite parent) {
		buttonText = textLabel;
		validateMe = validator;
		init(name, textLabel);
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		((GridData) button.getLayoutData()).horizontalSpan = numColumns;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
        button = getChangeControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        button.setLayoutData(gd);
        button.setText(buttonText);
	}

	private Button getChangeControl(Composite parent) {
        if (button == null) {
        	button = new Button(parent, SWT.PUSH);
        	button.setFont(parent.getFont());
        	button.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                	if(null != validateMe) {
                    	if(validateMe.isValid()) {
                    		value = false;
                        	fireStateChanged(IS_VALID, false, true);
                        	fireValueChanged(VALUE, false, true);
                            button.setEnabled(false);                    		
                    	}
                	} else {
                		showErrorMessage("No Validator was supplied!\nThere's no way to validate");
                	}
                }
            });
        	button.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                	button = null;
                }
            });
        }
        else {
            checkParent(button, parent);
        }
        return button;
	}

	@Override
	protected void doLoad() {
		if(null != button) {
			value = getPreferenceStore().getBoolean(getPreferenceName());
			button.setEnabled(value);
		}
    }

	@Override
	protected void doLoadDefault() {
		if(null != button) {
			value = getPreferenceStore().getDefaultBoolean(getPreferenceName());
			button.setEnabled(value);
		}		
	}

	@Override
	protected void doStore() {
		if(null != button) {
			getPreferenceStore().setValue(getPreferenceName(), value);
		}
	}
	
	@Override
	public int getNumberOfControls() {
		return 1;
	}

	@Override
	public String getLabelText() {
		return null;
	}

	@Override
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		button.setEnabled(enabled);
		value = enabled;
	}

	public boolean getValue() {
		return value;
	}	
	
}
