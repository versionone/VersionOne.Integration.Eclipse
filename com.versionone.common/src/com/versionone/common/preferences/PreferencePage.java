package com.versionone.common.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.versionone.apiclient.MetaModel;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1APIConnector;
import com.versionone.apiclient.V1Exception;
import com.versionone.common.Activator;
import com.versionone.common.preferences.ButtonFieldEditor.Validator;
import com.versionone.common.sdk.V1Server;

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor urlEditor;
	private StringFieldEditor userEditor;
	private StringFieldEditor pwdField;
	private BooleanFieldEditor effortEditor;
	private BooleanFieldEditor enabledEditor;
	private ButtonFieldEditor requiresValidation;
	private BooleanFieldEditor integratedAuthEditor;

	public static IPreferenceStore getPreferences() {
		return Activator.getDefault().getPreferenceStore();
	}
	
	/**
	 * Default Constructor
	 */
	public PreferencePage() {
		super(GRID);
		setPreferenceStore(PreferencePage.getPreferences());
		setDescription("Connection Preferences");
	}
	
	/**
	 * Initialize
	 * {@link #init(IWorkbench)}
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 * {@link #createFieldEditors()}
	 */
	@Override
	protected void createFieldEditors() {
		enabledEditor = new BooleanFieldEditor(PreferenceConstants.P_ENABLED, 
				"Enabled", 
				this.getFieldEditorParent());
		addField(enabledEditor);

		integratedAuthEditor = new BooleanFieldEditor(PreferenceConstants.P_INTEGRATED_AUTH,
				"Windows Integrated Authentication",
				this.getFieldEditorParent());
		addField(integratedAuthEditor);
		
		urlEditor = new StringFieldEditor(PreferenceConstants.P_URL,
						"Application URL:",
						this.getFieldEditorParent());
		addField(urlEditor);
		
		userEditor = new StringFieldEditor(PreferenceConstants.P_USER,
						"Username:",
						this.getFieldEditorParent());
		addField(userEditor);
		
		pwdField = new StringFieldEditor(PreferenceConstants.P_PASSWORD,
						"Password:",
						this.getFieldEditorParent());
		pwdField.getTextControl(this.getFieldEditorParent()).setEchoChar('*');
		addField(pwdField);
		
		effortEditor = new BooleanFieldEditor(PreferenceConstants.P_TRACK_EFFORT,
						"Effort Tracking",
						this.getFieldEditorParent());
		addField(effortEditor);
		
		requiresValidation = new ButtonFieldEditor(PreferenceConstants.P_REQUIRESVALIDATION,
				                                   "Validate Connection", 
				                                   new ConnectionValidator(),
				                                   this.getFieldEditorParent());
		addField(requiresValidation);
		
		setControlAccess(PreferencePage.getPreferences().getBoolean(PreferenceConstants.P_ENABLED));
	}

	/**
	 * Determines if fields are enabled
	 * @see org.eclipse.jface.preference.FieldEditor#setEnabled(boolean, org.eclipse.swt.widgets.Composite)
	 * @param value passed to setEnabled on field editors
	 */
	private void setControlAccess(boolean value) {
		urlEditor.setEnabled(value, this.getFieldEditorParent());
		if(getPreferences().getBoolean(PreferenceConstants.P_INTEGRATED_AUTH)) {
			userEditor.setEnabled(false, this.getFieldEditorParent());
			pwdField.setEnabled(false, this.getFieldEditorParent());
		} else {
			userEditor.setEnabled(value, this.getFieldEditorParent());
			pwdField.setEnabled(value, this.getFieldEditorParent());			
		}
		effortEditor.setEnabled(value, this.getFieldEditorParent());
		requiresValidation.setEnabled(value, this.getFieldEditorParent());
		integratedAuthEditor.setEnabled(value, this.getFieldEditorParent());
	}

	/**
	 * {@link #performDefaults()}
	 */
	@Override
	protected void performDefaults() {
		super.performDefaults();
		this.setControlAccess(this.enabledEditor.getBooleanValue());
		this.checkState();
	}
	
	/**
	 * {@link #propertyChange(PropertyChangeEvent)}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if(event.getSource().equals(enabledEditor)){
			setControlAccess(enabledEditor.getBooleanValue());
			this.checkState();
		}
		if(event.getSource().equals(integratedAuthEditor)){
			userEditor.loadDefault();
			pwdField.loadDefault();
			userEditor.setEnabled(!integratedAuthEditor.getBooleanValue(), this.getFieldEditorParent());
			pwdField.setEnabled(!integratedAuthEditor.getBooleanValue(), this.getFieldEditorParent());
			requiresValidation.setEnabled(true, this.getFieldEditorParent());
			this.checkState();
		}
		else if(event.getProperty().equals(FieldEditor.VALUE)) {
			if ( (event.getSource().equals(userEditor)) ||
					(event.getSource().equals(pwdField)) ||
					(event.getSource().equals(urlEditor))) {
				requiresValidation.setEnabled(true, this.getFieldEditorParent());
			}
			this.checkState();
		}		
	}
	
	/**
	 * {@link #checkState()}
	 */
	@Override
	protected void checkState() {
		super.checkState();
		if(this.isValid() && enabledEditor.getBooleanValue()) {

			if(0 == urlEditor.getStringValue().length()) {
				setErrorMessage("Application URL Is a required field");
				setValid(false);
				requiresValidation.setEnabled(false, this.getFieldEditorParent());
			}
			if(!urlEditor.getStringValue().endsWith("/")) {
				setErrorMessage("URL Must end with a /");
				setValid(false);
				requiresValidation.setEnabled(false, this.getFieldEditorParent());
			}			
			else if((!integratedAuthEditor.getBooleanValue()) && (0 == userEditor.getStringValue().length())) {
				setErrorMessage("Username is a required field");
				setValid(false);
				requiresValidation.setEnabled(false, this.getFieldEditorParent());
			}
			else if(requiresValidation.getValue()) {
				setErrorMessage("Connection Parameters Require Validation");
				setValid(false);				
			}
			else {
				setErrorMessage(null);
				setValid(true);				
			}
		}
		else if(this.isValid()){
			setErrorMessage(null);
			setValid(true);
		}
	}
	
	/**
	 * Validate the connection
	 * @author Jerry D. Odenwelder Jr.
	 *
	 */
	class ConnectionValidator implements Validator {

		public boolean isValid() {
			boolean rc = true;
			
			String url = urlEditor.getStringValue();

			V1APIConnector metaConnector = new V1APIConnector(url.toString() + V1Server.META_URL_SUFFIX);
			MetaModel model = new MetaModel(metaConnector);
			
			V1APIConnector dataConnector = null;
			if(integratedAuthEditor.getBooleanValue()) {
				dataConnector = new V1APIConnector(url.toString() + V1Server.DATA_URL_SUFFIX);
			}
			else {
				dataConnector = new V1APIConnector(url.toString() + V1Server.DATA_URL_SUFFIX, userEditor.getStringValue(), pwdField.getStringValue());
			}
			
			Services v1Service = new Services(model, dataConnector);
			
			try {
				V1Server.setMemberToken(getPreferenceStore(), v1Service);
			} catch (V1Exception e) {
				rc = false;
				Activator.logError(e);
				setErrorMessage("Validation Failed.");
			}
			return rc;
		}
	}	
}
