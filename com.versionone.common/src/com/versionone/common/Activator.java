package com.versionone.common;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.versionone.common.preferences.PreferenceConstants;
import com.versionone.common.preferences.PreferencePage;
import com.versionone.common.sdk.ApiDataLayer;
import com.versionone.common.sdk.ConnectionSettings;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.versionone.common";

    // The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }
    
    /**
     * connect to the VersionOne server with current user preference
     * @throws Exception
     */
    public static void connect() {
        final IPreferenceStore pref = PreferencePage.getPreferences(); 
        ConnectionSettings settings = new ConnectionSettings();
        settings.v1Path = pref.getString(PreferenceConstants.P_URL);
        settings.v1Username = pref.getString(PreferenceConstants.P_USER); 
        settings.v1Password = pref.getString(PreferenceConstants.P_PASSWORD);
        settings.isWindowsIntegratedAuthentication = pref.getBoolean(PreferenceConstants.P_INTEGRATED_AUTH);;
        settings.isProxyEnabled = pref.getBoolean(PreferenceConstants.P_PROXY_ENABLED);
        settings.proxyUri = pref.getString(PreferenceConstants.P_PROXY_URI);
        settings.proxyUsername = pref.getString(PreferenceConstants.P_PROXY_USER);
        settings.proxyPassword = pref.getString(PreferenceConstants.P_PROXY_PASSWORD);
        connect(settings);
        ApiDataLayer.getInstance().setCurrentProjectId(pref.getString(PreferenceConstants.P_PROJECT_TOKEN));
    }
    
    public static void connect(ConnectionSettings settings) {
        try {
            ApiDataLayer.getInstance().connect(settings);
        } catch (Exception e) {
            Activator.logError(e);
        }
        boolean showAllTask = PreferencePage.getPreferences().getBoolean(PreferenceConstants.P_ONLY_USER_WORKITEMS);
        ApiDataLayer.getInstance().setShowAllTasks(showAllTask);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    public static void logError(Throwable t) {
        logError("Unexpected Error", t);
    }

    public static void logError(String message, Throwable t) {
        log(IStatus.ERROR, IStatus.OK, message, t);
    }

    private static void log(int severity, int code, String message, Throwable t) {
        Activator.getDefault().getLog().log(createStatus(severity, code, message, t));

    }

    private static IStatus createStatus(int severity, int code, String message, Throwable t) {
        return new Status(severity, PLUGIN_ID, code, message, t);
    }
}
